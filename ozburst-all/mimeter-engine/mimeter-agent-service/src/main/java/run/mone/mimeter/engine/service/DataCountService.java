package run.mone.mimeter.engine.service;

import com.xiaomi.youpin.docean.anno.Service;
import common.Const;
import lombok.extern.slf4j.Slf4j;
import run.mone.event.Event;
import run.mone.mimeter.engine.agent.bo.task.HeraContextInfo;
import run.mone.mimeter.engine.agent.bo.stat.SceneTotalCountContext;
import run.mone.mimeter.engine.agent.bo.stat.SceneTotalCountContextDTO;
import run.mone.mimeter.engine.agent.bo.task.Task;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

import static common.Const.*;
import static common.Const.METRICS_TYPE_SCENE;
import static run.mone.mimeter.engine.agent.bo.stat.MetricLabelEnum.labelNameSlice;

@Service
@Slf4j
public class DataCountService {
    /**
     * 增量统计数据推送
     */
    public void pushTotalCountData(SceneTotalCountContext totalCountContext, boolean lastTime) {
        // 结束时最后推送一次统计数据
        if (lastTime) {
            totalCountContext.setLastTime(true);
        } else {
            //兼容，tps不应该是数组了,目前忽略了最后一次剩余的统计
            recordCurTpsAndRps(totalCountContext);
        }
        try {
            //推送数据统计事件
            SceneTotalCountContextDTO dto = copyToDTO(totalCountContext, lastTime);
            Event.ins().post(dto);
        } catch (Exception e) {
            log.warn("push count data error:{}", e.getMessage());
        }
    }

    /**
     * do copy
     */
    private SceneTotalCountContextDTO copyToDTO(SceneTotalCountContext totalCountContext, boolean last) {
        SceneTotalCountContextDTO dto = new SceneTotalCountContextDTO();
        //copy 基本信息
        dto.setReportId(totalCountContext.getReportId());
        dto.setSceneId(totalCountContext.getSceneId());
        dto.setSceneType(totalCountContext.getSceneType());
        dto.setTaskId(totalCountContext.getTaskId());
        dto.setAgentNum(totalCountContext.getAgentNum());
        dto.setDagTaskRps(totalCountContext.getDagTaskRps());
        dto.setRpsRate(totalCountContext.getRpsRate());
        dto.setConnectTaskNum(totalCountContext.getConnectTaskNum());
        dto.setLastTime(totalCountContext.isLastTime());
        //copy 总请求数
        synchronized (totalCountContext.getTotalReq()) {
            //请求总数
            dto.setTotalReq(totalCountContext.getTotalReq().longValue());
            totalCountContext.getTotalReq().reset();
            //总丢失连接数
            dto.setLossConnNum(totalCountContext.getLossConnNum().longValue());
            totalCountContext.getLossConnNum().reset();

            //copy 总事务数数
            dto.setTotalTCount(totalCountContext.getTotalTCount().longValue());
            totalCountContext.getTotalTCount().reset();
            //copy 总成功数
            dto.setTotalSuccReq(totalCountContext.getTotalSuccReq().longValue());
            totalCountContext.getTotalSuccReq().reset();
            //copy 总错误数
            dto.setTotalErrReq(totalCountContext.getTotalErrReq().longValue());
            totalCountContext.getTotalErrReq().reset();
        }

        synchronized (totalCountContext.getErrCounterMap()) {
            //copy 错误记录map
            ConcurrentHashMap<String, ConcurrentHashMap<Integer, Long>> tmpErrCounterMap = new ConcurrentHashMap<>();
            totalCountContext.getErrCounterMap().forEach((errFlag, map) -> {
                ConcurrentHashMap<Integer, Long> subMap = new ConcurrentHashMap<>();
                map.forEach((apiId, adder) -> subMap.put(apiId, adder.longValue()));
                tmpErrCounterMap.put(errFlag, subMap);
            });
            dto.setCounterMap(tmpErrCounterMap);
            if (last) {
                totalCountContext.getErrCounterMap().clear();
            } else {
                totalCountContext.getErrCounterMap().values().forEach(map -> map.forEach((apiId, value) -> value.reset()));
            }
        }

        synchronized (totalCountContext.getApiRtMap()) {
            //copy rt记录的map
            ConcurrentHashMap<Integer, ConcurrentHashMap<String, List<Integer>>> tmpApiRtAndTpsMap = new ConcurrentHashMap<>();
            totalCountContext.getApiRtMap().forEach((apiId, map) -> {
                ConcurrentHashMap<String, List<Integer>> subMap = new ConcurrentHashMap<>();
                map.forEach((labFlag, vList) -> subMap.put(labFlag, vList.stream().toList()));
                tmpApiRtAndTpsMap.put(apiId, subMap);
            });

            dto.setApiRtMap(tmpApiRtAndTpsMap);
            totalCountContext.getApiRtMap().clear();
        }
        synchronized (totalCountContext.getApiCountMap()) {
            //copy 接口级别统计次数
            ConcurrentHashMap<Integer, ConcurrentHashMap<String, Integer>> apiCountMap = new ConcurrentHashMap<>();
            totalCountContext.getApiCountMap().forEach((apiId, map) -> {
                ConcurrentHashMap<String, Integer> subMap = new ConcurrentHashMap<>();
                map.forEach((type, count) -> subMap.put(type, count.intValue()));
                apiCountMap.put(apiId, subMap);
            });
            dto.setApiCountMap(apiCountMap);

            if (last) {
                totalCountContext.getApiCountMap().clear();
            } else {
                totalCountContext.getApiCountMap().values().forEach(it -> {
                    it.get(API_REQ_TOTAL_T).reset();
                    it.get(API_REQ_TOTAL_R).reset();
                    it.get(API_REQ_SUCC).reset();
                    it.get(API_REQ_FAIL).reset();
                });
            }
        }

        synchronized (totalCountContext.getApiRpsMap()) {
            //copy api Rps
            ConcurrentHashMap<Integer, Integer> apiRpsMap = new ConcurrentHashMap<>();
            totalCountContext.getApiRpsMap().forEach((apiId, avg10Rps) -> apiRpsMap.put(apiId, avg10Rps.intValue()));
            dto.setApiRpsMap(apiRpsMap);
            totalCountContext.getApiRpsMap().clear();
        }
        synchronized (totalCountContext.getApiTpsMap()) {
            //copy api Tps
            ConcurrentHashMap<Integer, Integer> apiTpsMap = new ConcurrentHashMap<>();
            totalCountContext.getApiTpsMap().forEach((apiId, avg10Tps) -> apiTpsMap.put(apiId, avg10Tps.intValue()));
            dto.setApiTpsMap(apiTpsMap);
            totalCountContext.getApiTpsMap().clear();
        }

        return dto;
    }

    /**
     * 计算暂存当前的apis 10s 内tps
     */
    private void recordCurTpsAndRps(SceneTotalCountContext totalCountContext) {
        totalCountContext.getApiCountMap().forEach((apiId, countMap) -> {
            //计算10s 平均 tps
            ConcurrentHashMap<Integer, AtomicInteger> apiTpsMap = totalCountContext.getApiTpsMap();
            int avg10Tps = countMap.get(API_REQ_TOTAL_T).intValue() / PUSH_STAT_RATE;
            if (!apiTpsMap.containsKey(apiId)) {
                apiTpsMap.put(apiId, new AtomicInteger(avg10Tps));
            } else {
                apiTpsMap.get(apiId).set(avg10Tps);
            }
            //计算10s 平均 rps
            ConcurrentHashMap<Integer, AtomicInteger> apiRpsMap = totalCountContext.getApiRpsMap();
            int avg10Rps = countMap.get(API_REQ_TOTAL_R).intValue() / PUSH_STAT_RATE;
            if (!apiRpsMap.containsKey(apiId)) {
                apiRpsMap.put(apiId, new AtomicInteger(avg10Rps));
            } else {
                apiRpsMap.get(apiId).set(avg10Rps);
            }
        });
    }

    public void recordProms(LongAdder tpsCounter, LongAdder rpsCounter, Task task) {
        HeraContextInfo heraContextInfo = task.getHeraContextInfo();
        long rps;
        synchronized (rpsCounter) {
            rps = rpsCounter.longValue();
            rpsCounter.reset();
        }
        MetricsService.recordCounter(Const.METRICS_NAME_RPS, labelNameSlice(4), rps,
                String.valueOf(heraContextInfo.getSceneId()), heraContextInfo.getTaskFlag(), String.valueOf(heraContextInfo.getSerialLinkId()),
                METRICS_TYPE_SCENE);

        long tps;
        synchronized (tpsCounter) {
            tps = tpsCounter.longValue();
            tpsCounter.reset();
        }
        MetricsService.recordCounter(Const.METRICS_NAME_TPS, labelNameSlice(4), tps,
                String.valueOf(heraContextInfo.getSceneId()), heraContextInfo.getTaskFlag(), String.valueOf(heraContextInfo.getSerialLinkId()),
                METRICS_TYPE_SCENE);
    }
}
