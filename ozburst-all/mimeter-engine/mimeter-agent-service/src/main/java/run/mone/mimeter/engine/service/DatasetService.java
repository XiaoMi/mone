package run.mone.mimeter.engine.service;

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.Pair;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic.BasicTrafficList;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic.GetTrafficReq;
import com.xiaomi.youpin.tesla.traffic.recording.api.service.TrafficDubboService;
import common.Const;
import common.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.annotation.DubboReference;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.dataset.DatasetLineNum;
import run.mone.mimeter.dashboard.bo.dataset.DatasetLinesReq;
import run.mone.mimeter.dashboard.service.DatasetInfoSercice;
import run.mone.mimeter.engine.agent.bo.data.DubboData;
import run.mone.mimeter.engine.agent.bo.data.HttpData;
import run.mone.mimeter.engine.agent.bo.data.CommonReqInfo;
import run.mone.mimeter.engine.agent.bo.data.ReqParamType;
import run.mone.mimeter.engine.agent.bo.task.PullApiTrafficReq;
import run.mone.mimeter.engine.agent.bo.task.Task;
import run.mone.mimeter.engine.agent.bo.task.TaskType;
import run.mone.mimeter.engine.bo.DataMapCache;
import run.mone.mimeter.engine.filter.preFilter.filters.TrafficFilter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static common.Const.*;

/**
 * 数据源服务
 */
@Service
@Slf4j
public class DatasetService {

    private final Gson gson = Util.getGson();

    private static final Pattern EL_PATTERN = Pattern.compile("\\$\\{([^}]*)}");

    /**
     * <"reportId",<countLinkNum,dataMap>>
     */
    private final ConcurrentHashMap<String, DataMapCache> datasetCache = new ConcurrentHashMap<>();

    @DubboReference(check = false, group = "${mimeter.dashboard.dubbo.group}", version = "${mimeter.dashboard.dubbo.version}", interfaceClass = DatasetInfoSercice.class, timeout = 10000)
    private DatasetInfoSercice datasetInfoSercice;

    @DubboReference(check = false, group = "${traffic.dubbo.group}", interfaceClass = TrafficDubboService.class, timeout = 10000)
    private TrafficDubboService trafficDubboService;

    /**
     * 加载录制的流量数据
     */
    public void loadTrafficData(Task task) {
        try {
            List<PullApiTrafficReq> pullApiTrafficReqs = task.getTrafficToPullConfList().getApiTrafficReqList();

            //批量加载流量数据的请求列表
            List<GetTrafficReq> getTrafficReqList = new ArrayList<>();

            pullApiTrafficReqs.forEach(pullApiTrafficReq -> {
                GetTrafficReq getTrafficReq = new GetTrafficReq();
                getTrafficReq.setRecordingConfigId(pullApiTrafficReq.getTrafficConfigId());
                getTrafficReq.setUrl(pullApiTrafficReq.getUrl());
                getTrafficReq.setStartTime(pullApiTrafficReq.getFromTime());
                getTrafficReq.setEndTime(pullApiTrafficReq.getToTime());
                getTrafficReqList.add(getTrafficReq);
            });
            //获取每个conf的流量记录条数<confId,trafficSize>
            Map<Integer, Integer> confTrafficSizeMap = trafficDubboService.getTrafficRecordCountList(getTrafficReqList).getData();

            //更新每个conf请求的拉取数据范围
            getTrafficReqList.forEach(getTrafficReq -> {
                if (!confTrafficSizeMap.containsKey(getTrafficReq.getRecordingConfigId())) {
                    //must be bug
                    log.warn("load traffic size failed!");
                } else {
                    //该配置项当前筛选范围的流量总条数
                    int totalSize = confTrafficSizeMap.get(getTrafficReq.getRecordingConfigId());
                    //当前页数与大小根据压测机集群机器总数及本机器索引位置计算,
                    //例如 共5台发压机，流量总量10000，本机 index 为1，则page为2,pageSize为2000
                    int page = task.getAgentIndex() + 1;
                    int pageSize = totalSize / task.getAgentNum();
                    //最多允许 1w 行
                    if (pageSize > AGENT_MAX_TRAFFIC_SIZE) {
                        pageSize = AGENT_MAX_TRAFFIC_SIZE;
                    }
                    getTrafficReq.setPage(page);
                    getTrafficReq.setPageSize(pageSize);
                }
            });
            //拉取具体流量数据列表
            List<BasicTrafficList> basicTrafficLists = trafficDubboService.getBasicTrafficList(getTrafficReqList).getData();

            //更新filter内的缓存流量数据
            TrafficFilter.updateTrafficCache(basicTrafficLists);
        } catch (Exception e) {
            log.error("DatasetService loadTrafficData error:{}", e.getMessage());
        }
    }

    public TreeMap<String, List<String>> loadAndInitParamDataset(Task task) {

        if (datasetCache.get(task.getReportId()) != null && !task.isDebug()) {
            return datasetCache.get(task.getReportId()).getDataMap();
        } else {
            //每台发压机随机取一部分请求数据
            int agentNum = task.getAgentNum();

            log.info("loadAndInitParamDataset agent num:{}",agentNum);

            //该机器索引位置
            int index = task.getAgentIndex();

            log.info("loadAndInitParamDataset agent index:{}",index);

            List<DatasetLinesReq> reqList = new ArrayList<>();
            // 从 dashboard 获取该场景下数据源文件名及对应行数
            List<DatasetLineNum> datasetLineNums = datasetInfoSercice.getLineNumBySceneId(task.getSceneId()).getData();
            log.info("loadAndInitParamDataset datasetLineNums :{}",gson.toJson(datasetLineNums));

            datasetLineNums.forEach(datasetLineNum -> {
                DatasetLinesReq req = new DatasetLinesReq();
                req.setDatasetId(datasetLineNum.getDatasetId());
                req.setFileUrl(datasetLineNum.getFileUrl());
                req.setFileKsKey(datasetLineNum.getFileKsKey());
                req.setDefaultParamName(datasetLineNum.getDefaultParamName());
                long lineNum = datasetLineNum.getFileRaw();
                if (datasetLineNum.getIgnoreFirstLine()) {
                    lineNum -= 1;
                }
                double range = (double) (lineNum / agentNum);
                if (range < 1.0) {
                    range = 1.0;
                }
                if (isInt(range)) {
                    req.setFrom(index * (int) range);
                    req.setTo((index + 1) * (int) range);
                } else {
                    req.setFrom(index * (int) range);
                    req.setTo((index + 1) * (int) range + 1);
                }

                reqList.add(req);
            });
            Stopwatch dsw = Stopwatch.createStarted();

            Result<TreeMap<String, List<String>>> dataRes;
            try {
                log.info("loadAndInitParamDataset getDatasetMap :{}",gson.toJson(reqList));

                dataRes = datasetInfoSercice.getDatasetMap(reqList);
            } catch (Exception e) {
                log.error("getDatasetMap faild,cause by:{}", e.getMessage(),e);
                return new TreeMap<>();
            }
            log.debug("call dubbo data task {} load dataset use time:{}", task.getId(), dsw.elapsed(TimeUnit.MILLISECONDS));
            if (dataRes == null || dataRes.getData() == null || dataRes.getData().isEmpty()) {
                log.error("get dataset error ,data is empty,reqList:{}", Arrays.toString(reqList.toArray()));
                return new TreeMap<>();
            }

            //每次压测只拉取缓存一次
            if (!task.isDebug()) {
                //调试任务直接拉取，不缓存
                datasetCache.putIfAbsent(task.getReportId(), new DataMapCache(new AtomicInteger(1), dataRes.getData()));
            }
            return dataRes.getData();
        }
    }


    /**
     * 数据过大，二分切分重拉
     */
//    private void reverseAppendData(TreeMap<String, List<String>> totalRes, List<DatasetLinesReq> reqList, run.mone.mibench.dashboard.bo.common.Result<TreeMap<String, List<String>>> dataRes) {
//        if (dataRes.getCode() == Const.DUBBO_DATA_TOO_LONG_ERR_CODE) {
//            List<DatasetLinesReq> reqListFront = new ArrayList<>();
//            List<DatasetLinesReq> reqListBack = new ArrayList<>();
//
//            for (DatasetLinesReq req : reqList) {
//                DatasetLinesReq frontReq = new DatasetLinesReq();
//                BeanUtils.copyProperties(req, frontReq);
//                DatasetLinesReq backReq = new DatasetLinesReq();
//                BeanUtils.copyProperties(req, backReq);
//
//                int from = req.getFrom();
//                int to = req.getTo();
//
//                frontReq.setFrom(from);
//                frontReq.setTo((to + from) / 2);
//                reqListFront.add(frontReq);
//
//                backReq.setFrom((to + from) / 2 + 1);
//                backReq.setTo(to);
//                reqListBack.add(backReq);
//            }
//
//            run.mone.mibench.dashboard.bo.common.Result<TreeMap<String, List<String>>> dataResFront = datasetInfoSercice.getDatasetMap(reqListFront);
//            reverseAppendData(totalRes, reqListFront, dataResFront);
//
//            run.mone.mibench.dashboard.bo.common.Result<TreeMap<String, List<String>>> dataResBack = datasetInfoSercice.getDatasetMap(reqListBack);
//            reverseAppendData(totalRes, reqListBack, dataResBack);
//
//        } else {
//            TreeMap<String, List<String>> tempData = dataRes.getData();
//            tempData.keySet().forEach(key -> {
//                if (totalRes.containsKey(key)) {
//                    totalRes.get(key).addAll(tempData.get(key));
//                } else {
//                    totalRes.put(key, tempData.get(key));
//                }
//            });
//        }
//    }

    /**
     * @param currentTask 当前任务
     * @param lineFlag    读取的行数
     */
    public CommonReqInfo processTaskParam(Task currentTask, int lineFlag, boolean isDebug) throws CloneNotSupportedException {
        log.debug("processTaskParam lineFlag :{}, dataMap:{}",lineFlag,currentTask.getDataMap());

        CommonReqInfo commonReqInfo = new CommonReqInfo();
        TreeMap<String, List<String>> dataMap = currentTask.getDataMap();
        //替换数据源参数
        if (currentTask.getType() == TaskType.http) {
            HttpData httpData = currentTask.getHttpData().clone();
            //替换路径参数
            if (httpData.getUrl().contains("${")) {
                processDatasetUrl(httpData, dataMap, lineFlag);
            }
            commonReqInfo.setDebugUrl(httpData.getUrl());
            //http场景
            if (httpData.getMethod().equalsIgnoreCase(Const.HTTP_GET)) {
                //get请求
                List<Object> tmpList = new ArrayList<>(httpData.getParamNum());
                IntStream.range(0, httpData.getParamNum()).forEach(i -> {
                    String paramValue = httpData.getParams().get(i).toString();
                    tmpList.add(getValue(paramValue, dataMap, lineFlag));
                });
//                commonReqInfo.setGetOrFormParamsList(tmpList);
                commonReqInfo.setQueryParamMap(httpData.httpGetTmpParams(tmpList));
                if (currentTask.isDebug()) {
                    commonReqInfo.setParamsType(ReqParamType.Http_Get.code);
                    commonReqInfo.setParamJson(gson.toJson(httpData.httpGetTmpParams(tmpList)));
                }
            } else if (httpData.getMethod().equalsIgnoreCase(Const.HTTP_POST)) {
                if (httpData.getContentType().equals(CONTENT_TYPE_APP_FORM) || httpData.getContentType().equals(CONTENT_TYPE_APP_FORM2)) {
                    //post 表单，处理方式同get
                    List<Object> tmpList = new ArrayList<>(httpData.getParamNum());

                    IntStream.range(0, httpData.getParamNum()).forEach(i -> {
                        String paramValue = httpData.getParams().get(i).toString();
                        tmpList.add(getValue(paramValue, dataMap, lineFlag));
                    });
                    commonReqInfo.setQueryParamMap(httpData.httpGetTmpParams(tmpList));
                    if (currentTask.isDebug()) {
                        commonReqInfo.setParamsType(ReqParamType.Http_Post_Form.code);
                        commonReqInfo.setParamJson(gson.toJson(httpData.httpGetTmpParams(tmpList)));
                    }
                } else {
                    //post json  先转json处理，后续考虑是否优化
                    String body = httpData.getJsonParam().get();
//                    if (currentTask.getHttpData().getUrl().contains("proretail-api.nr.mi.com/api/nstrade/mobile/customer/queryCustomerInfo")){
//                        log.info("final before json body:{},time:{}", body, System.currentTimeMillis());
//                    }
//                    使用完成后恢复为源参数体
//                    httpData.recoverJsonParam();
                    Matcher m = EL_PATTERN.matcher(body);
                    while (m.find()) {
                        String expr = m.group(1);
                        Object targetV;
                        String v = getDataValue(expr, dataMap, lineFlag);
                        if (NumberUtils.isNumber(v)) {
                            if (isNumeric(v)) {
                                targetV = Long.parseLong(v);
                            } else {
                                targetV = Double.parseDouble(v);
                            }
                        } else {
                            targetV = v;
                        }
//                        log.info("final target v:{}", targetV);
                        if (targetV != null && StringUtils.isNotEmpty(targetV.toString())){
                            body = Util.Parser.parse$(expr, body, targetV);
                        }
                    }
//                    log.info("final after json body:{}", body);
                    httpData.setPostParamJson(body);
                    commonReqInfo.setParamsType(ReqParamType.Http_Post_Json.code);
                    commonReqInfo.setParamJson(body);
                }
            }
            //替换header参数
            Map<String, String> tmpHeaders = new HashMap<>();
            for (Map.Entry<String, String> headerMap :
                    httpData.getHeaders().entrySet()) {
                tmpHeaders.put(headerMap.getKey(), getValue(headerMap.getValue(), dataMap, lineFlag));
            }
            commonReqInfo.setHeaders(tmpHeaders);
        } else if (currentTask.getType() == TaskType.dubbo) {
            DubboData dubboData = currentTask.getDubboData();
            String body = dubboData.getJsonParam().get();
//            dubboData.recoverJsonParam();
            Matcher m = EL_PATTERN.matcher(body);
            while (m.find()) {
                String expr = m.group(1);
                Object targetV;
                String v = getDataValue(expr, dataMap, lineFlag);
                if (NumberUtils.isNumber(v)) {
                    if (isNumeric(v)) {
                        targetV = Long.parseLong(v);
                    } else {
                        targetV = Double.parseDouble(v);
                    }
                } else {
                    targetV = v;
                }
                if (targetV != null && StringUtils.isNotEmpty(targetV.toString())){
                    body = Util.Parser.parse$(expr, body, targetV);
                }
            }
            commonReqInfo.setParamsType(ReqParamType.Dubbo.code);
            commonReqInfo.setParamJson(body);
        }
        return commonReqInfo;
    }

    private void processDatasetUrl(HttpData httpData, TreeMap<String, List<String>> dataMap, int n) {
        if (dataMap == null) {
            return;
        }
        String k;
        String v = null;
        String url = httpData.getUrl();
        String tmpUrl = url;
        if (null != url && url.length() > 0) {
            Matcher m = EL_PATTERN.matcher(url);
            while (m.find()) {
                try {
                    k = m.group(1);
                    if (dataMap.get(k) != null && dataMap.get(k).size() != 0) {
                        int line = n % dataMap.get(k).size();
                        v = dataMap.get(k).get(line);
                    }
                    try {
                        if (v != null) {
                            tmpUrl = Util.Parser.parse$(k, tmpUrl, v);
                        }
                    } catch (Exception e) {
                        log.error("processDatasetUrl error,k:{}, v:{}, error:{}", k, v, e);
                    }
                } catch (Exception e) {
                    log.error("processDatasetUrl 2 error, v:{}, error:{}", v, e);
                }
            }
        }
        httpData.setUrl(tmpUrl);
    }

    private String getValue(String key, TreeMap<String, List<String>> dataMap, int n) {
        if (dataMap == null) {
            return key;
        }
        //适配 ${key} 格式
        if (key.startsWith("${")) {
            Pair<String, String> pair = getElKey(key, dataMap, n);
            return pair.getValue();
        }
        return key;
    }

    private static Pair<String, String> getElKey(String key, TreeMap<String, List<String>> dataMap, int n) {
        String k = null;
        String v = null;
        if (null != key && key.length() > 0) {
            Matcher m = EL_PATTERN.matcher(key);

            if (m.find()) {
                k = m.group(1);
                if (dataMap.get(k) != null && dataMap.get(k).size() != 0) {
                    int line = n % dataMap.get(k).size();
                    v = dataMap.get(k).get(line);
                }
            }
        }
        if (v == null) {
            v = k;
        }
        return Pair.of(k, v);
    }

    private static String getDataValue(String key, TreeMap<String, List<String>> dataMap, int n) {
        log.debug("getDataValue lineFlag :{}, dataMap:{}",n,dataMap);
        if (dataMap == null) {
            return "";
        }
        if (null != key && key.length() > 0) {
            if (dataMap.get(key) == null) {
                return "";
            }
            log.debug("getDataValue size :{}",dataMap.get(key).size());

            if (dataMap.get(key).size() != 0) {
                int line = n % dataMap.get(key).size();
                log.debug("getDataValue line :{},val:{}",line,dataMap.get(key).get(line));

                return dataMap.get(key).get(line);
            }
        }
        return "";
    }

    public void processDataMapCache(Task task) throws InterruptedException {
        if (datasetCache.get(task.getReportId()) != null) {
            int currFinLinkNum = datasetCache.get(task.getReportId()).getCountFinLinkNum().addAndGet(1);
            if (currFinLinkNum == task.getConnectTaskNum()) {
                //最后一条链路完成，延迟100s清理该次压测的缓存数据源
                Thread.sleep(100 * 1000);
                datasetCache.remove(task.getReportId());
            }
        }
    }

    private static boolean isInt(double num) {
        return Math.abs(num - Math.round(num)) < Double.MIN_VALUE;
    }

    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57)
                return false;
        }
        return true;
    }
}

