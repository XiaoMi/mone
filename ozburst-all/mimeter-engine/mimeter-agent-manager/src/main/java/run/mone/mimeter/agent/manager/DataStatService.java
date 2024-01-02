package run.mone.mimeter.agent.manager;

import com.xiaomi.faas.func.api.PrometheusService;
import com.xiaomi.faas.func.domain.MimeterApiDetailRes;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.annotation.DubboReference;
import run.mone.mimeter.dashboard.bo.common.EmitterTypeEnum;
import run.mone.mimeter.dashboard.bo.statistics.ApiStatistics;
import run.mone.mimeter.dashboard.bo.statistics.ErrorTypeAnalysis;
import run.mone.mimeter.dashboard.bo.statistics.TotalStatAnalysisEvent;
import run.mone.mimeter.dashboard.service.BenchBroadcastService;
import run.mone.mimeter.engine.agent.bo.data.AgentReq;
import run.mone.mimeter.engine.agent.bo.stat.SceneTotalCountContextDTO;
import run.mone.mimeter.engine.agent.bo.stat.TotalCounterStatistic;
import run.mone.mimeter.engine.agent.bo.task.DagTaskRps;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

import static common.Const.*;
import static common.Const.API_REQ_FAIL;

@Service
@Slf4j
public class DataStatService {

    @DubboReference(check = false, group = "${mimeter.dashboard.dubbo.group}", version = "${mimeter.dashboard.dubbo.version}", interfaceClass = BenchBroadcastService.class, timeout = 20000)
    private BenchBroadcastService benchBroadcastService;

    /**
     * 监控打点数据服务
     */
    @DubboReference(check = false, interfaceClass = PrometheusService.class, timeout = 5000)
    private PrometheusService prometheusService;

    /**
     * <"reportId",{}>
     */
    private final ConcurrentMap<String, TotalCounterStatistic> totalCountStatistic = new ConcurrentHashMap<>();

    public void processTotalCountCtxEvent(AgentReq req) throws InterruptedException {
        //处理回调过来的返回数据
        SceneTotalCountContextDTO totalCountContextDTO = req.getTotalCountContextDTO();

        //get p95 p99 data from remote
        Map<Integer, Integer> p95Map = new HashMap<>();
        Map<Integer, Integer> p99Map = new HashMap<>();
        try {
            Result pRtData = prometheusService.getMimeterApiDetail(String.valueOf(totalCountContextDTO.getSceneId()), totalCountContextDTO.getSceneType());
            log.debug("get remote p99 p95,scene id:{},type:{}, data :{}", totalCountContextDTO.getSceneId(), totalCountContextDTO.getSceneType(), pRtData.getData());
            if (pRtData.getCode() == 0 || pRtData.getData() != null) {
                MimeterApiDetailRes res = (MimeterApiDetailRes) pRtData.getData();
                if (res.getP95() != null) {
                    res.getP95().forEach((k, v) -> {
                        if (k.contains(":")) {
                            Integer apiId = Integer.valueOf(k.substring(k.indexOf(":") + 1));
                            double value = Double.parseDouble(v);
                            p95Map.put(apiId, (int) value);
                        }
                    });
                }
                if (res.getP99() != null) {
                    res.getP99().forEach((k, v) -> {
                        if (k.contains(":")) {
                            Integer apiId = Integer.valueOf(k.substring(k.indexOf(":") + 1));
                            double value = Double.parseDouble(v);
                            p99Map.put(apiId, (int) value);
                        }
                    });
                }
            }
        } catch (Exception e) {
            log.error("get p99 and p95 data from remote failed,cause by:{}", e.getMessage());
        }
        log.debug("get p99 p95 data p95:{},p99:{}", p95Map, p99Map);

        if (totalCountStatistic.containsKey(totalCountContextDTO.getReportId())) {

            //总记录中存在本压测任务
            //合并数据 错误数据、rt、tps数据
            //通知
            mergeTotalStatistic(totalCountContextDTO);
            notifyTotalStatistic(totalCountContextDTO.getReportId(), p95Map, p99Map, false);
            if (totalCountContextDTO.isLastTime()) {
                //该压测机结束
                TotalCounterStatistic statistic = totalCountStatistic.get(totalCountContextDTO.getReportId());
                int finishNum = statistic.getFinishAgentNum().addAndGet(1);
                if (finishNum >= totalCountContextDTO.getConnectTaskNum() * totalCountContextDTO.getAgentNum()) {
                    //所有机器执行完成
                    notifyTotalStatistic(totalCountContextDTO.getReportId(), p95Map, p99Map, true);
                    //清除该任务记录
                    totalCountStatistic.remove(totalCountContextDTO.getReportId());
                }
            }
        } else {
            totalCountStatistic.put(totalCountContextDTO.getReportId(), new TotalCounterStatistic(new AtomicInteger(), new LongAdder(), new LongAdder(), new LongAdder(),
                    new LongAdder(), new LongAdder(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>()));
            //总记录中存在本压测任务
            //合并数据
            //通知
            mergeTotalStatistic(totalCountContextDTO);
            notifyTotalStatistic(totalCountContextDTO.getReportId(), p95Map, p99Map, false);
        }
    }

    /**
     * 合并总体统计数据
     */
    private void mergeTotalStatistic(SceneTotalCountContextDTO countContextDTO) {
        String reportId = countContextDTO.getReportId();
        TotalCounterStatistic statistic = this.totalCountStatistic.get(reportId);

        //场景发压比例
        statistic.setRpsRate(countContextDTO.getRpsRate());

        //合并总请求数
        statistic.getTotalReq().add(countContextDTO.getTotalReq());

        //合并总处理数
        statistic.getTotalTCount().add(countContextDTO.getTotalTCount());

        //丢失连接数
        statistic.getLossConnNum().add(countContextDTO.getLossConnNum());

        //更新链路rps
        updateLinkRps(countContextDTO, statistic);

        //合并更新错误统计数据
        mergeErrData(countContextDTO, statistic);

        //合并更新rt、tps相关数据
        mergeRtAndTpsData(countContextDTO, statistic);

        //合并更新各接口统计数据
        mergeApiCountData(countContextDTO, statistic);

        //合并完后替换原先数据
        totalCountStatistic.put(reportId, statistic);
    }

    private void updateLinkRps(SceneTotalCountContextDTO countContextDTO, TotalCounterStatistic statistic) {
        if (statistic.getReportLinkRps() != null) {
            DagTaskRps dagTaskRps = countContextDTO.getDagTaskRps();
            if (statistic.getReportLinkRps().get(dagTaskRps.getLinkId()) != null) {
                statistic.getReportLinkRps().get(dagTaskRps.getLinkId()).setRps(dagTaskRps.getRps());
            } else {
                statistic.getReportLinkRps().put(dagTaskRps.getLinkId(), dagTaskRps);
            }
        }
    }

    /**
     * 合并统计错误数据
     */
    private void mergeErrData(SceneTotalCountContextDTO countContextDTO, TotalCounterStatistic statistic) {
        //合并总错误次数
        statistic.getTotalErrReq().add(countContextDTO.getTotalErrReq());
        //合并总错误次数
        statistic.getTotalSuccReq().add(countContextDTO.getTotalSuccReq());

        //合并错误统计
        //errKey:s_code_errorCode   countMap:<apiId,count>
        //例:<"s_code_404",<2342,10>>  or <"cp_id_141242",<2342,10>>
        countContextDTO.getCounterMap().forEach((errKey, countMap) -> {
            if (statistic.getCounterMap().containsKey(errKey)) {
                //已存在该错误类型
                countMap.forEach((apiId, count) -> {
                    if (statistic.getCounterMap().get(errKey).containsKey(apiId)) {
                        //已记录该api
                        statistic.getCounterMap().get(errKey).get(apiId).add(count);
                    } else {
                        //尚未记录该api
                        LongAdder longAdder = new LongAdder();
                        longAdder.add(count);
                        statistic.getCounterMap().get(errKey).put(apiId, longAdder);
                    }
                });
            } else {
                //尚不存在该类错误
                ConcurrentHashMap<Integer, LongAdder> tmpMap = new ConcurrentHashMap<>();
                countMap.forEach((k, v) -> {
                    LongAdder tmpAdder = new LongAdder();
                    tmpAdder.add(v);
                    tmpMap.put(k, tmpAdder);
                });
                statistic.getCounterMap().put(errKey, tmpMap);
            }
        });
    }

    /**
     * 合并接口中rt、tps、rps 数据
     */
    private void mergeRtAndTpsData(SceneTotalCountContextDTO countContextDTO, TotalCounterStatistic statistic) {
        //合并接口中rt、tps数据
        //所有接口本次的rt汇总
        CopyOnWriteArrayList<Integer> tmpAllRt = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<Integer> tmpAllTps = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<Integer> tmpAllRps = new CopyOnWriteArrayList<>();

        countContextDTO.getApiRtMap().forEach((apiId, typeMap) -> {
            if (statistic.getApiRtAndTpsMap().containsKey(apiId)) {
                //已存在该api的记录
                AtomicInteger apiAvgRt = new AtomicInteger();
                final int[] apiMaxRt = {0};
                ConcurrentHashMap<String, AtomicInteger> apiMap = statistic.getApiRtAndTpsMap().get(apiId);
                typeMap.forEach((type, list) -> {
                    if (type.equals(RT_LIST)) {
                        //rt 列表
                        tmpAllRt.addAll(list);
                        //之前存储的平均值
                        int oriAvgRt = apiMap.get(AVG_RT).get();
                        AtomicInteger tmpApiTotalRt = new AtomicInteger();
                        list.forEach(apiRt -> {
                            tmpApiTotalRt.addAndGet(apiRt);
                            if (apiRt >= apiMaxRt[0]) {
                                apiMaxRt[0] = apiRt;
                            }
                        });
                        //接口平均rt
                        if (list.size() != 0) {
                            if (oriAvgRt != 0) {
                                apiAvgRt.set((tmpApiTotalRt.get() + oriAvgRt) / (list.size() + 1));
                            } else {
                                apiAvgRt.set(tmpApiTotalRt.get() / list.size());
                            }
                            //更新接口平均rt
                            apiMap.get(AVG_RT).set(apiAvgRt.get());
                        }
                        //更新接口最大rt
                        if (apiMaxRt[0] > apiMap.get(MAX_RT).get()) {
                            apiMap.get(MAX_RT).set(apiMaxRt[0]);
                        }
                    }
                });
                //平均rps
                if (!countContextDTO.isLastTime()) {
                    int apiAvgRps;
                    int tmpCount = (countContextDTO.getApiRpsMap().get(apiId)) * countContextDTO.getAgentNum();
                    if (apiMap.get(AVG_RPS).get() != 0) {
                        apiAvgRps = (tmpCount + apiMap.get(AVG_RPS).get()) / 2;
                    } else {
                        apiAvgRps = tmpCount;
                    }
                    apiMap.get(AVG_RPS).set(apiAvgRps);

                    if (tmpCount != 0) {
                        tmpAllRps.add(tmpCount);
                    }
                    //最大rps
                    if (apiAvgRps > apiMap.get(MAX_RPS).get()) {
                        apiMap.get(MAX_RPS).set(apiAvgRps);
                    }
                    //平均tps
                    int apiAvgTps;
                    int tmpTCount = (countContextDTO.getApiTpsMap().get(apiId)) * countContextDTO.getAgentNum();
                    if (apiMap.get(AVG_TPS).get() != 0) {
                        apiAvgTps = (tmpTCount + apiMap.get(AVG_TPS).get()) / 2;
                    } else {
                        apiAvgTps = tmpTCount;
                    }
                    apiMap.get(AVG_TPS).set(apiAvgTps);

                    if (tmpTCount != 0) {
                        tmpAllTps.add(tmpTCount);
                    }
                    //最大tps
                    if (apiAvgTps > apiMap.get(MAX_TPS).get()) {
                        apiMap.get(MAX_TPS).set(apiAvgTps);
                    }
                }
            } else {
                //初始化
                ConcurrentHashMap<String, AtomicInteger> apiMap = new ConcurrentHashMap<>();
                apiMap.put(AVG_RT, new AtomicInteger(0));
                apiMap.put(MAX_RT, new AtomicInteger(0));
                apiMap.put(AVG_TPS, new AtomicInteger(0));
                apiMap.put(MAX_TPS, new AtomicInteger(0));
                apiMap.put(MAX_RPS, new AtomicInteger(0));
                apiMap.put(AVG_RPS, new AtomicInteger(0));
                statistic.getApiRtAndTpsMap().put(apiId, apiMap);
            }
        });
        //场景平均rt
        int avgRt;
        //最大rt
        AtomicInteger maxRt = new AtomicInteger();
        AtomicInteger totalRt = new AtomicInteger(0);
        tmpAllRt.forEach(rt -> {
            totalRt.addAndGet(rt);
            if (rt >= maxRt.get()) {
                maxRt.set(rt);
            }
        });
        if (tmpAllRt.size() != 0) {
            if (statistic.getAvgRt() != 0) {
                avgRt = (totalRt.get() + statistic.getAvgRt()) / (tmpAllRt.size() + 1);
            } else {
                avgRt = totalRt.get() / tmpAllRt.size();
            }
            //更新平均rt
            statistic.setAvgRt(avgRt);
        }
        //更新最大rt
        if (maxRt.get() >= statistic.getMaxRt()) {
            statistic.setMaxRt(maxRt.get());
        }

        if (!countContextDTO.isLastTime()) {
            //场景平均rps
            int avgRps;
            AtomicInteger maxRps = new AtomicInteger();
            AtomicInteger totalRps = new AtomicInteger();
            if (tmpAllRps.size() != 0) {
                tmpAllRps.forEach(rps -> {
                    totalRps.addAndGet(rps);
                    if (rps > maxRps.get()) {
                        maxRps.set(rps);
                    }
                });

                int tmpAvgRps = totalRps.get() / tmpAllRps.size();

                if (statistic.getAvgRps() != 0) {
                    avgRps = (statistic.getAvgRps() + tmpAvgRps) / 2;
                } else {
                    avgRps = tmpAvgRps;
                }
                statistic.setAvgRps(avgRps);
            }
            //更新最大rps
            if (maxRps.get() > statistic.getMaxRps()) {
                statistic.setMaxRps(maxRps.get());
            }

            //场景平均tps
            int avgTps;
            AtomicInteger maxTps = new AtomicInteger();
            AtomicInteger totalTps = new AtomicInteger();
            if (tmpAllTps.size() != 0) {
                tmpAllTps.forEach(tps -> {
                    totalTps.addAndGet(tps);
                    if (tps > maxTps.get()) {
                        maxTps.set(tps);
                    }
                });

                int tmpAvgTps = totalTps.get() / tmpAllTps.size();

                if (statistic.getAvgTps() != 0) {
                    avgTps = (statistic.getAvgTps() + tmpAvgTps) / 2;
                } else {
                    avgTps = tmpAvgTps;
                }
                statistic.setAvgTps(avgTps);
            }

            //更新最大tps
            if (maxTps.get() > statistic.getMaxTps()) {
                statistic.setMaxTps(maxTps.get());
            }
        }
    }

    /**
     * 合并各接口统计数据
     */
    private void mergeApiCountData(SceneTotalCountContextDTO countContextDTO, TotalCounterStatistic statistic) {
        ConcurrentHashMap<Integer, ConcurrentHashMap<String, Integer>> apiCountMap = countContextDTO.getApiCountMap();
        apiCountMap.forEach((apiId, countMap) -> {
            if (statistic.getApiCountMap().containsKey(apiId)) {
                //已记录该接口数据
                countMap.forEach((type, count) -> statistic.getApiCountMap().get(apiId).get(type).add(count));
            } else {
                //未记录，初始化第一次
                ConcurrentHashMap<String, LongAdder> apiAdderMap = new ConcurrentHashMap<>();
                countMap.forEach((type, count) -> {
                    LongAdder longAdder = new LongAdder();
                    longAdder.add(count);
                    apiAdderMap.put(type, longAdder);
                });
                statistic.getApiCountMap().put(apiId, apiAdderMap);
            }
        });
    }

    /**
     * 推送错误统计数据
     */
    private void notifyTotalStatistic(String reportId, Map<Integer, Integer> p95Map, Map<Integer, Integer> p99Map, boolean finish) throws InterruptedException {
        if (!totalCountStatistic.containsKey(reportId)) {
            return;
        }
        if (finish) {
            //等下同时间推送的事件merge完成
            Thread.sleep(100);
        }
        TotalCounterStatistic totalCounterStatistic = totalCountStatistic.get(reportId);

        TotalStatAnalysisEvent analysisEvent = new TotalStatAnalysisEvent();
        analysisEvent.setFinish(finish);
        int totalCount = totalCounterStatistic.getTotalReq().intValue();
        int totalSuccCount = totalCounterStatistic.getTotalSuccReq().intValue();
        int totalErrCount = totalCounterStatistic.getTotalErrReq().intValue();

        //链路rps
        ConcurrentHashMap<Integer,DagTaskRps> innerRpsMap = totalCounterStatistic.getReportLinkRps();
        Map<Integer, run.mone.mimeter.dashboard.bo.task.DagTaskRps> dagTaskRpsMap = new HashMap<>();
        innerRpsMap.forEach((linkId,rpsObj) -> dagTaskRpsMap.putIfAbsent(linkId,new run.mone.mimeter.dashboard.bo.task.DagTaskRps(linkId,rpsObj.getTaskId(), rpsObj.getRps())));
        analysisEvent.setLinkToDagTaskRpsMap(dagTaskRpsMap);

        //当前发压比例
        analysisEvent.setRpsRate(totalCounterStatistic.getRpsRate());
        //总请求数
        analysisEvent.setTotalReq(totalCount);

        analysisEvent.setLossConnNum(totalCounterStatistic.getLossConnNum().intValue());
        //业务总处理次数
        analysisEvent.setTotalTCount(totalCounterStatistic.getTotalTCount().intValue());
        //总成功数
        analysisEvent.setTotalSuccReq(totalSuccCount);
        //总错误数
        analysisEvent.setTotalErrReq(totalErrCount);
        //总平均rt
        analysisEvent.setAvgRt(totalCounterStatistic.getAvgRt());
        //总最大rt
        analysisEvent.setMaxRt(totalCounterStatistic.getMaxRt());
        //总平均tps
        analysisEvent.setAvgTps(totalCounterStatistic.getAvgTps());
        //总最大tps
        analysisEvent.setMaxTps(totalCounterStatistic.getMaxTps());
        //总最大rps
        analysisEvent.setMaxRps(totalCounterStatistic.getMaxRps());
        //总平均rps
        analysisEvent.setAvgRps(totalCounterStatistic.getAvgRps());

        double totalErrRate = 0;
        if (totalCount != 0) {
            totalErrRate = (1.0 * totalErrCount / totalCount) * 100d;
        }

        analysisEvent.setTotalErrRate(format2(totalErrRate));

        double totalSuccRate = 0;
        if (totalCount != 0) {
            totalSuccRate = (1.0 * totalSuccCount / totalCount) * 100d;
        }
        analysisEvent.setTotalSuccRate(format2(totalSuccRate));

        List<ErrorTypeAnalysis> errorTypeAnalyses = new ArrayList<>();
        totalCounterStatistic.getCounterMap().forEach((errFlag, apiMap) -> {
            ErrorTypeAnalysis analysis = new ErrorTypeAnalysis();

            String[] flagAndCode = errFlag.split("_", 2);

            if (errFlag.startsWith(ERR_STATUS_CODE_PREFIX)) {
                //错误状态码
                analysis.setErrorType(ERR_STATUS_CODE_TYPE);
                analysis.setErrorCode(Integer.parseInt(flagAndCode[1]));

            } else if (errFlag.startsWith(ERR_CHECKPOINT_PREFIX)) {
                //检查点规则
                analysis.setErrorType(ERR_CHECKPOINT_TYPE);
                //检查点id
                analysis.setCheckPointId(Integer.valueOf(flagAndCode[1]));
            } else if (errFlag.equals(ERR_STATUS_DUBBO_PREFIX)) {
                //检查点规则
                analysis.setErrorType(ERR_DUBBO_CALL_TYPE);
                //检查点id
                analysis.setCheckPointId(500);
            }
            //本错误总数
            AtomicInteger thisErrTotalCount = new AtomicInteger();
            AtomicInteger mostErrApiId = new AtomicInteger();
            final int[] tmpMostErrApiCount = {0};
            Map<Integer, Integer> errInApis = new HashMap<>();
            apiMap.forEach((apiId, count) -> {
                errInApis.put(apiId, count.intValue());
                thisErrTotalCount.addAndGet(count.intValue());

                if (count.intValue() >= tmpMostErrApiCount[0]) {
                    tmpMostErrApiCount[0] = count.intValue();
                    mostErrApiId.set(apiId);
                }
            });
            //本错误占比
            double thisErrRate = 0;
            if (totalErrCount != 0) {
                thisErrRate = (1.0 * thisErrTotalCount.get() / totalErrCount) * 100d;
            }

            analysis.setErrRate(format2(thisErrRate));
            analysis.setMostErrApi(mostErrApiId.get());
            analysis.setErrInApis(errInApis);

            errorTypeAnalyses.add(analysis);
        });
        //错误分析数据
        analysisEvent.setErrorTypeAnalyses(errorTypeAnalyses);

        List<ApiStatistics> apiStatisticsList = new ArrayList<>();
        ConcurrentHashMap<Integer, ConcurrentHashMap<String, LongAdder>> apiCountMaps = totalCounterStatistic.getApiCountMap();

        totalCounterStatistic.getApiRtAndTpsMap().forEach((apiId, apiMap) -> {
            ApiStatistics apiStatistics = new ApiStatistics();
            apiStatistics.setApiId(apiId);
            apiStatistics.setAvgRt(apiMap.getOrDefault(AVG_RT, new AtomicInteger(0)).get());
            apiStatistics.setMaxRt(apiMap.getOrDefault(MAX_RT, new AtomicInteger(0)).get());
            //remote 的两个数据p95 p99
            apiStatistics.setP95Rt(p95Map.getOrDefault(apiId, 0));
            apiStatistics.setP99Rt(p99Map.getOrDefault(apiId, 0));

            apiStatistics.setAvgTps(apiMap.getOrDefault(AVG_TPS, new AtomicInteger(0)).get());
            apiStatistics.setMaxTps(apiMap.getOrDefault(MAX_TPS, new AtomicInteger(0)).get());
            apiStatistics.setMaxRps(apiMap.getOrDefault(MAX_RPS, new AtomicInteger(0)).get());
            apiStatistics.setAvgRps(apiMap.getOrDefault(AVG_RPS, new AtomicInteger(0)).get());

            ConcurrentHashMap<String, LongAdder> apiCountMap = apiCountMaps.get(apiId);
            if (apiCountMap != null) {
                int apiTotal = apiCountMap.getOrDefault(API_REQ_TOTAL_R, new LongAdder()).intValue();
                int apiTransTotal = apiCountMap.getOrDefault(API_REQ_TOTAL_T, new LongAdder()).intValue();
                int apiSucc = apiCountMap.getOrDefault(API_REQ_SUCC, new LongAdder()).intValue();
                int apiFail = apiCountMap.getOrDefault(API_REQ_FAIL, new LongAdder()).intValue();
                apiStatistics.setReqTotal(apiTotal);
                apiStatistics.setTansTotal(apiTransTotal);
                apiStatistics.setReqSucc(apiSucc);
                apiStatistics.setReqFail(apiFail);

                double apiSuccRate = 0;
                if (apiTotal != 0) {
                    apiSuccRate = (1.0 * apiSucc / apiTotal) * 100d;
                }
                apiStatistics.setSuccRate(format2(apiSuccRate));
            }
            apiStatisticsList.add(apiStatistics);
        });
        analysisEvent.setApiStatisticsList(apiStatisticsList);
        //推送错误统计数据
        this.benchBroadcastService.notifyEvent(EmitterTypeEnum.TOTAL_STAT_ANALYSIS, reportId, analysisEvent);
    }

    public static String format2(double value) {
        /*
         * %.2f % 表示 小数点前任意位数 2 表示两位小数 格式后的结果为 f 表示浮点型
         */
        return new Formatter().format("%.2f", value).toString();
    }


}
