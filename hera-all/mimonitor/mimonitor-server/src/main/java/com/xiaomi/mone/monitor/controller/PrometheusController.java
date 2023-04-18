package com.xiaomi.mone.monitor.controller;

import com.xiaomi.mone.app.api.model.HeraAppBaseInfoModel;
import com.xiaomi.mone.log.api.model.dto.TraceLogDTO;
import com.xiaomi.mone.log.api.model.vo.TraceLogQuery;
import com.xiaomi.mone.log.api.service.LogDataService;
import com.xiaomi.mone.monitor.controller.model.PromQueryRangeParam;
import com.xiaomi.mone.monitor.controller.model.TimeUnit;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.AppAlarmService;
import com.xiaomi.mone.monitor.service.HeraBaseInfoService;
import com.xiaomi.mone.monitor.service.es.EsService;
import com.xiaomi.mone.monitor.service.model.PageData;
import com.xiaomi.mone.monitor.service.model.prometheus.MetricDetailQuery;
import com.xiaomi.mone.monitor.service.model.prometheus.MiLogQuery;
import com.xiaomi.mone.monitor.service.prometheus.AlarmService;
import com.xiaomi.mone.monitor.service.prometheus.MetricSuffix;
import com.xiaomi.mone.monitor.service.prometheus.PrometheusService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author gaoxihui
 * @date 2021/8/9 5:39 下午
 */
@Slf4j
@RestController
public class PrometheusController {

    @Autowired
    PrometheusService prometheusService;

    @Autowired
    AlarmService alarmService;

    @Reference(check = false, interfaceClass = LogDataService.class, timeout = 5000)
    LogDataService logDataService;
    
    @Autowired
    EsService esService;

    @Value("${esIndex}")
    String esIndex;

    @Autowired
    AppAlarmService appAlarmService;

    @Autowired
    HeraBaseInfoService heraBaseInfoService;

    @Value("${server.type}")
    private String env;


    @ResponseBody
    @PostMapping("/prometheus/queryRange")
    public Result<PageData> queryRange(@RequestBody PromQueryRangeParam param){

        log.info("PrometheusController.queryRange request param : {} ",param.toString());

        Long startTime = param.getStartTime() != null ? param.getStartTime()/1000 : (System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000) / 1000L; //默认查询最近7天

        Long endTime = param.getEndTime()!=null ? param.getEndTime()/1000 : System.currentTimeMillis() / 1000L;

        Long step = param.getStep() != null ? param.getStep() : (endTime - startTime)/2; // 默认为查询的时间区间，即step = 查询区间/2

        String projectName = new StringBuilder().append(param.getProjectId()).append("_").append(param.getProjectName()).toString();

        MetricSuffix metricSuffix = MetricSuffix.getByName(param.getMetricSuffix()) != null ? MetricSuffix.getByName(param.getMetricSuffix()) : MetricSuffix._count;

        log.info("PrometheusController.queryRange request afterConvert Param startTime : {} ,endTime : {} ,step : {},projectName : {},metricSuffix : {}",startTime,endTime,step,projectName,metricSuffix);

        return prometheusService.queryRange(param.getMetric(),param.getLabels(),projectName, metricSuffix.name(),startTime,endTime,step,param.getOp(),param.getValue());

    }

    @ResponseBody
    @PostMapping("/prometheus/querySumOverTime")
    public Result<PageData> querySumOverTime(@RequestBody PromQueryRangeParam param){

        log.info("PrometheusController.queryRange request param : {} ",param.toString());

        Long startTime = param.getStartTime() != null ? param.getStartTime()/1000 : (System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000) / 1000L; //默认查询最近7天

        Long endTime = param.getEndTime()!=null ? param.getEndTime()/1000 : System.currentTimeMillis() / 1000L;

        Long duration = endTime - startTime;

        Long step = param.getStep() != null ? param.getStep() : duration; // 默认为查询的时间区间，即step = 查询区间

        String projectName = new StringBuilder().append(param.getProjectId()).append("_").append(param.getProjectName()).toString();

        MetricSuffix metricSuffix = MetricSuffix.getByName(param.getMetricSuffix()) != null ? MetricSuffix.getByName(param.getMetricSuffix()) : MetricSuffix._count;

        log.info("PrometheusController.queryRange request afterConvert Param startTime : {} ,endTime : {} ,step : {},projectName : {},metricSuffix : {}",startTime,endTime,step,projectName,metricSuffix);

        String pDuration = duration + "s";
        return  prometheusService.queryRangeSumOverTime(param.getMetric(),param.getLabels(),projectName, metricSuffix.name(),startTime,endTime,step,pDuration);
    }

    @ResponseBody
    @PostMapping("/prometheus/queryCountOverTime")
    public Result<PageData> queryCountOverTime(@RequestBody PromQueryRangeParam param){

        Long startTime = param.getStartTime() != null ? param.getStartTime()/1000 : (System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000) / 1000L; //默认查询最近7天

        Long endTime = param.getEndTime()!=null ? param.getEndTime()/1000 : System.currentTimeMillis() / 1000L;

        Long duration = endTime - startTime;

        Long step = param.getStep() != null ? param.getStep() : duration; // 默认为查询的时间区间，即step = 查询区间

        String projectName = new StringBuilder().append(param.getProjectId()).append("_").append(param.getProjectName()).toString();

        MetricSuffix metricSuffix = MetricSuffix.getByName(param.getMetricSuffix()) != null ? MetricSuffix.getByName(param.getMetricSuffix()) : MetricSuffix._count;

        log.info("PrometheusController.queryRange request afterConvert Param startTime : {} ,endTime : {} ,step : {},projectName : {},metricSuffix : {}",startTime,endTime,step,projectName,metricSuffix);

        String pDuration = duration + "s";
        return  prometheusService.queryCountOverTime(param.getMetric(),param.getLabels(),projectName, metricSuffix.name(),startTime,endTime,step,pDuration);
    }

    @ResponseBody
    @PostMapping("/prometheus/queryIncrease")
    public Result<PageData> queryIncrease(@RequestBody PromQueryRangeParam param){

        Long startTime = param.getStartTime() != null ? param.getStartTime()/1000 : (System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000) / 1000L; //默认查询最近7天

        Long endTime = param.getEndTime()!=null ? param.getEndTime()/1000 : System.currentTimeMillis() / 1000L;

        Long duration = endTime - startTime;

        Long step = param.getStep() != null ? param.getStep() : duration; // 默认为查询的时间区间，即step = 查询区间

        String projectName = new StringBuilder().append(param.getProjectId()).append("_").append(param.getProjectName()).toString();

        MetricSuffix metricSuffix = MetricSuffix.getByName(param.getMetricSuffix()) != null ? MetricSuffix.getByName(param.getMetricSuffix()) : MetricSuffix._count;

        log.info("PrometheusController.queryIncrease request Param startTime : {} ,endTime : {} ,step : {},projectName : {},metricSuffix : {}",startTime,endTime,step,projectName,metricSuffix);

        String pDuration = duration + "s";

        Result<PageData> pageDataResult = prometheusService.queryRangeSumOverTime(param.getMetric(), param.getLabels(), projectName, metricSuffix.name(), startTime, endTime, step, pDuration);


        return pageDataResult;
    }


    @ResponseBody
    @PostMapping("/prometheus/instanceIpList")
    public Result<List<String>> instanceIpList(@RequestBody PromQueryRangeParam param){

        List<String> instanceIpList = alarmService.getInstanceIpList(param.getProjectId(),param.getProjectName());
        log.info("instanceIpList param:{},instanceIpList:{}",param,instanceIpList);

        return Result.success(!CollectionUtils.isEmpty(instanceIpList) ? instanceIpList.stream().distinct().collect(Collectors.toList()) : instanceIpList);
    }

    @ResponseBody
    @PostMapping("/prometheus/httpClientDomainList")
    public Result<List<String>> httpClientDomainList(@RequestBody PromQueryRangeParam param){

        List<String> httpClientDomainList = alarmService.getHttpClientServerDomain(param.getProjectId(),param.getProjectName());
        log.info("httpClientDomainList param:{},httpClientDomainList:{}",param,httpClientDomainList);

        return Result.success(!CollectionUtils.isEmpty(httpClientDomainList) ? httpClientDomainList.stream().distinct().collect(Collectors.toList()) : httpClientDomainList);
    }

    @ResponseBody
    @PostMapping("/prometheus/serverEnvList")
    public Result<Map> serverEnvList(@RequestBody PromQueryRangeParam param){

        Map map = alarmService.getEnvIpMapping(param.getProjectId(),param.getProjectName());
        log.info("instanceIpList param:{},map:{}",param,map);

        return Result.success(map);
    }

    @ResponseBody
    @PostMapping("/prometheus/detail")
    public Result<PageData> detail(@RequestBody MetricDetailQuery param){
        log.info("PrometheusController detail param : {}",param);

        try {
            String esIndexName = esIndex;

            if(param.getAppSource() == null){
                HeraAppBaseInfoModel byBindIdAndName = heraBaseInfoService.getByBindIdAndName(String.valueOf(param.getProjectId()), param.getProjectName());
                if(byBindIdAndName != null){
                    log.info("metric detail param no app source found! reset by db value : {},param:{}",byBindIdAndName.getPlatformType(),param);
                    param.setAppSource(byBindIdAndName.getPlatformType());
                }else{
                    log.info("metric detail param no app source found! and no db info found! use china area index! param:{}",param);
                }
            }

            return esService.query(esIndexName, param, param.getPage(), param.getPageSize());
        } catch (Exception e) {
            log.error("PrometheusController.detail Error" + e.getMessage(),e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @ResponseBody
    @PostMapping("/prometheus/logInfo")
    public Result<PageData> logInfo(@RequestBody MiLogQuery param){

        TraceLogQuery query = new TraceLogQuery(param.getProjectId(),param.getServerIp(),param.getTraceId());
//        query.setGenerationTime(param.getGenerationTime());
//        query.setLevel(param.getLevel());
        try {
            PageData pd = new PageData();
            TraceLogDTO traceLog = logDataService.getTraceLog(query);

            log.info("getTraceLog param : {},result:{}",param,traceLog.toString());

            Set<String> dataList = traceLog.getDataList();
            if(CollectionUtils.isEmpty(dataList)){
                return Result.success(pd);
            }

            pd.setTotal(Long.valueOf(dataList.size()));
            pd.setList(dataList);
            return Result.success(pd);
        } catch (Exception e) {
            log.error("PrometheusController.logInfo Error" + e.getMessage(),e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    //获取tesla错误大盘的数据
    @GetMapping("/prometheus/getTeslaError")
    public Result<PageData> getTeslaError(String department,String area) {
            if (StringUtils.isEmpty(department) || StringUtils.isEmpty(area)) {
                return Result.fail(ErrorCode.unknownError);
            }
            return prometheusService.getTeslaError(department,area);

    }

    private Long convertStartTime(PromQueryRangeParam param){
        if(param.getTimeDuration() == null || param.getTimeDuration().intValue() <= 0 || StringUtils.isEmpty(param.getTimeUnit())){
            log.info("PrometheusController.convertQueryTime invalid timeDuration or timeUnit param! param : {} " , param);
            return null;
        }

        TimeUnit timeUnit = TimeUnit.getByName(param.getTimeUnit());
        if(timeUnit == null ){
            log.info("PrometheusController.convertQueryTime no allowed timeUnit! timeUnit : {} " , param.getTimeUnit());
            return null;
        }
        switch (timeUnit){
            case m:
                return System.currentTimeMillis()/1000 - param.getTimeDuration() * 60;
            case h:
                return System.currentTimeMillis()/1000 - param.getTimeDuration() * 60 * 60;
            case d:
                return System.currentTimeMillis()/1000 - param.getTimeDuration() * 60 * 60 * 24;
            default:
                return null;
        }

    }

    //获取服务的QPS接口
    @GetMapping("/api/prometheus/getServiceQps")
    public Result getServiceQps(String serviceName,String type) {
        if (StringUtils.isEmpty(serviceName)) {
            return Result.fail(ErrorCode.unknownError);
        }
        return prometheusService.getServiceQps(serviceName,type);
    }

    //获取dubbo服务的service列表  (type = http/dubbo/dubboConsumer/grpcServer/grpcClient/thriftServer/thriftClient/apusClient/apusServer)
    @GetMapping("/api/prometheus/getDubboServiceList")
    public Result getDubboServiceList(String serviceName,String type,String startTime,String endTime) {
        if (StringUtils.isEmpty(serviceName) || StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime) || Long.parseLong(startTime) > Long.parseLong(endTime)) {
            return Result.fail(ErrorCode.success);
        }
        //默认dubbo
        if (StringUtils.isEmpty(type)) {
            type = "dubbo";
        }
        return prometheusService.queryDubboServiceList(serviceName,type,startTime,endTime);
    }

//    @GetMapping("/api-manual/redis/switch")
//    public Result redisSwitch(String value) {
//        RedisHelper.redisSwitch = value;
//        return Result.success("ok!");
//    }

    @GetMapping("/api/prometheus/oracle")
    public Result oracle(String mode,String type){
        prometheusService.oracle(mode,type);
        return Result.success("test");
    }


}
