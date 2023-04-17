package com.xiaomi.mone.monitor.service;

import com.google.common.collect.Lists;
import com.xiaomi.mone.monitor.bo.ReqErrorMetrics;
import com.xiaomi.mone.monitor.bo.ReqSlowMetrics;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.helper.AlertHelper;
import com.xiaomi.mone.monitor.service.helper.RedisHelper;
import com.xiaomi.mone.monitor.service.model.AppMonitorRequest;
import com.xiaomi.mone.monitor.service.model.PageData;
import com.xiaomi.mone.monitor.service.model.ProjectInfo;
import com.xiaomi.mone.monitor.service.model.prometheus.Metric;
import com.xiaomi.mone.monitor.service.model.prometheus.MetricKind;
import com.xiaomi.mone.monitor.service.model.redis.AppAlarmData;
import com.xiaomi.mone.monitor.service.prometheus.MetricSuffix;
import com.xiaomi.mone.monitor.service.prometheus.PrometheusService;
import com.xiaomi.mone.monitor.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author gaoxihui
 * @date 2021/8/17 10:08 上午
 */
@Slf4j
@Service
public class ComputeTimerService {

    private final ThreadPoolExecutor executor;

    private final ThreadPoolExecutor metricExecutor;

    @Autowired
    PrometheusService prometheusService;
//    @Autowired
//    private RedisHelper redisHelper;
    @Autowired
    private AlertHelper alertHelper;

    public ComputeTimerService() {
        executor = new ThreadPoolExecutor(10, 50, 5, TimeUnit.MINUTES, new LinkedBlockingQueue(100),
                (Runnable r) -> new Thread(r, "compute-execute-thread-v2"), new ThreadPoolExecutor.CallerRunsPolicy());

        metricExecutor = new ThreadPoolExecutor(50, 100, 5, TimeUnit.MINUTES, new LinkedBlockingQueue(1000),
                (Runnable r) -> new Thread(r, "compute-metric-thread-v2"), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @PreDestroy
    public void destory() {
        executor.shutdownNow();
        metricExecutor.shutdownNow();
    }

    public Result<List<AppAlarmData>> getProjectStatistics(AppMonitorRequest param) {
        final Long endTime = CommonUtil.toSeconds(System.currentTimeMillis());
        final Long startTime = endTime - param.getDuration();
        final String timeDurarion = param.getDuration() + "s";
        final Long step = param.getDuration();
        int projectSize = param.getProjectList().size() > 50 ? 50 : param.getProjectList().size();
        List<Callable<AppAlarmData>> callList = new ArrayList<>();
        for (int idx = 0; idx < projectSize; idx++) {
            final ProjectInfo project = param.getProjectList().get(idx);
            if (project.getId() == null || StringUtils.isBlank(project.getName()) || project.getIamTreeId() == null) {
                continue;
            }
            callList.add(new Callable() {
                @Override
                public AppAlarmData call() throws Exception {
                    return getAppAlarmData(project, startTime, endTime, timeDurarion, step, param);
                }
            });
        }
        List<AppAlarmData> dataList = new ArrayList<>();
        try {
            List<Future<AppAlarmData>> futures = executor.invokeAll(callList, 30, TimeUnit.SECONDS);
            if (!CollectionUtils.isEmpty(futures)) {
                AppAlarmData data = null;
                for (Future<AppAlarmData> future : futures) {
                    data = future.get();
                    if (data == null) {
                        continue;
                    }
                    dataList.add(data);
                }
            }
        } catch (Exception e) {
            log.info("ComputeTimerServiceV2.executor.invokeAll异常  param={}, ", param, e);
        }
        return Result.success(dataList);
    }

    /**
     *
     * @param project
     * @param startTime
     * @param endTime
     * @param timeDurarion
     * @param param
     */
    public AppAlarmData getAppAlarmData(ProjectInfo project, Long startTime, Long endTime, String timeDurarion, Long step, AppMonitorRequest param) {
        String appName = new StringBuilder().append(project.getId()).append("_").append(project.getName().replaceAll("-","_")).toString();

        AppAlarmData data = null;
//        AppAlarmData data = redisHelper.getAppAlarmData(appName);
//        if (data != null) {
//            log.info("ComputeTimerServiceV2.getAppAlarmData cache-result appName={}, data={}", appName, data);
//            return data;
//        }

        AppAlarmData.AppAlarmDataBuilder dataBuilder = AppAlarmData.builder();
        dataBuilder.id(project.getId()).name(project.getName()).iamTreeId(project.getIamTreeId());
        dataBuilder.startTime(startTime).endTime(endTime);
        List<MetricKind> kinds = Lists.newArrayList(MetricKind.http,MetricKind.dubbo, MetricKind.grpc, MetricKind.apus, MetricKind.thrift, MetricKind.db, MetricKind.redis);
        List<Callable<Void>> callList = kinds.stream().map(kind -> {
            return new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    computByMetricType(param, appName, kind, dataBuilder,startTime,endTime,timeDurarion, step);
                    return null;
                }
            };
        }).collect(Collectors.toList());
        try {
            List<Future<Void>> futureList = metricExecutor.invokeAll(callList);
            if (param == null) {
                dataBuilder.alertTotal(alertHelper.queryAlertEventNum(project.getId().intValue(), project.getIamTreeId().intValue(), startTime, endTime));
            }
            futureList.stream().forEach(f -> {
                try {
                    f.get();
                } catch (Throwable e) {
                    log.warn("metric统计查询异步get结果异常", project.getName(), e);
                }
            });
        } catch (Throwable e) {
            log.error("metric统计异步查询异常 projectName={}", project.getName(), e);
        }
        dataBuilder.logExceptionNum(0);
        data = dataBuilder.build();
        calExceptionTotal(data);
        calSlowQueryTotal(data);
        log.info("ComputeTimerServiceV2.getAppAlarmData new-result appName={}, data={}", appName, data);
//        redisHelper.setAppAlarmData(appName, data);
        return data;
    }

    private void computByMetricType(AppMonitorRequest param, String appName,MetricKind metricKind, AppAlarmData.AppAlarmDataBuilder dataBuilder,Long startTime, Long endTime, String timeDurarion, Long step){
        try {
            //当前页面
            MetricKind.MetricType curMetricType = null;
            if (param != null) {
                curMetricType = MetricKind.getMetricTypeByCode(param.getMetricType());
            }
            switch (metricKind){
                case http:

                    // http请求异常统计
                    Result<PageData> httpExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.httpError.getCode(),getLable(MetricKind.MetricType.http_exception, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.httpExceptionNum(countRecordMetric(httpExceptions));

                    // httpClient请求异常统计
                    Result<PageData> httpClientExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.httpClientError.getCode(), getLable(MetricKind.MetricType.http_client_exception, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.httpClientExceptionNum(countRecordMetric(httpClientExceptions));

                    // http请求慢查询统计
                    Result<PageData> httpSlowQuery = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.httpSlowQuery.getCode(),getLable(MetricKind.MetricType.http_slow, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.httpSlowNum(countRecordMetric(httpSlowQuery));

                    // httpClient请求慢查询统计
                    Result<PageData> httpClientSlowQuerys = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.httpClientSlowQuery.getCode(), getLable(MetricKind.MetricType.http_client_slow_query, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.httpClientSlowNum(countRecordMetric(httpClientSlowQuerys));

                    break;

                case dubbo:

                    // dubbo请求异常统计
                    Result<PageData> dubboExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.dubboConsumerError.getCode(), getLable(MetricKind.MetricType.dubbo_consumer_exception, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.dubboExceptionNum(countRecordMetric(dubboExceptions));
                    // dubbo请求异常统计
                    Result<PageData> dubboPExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.dubboProvider.getCode(), getLable(MetricKind.MetricType.dubbo_provider_exception, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.dubboPExceptionNum(countRecordMetric(dubboPExceptions));
                    // dubbo consumer慢请求统计
                    Result<PageData> dubboConsumerSlowQuerys = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.dubboConsumerSlowQuery.getCode(), getLable(MetricKind.MetricType.dubbo_consumer_slow_query, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.dubboCSlowQueryNum(countRecordMetric(dubboConsumerSlowQuerys));
                    log.info("projectName:{},dubboConsumerSlowQuerys:{}",appName,dubboConsumerSlowQuerys);
                    // dubbo provider慢请求统计
                    Result<PageData> dubboProviderSlowQuerys = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.dubboProviderSlowQuery.getCode(), getLable(MetricKind.MetricType.dubbo_provider_slow_query, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.dubboProviderSlowQueryNum(countRecordMetric(dubboProviderSlowQuerys));
                    log.info("projectName:{},dubboProviderSlowQuerys:{}",appName,dubboProviderSlowQuerys);

                    break;

                case grpc :
                    // grpc请求异常统计
                    Result<PageData> grpcServerExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.grpcServerError.getCode(), getLable(MetricKind.MetricType.grpc_server_exception, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.grpcServerErrorNum(countRecordMetric(grpcServerExceptions));

                    Result<PageData> grpcClientExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.grpcClientError.getCode(), getLable(MetricKind.MetricType.grpc_client_exception, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.grpcClientErrorNum(countRecordMetric(grpcClientExceptions));

                    Result<PageData> grpcClientSlowQuery = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.grpcClientSlowQuery.getCode(), getLable(MetricKind.MetricType.grpc_client_slow_query, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.grpcClientSlowQueryNum(countRecordMetric(grpcClientSlowQuery));

                    Result<PageData> grpcServerSlowQuery = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.grpcServerSlowQuery.getCode(), getLable(MetricKind.MetricType.grpc_server_slow_query, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.grpcServerSlowQueryNum(countRecordMetric(grpcServerSlowQuery));

                    break;

                case apus :

                    // apus请求异常统计
                    Result<PageData> apusServerExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.apusServerError.getCode(), getLable(MetricKind.MetricType.apus_server_exception, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.apusServerErrorNum(countRecordMetric(apusServerExceptions));

                    Result<PageData> apusClientExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.apusClientError.getCode(), getLable(MetricKind.MetricType.apus_client_exception, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.apusClientErrorNum(countRecordMetric(apusClientExceptions));

                    Result<PageData> apusClientSlowQuery = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.apusClientSlowQuery.getCode(), getLable(MetricKind.MetricType.apus_client_slow_query, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.apusClientErrorNum(countRecordMetric(apusClientSlowQuery));

                    Result<PageData> apusServerSlowQuery = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.apusServerSlowQuery.getCode(), getLable(MetricKind.MetricType.apus_server_slow_query, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.apusServerSlowQueryNum(countRecordMetric(apusServerSlowQuery));

                    break;


                case thrift :

                    // thrift请求异常统计
                    Result<PageData> thriftServerExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.thriftServerError.getCode(), getLable(MetricKind.MetricType.thrift_server_exception, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.thriftServerErrorNum(countRecordMetric(thriftServerExceptions));

                    Result<PageData> thriftClientExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.thriftClientError.getCode(), getLable(MetricKind.MetricType.thrift_client_exception, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.thriftClientErrorNum(countRecordMetric(thriftClientExceptions));

                    Result<PageData> thriftClientSlowQuery = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.thriftClientSlowQuery.getCode(),  getLable(MetricKind.MetricType.thrift_client_slow_query, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.thriftClientSlowQueryNum(countRecordMetric(thriftClientSlowQuery));

                    Result<PageData> thriftServerSlowQuery = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.thriftServerSlowQuery.getCode(),  getLable(MetricKind.MetricType.thrift_server_slow_query, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.thriftServerSlowQueryNum(countRecordMetric(thriftServerSlowQuery));

                    break;

                case db :
                    // mysql请求异常统计
                    Result<PageData> sqlExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.dbError.getCode(), getLable(MetricKind.MetricType.db_exception, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.sqlExceptionNum(countRecordMetric(sqlExceptions));
                    // mysql慢请求统计
                    Result<PageData> sqlSlowQuerys = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.dbSlowQuery.getCode(), getLable(MetricKind.MetricType.db_slow_query, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.sqlSlowQueryNum(countRecordMetric(sqlSlowQuerys));

                    // oracle请求异常统计
                    Result<PageData> oracleExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.oracleException.getCode(), getLable(MetricKind.MetricType.oracle_exception, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.oracleExceptionNum(countRecordMetric(oracleExceptions));

                    // oracle慢请求统计
                    Result<PageData> oracleSlowQuerys = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.oracleSlow.getCode(), getLable(MetricKind.MetricType.oracle_slow_query, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.oracleSlowNum(countRecordMetric(oracleSlowQuerys));
                    log.info("projectName:{},sqlSlowQuerys:{},oracleSlowQuerys:{}",appName,sqlSlowQuerys,oracleSlowQuerys);

                    // es请求异常统计
                    Result<PageData> esExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.esException.getCode(), getLable(MetricKind.MetricType.es_exception, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.esExceptionNum(countRecordMetric(esExceptions));

                    // es慢请求统计
                    Result<PageData> esSlowQuerys = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.esSlow.getCode(), getLable(MetricKind.MetricType.es_slow_query, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.esSlowNum(countRecordMetric(esSlowQuerys));

                    break;

                case redis :
                    // redis请求异常统计
                    Result<PageData> redisExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.redisError.getCode(), getLable(MetricKind.MetricType.redis_exception, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.redisExceptionNum(countRecordMetric(redisExceptions));

                    Result<PageData> redisSlowQuerys = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.redisSlow.getCode(), getLable(MetricKind.MetricType.redis_slow, curMetricType, param), appName, MetricSuffix._total.name(), startTime, endTime, step, timeDurarion);
                    dataBuilder.redisSlowNum(countRecordMetric(redisSlowQuerys));
                    break;

                default:
                    log.error("invalid metric kind assign! metricType:{}",metricKind);
                    break;

            }
        } catch (Exception e) {
            log.error("ComputeTimerServiceV2.getAppAlarmData error! appName={}", appName, e);
        }
    }

    public AppAlarmData countAppMetricData(AppMonitorRequest param) {
        ProjectInfo project = param.getProjectList().get(0);
        Long startTime = param.getStartTimeCurrent();
        Long endTime = param.getEndTimeCurrent();
        Long step = endTime - startTime;
        return getAppAlarmData(project, startTime, endTime, step + "s", step, param);
    }


    private Map<String,String> getLable(MetricKind.MetricType metricTypeTarget,MetricKind.MetricType metricTypeParam,AppMonitorRequest param){
        if(metricTypeTarget == null || metricTypeParam == null || param == null){
            return null;
        }
        return metricTypeTarget == metricTypeParam ? getLabelByMetricType(param.getMetricType(), param.getMethodName()) : null;
    }

    private Map<String,String> getLabelByMetricType(String metricType,String methodName){
        if(StringUtils.isBlank(metricType)){
            return null;
        }
        MetricKind metricKind = MetricKind.getByMetricType(metricType);
        return getLabelByMetricKind(metricKind,methodName);
    }

    private Map<String,String> getLabelByMetricKind(MetricKind metricKind,String methodName){
        if(metricKind == null){
            return null;
        }
        Map<String,String> labels = new HashMap<>();
        labels.put(metricKind.getLebelName(),methodName);
        return labels;
    }

    private Integer countRecordMetric(Result<PageData> result) {
        if (ErrorCode.success.getCode() != result.getCode()) {
            log.error("ComputeTimerService.countRecordMetric error! result : {}", result.toString());
            return 0;
        }
        PageData data = result.getData();
        List<Metric> metrics = (List<Metric>) data.getList();
        if (CollectionUtils.isEmpty(metrics)) {
            return 0;
        }

        Integer ret = 0;
        for (Metric metric : metrics) {
            ret++ ;
        }
        log.info("ComputeTimerService.countRecordMetric ret : {}", ret);
        return ret;
    }

    /**
     * 计算异常总数
     * @return
     */
    private void calExceptionTotal(AppAlarmData data) {
        Integer exceptionTotal = 0;
        exceptionTotal += Optional.ofNullable(data.getHttpExceptionNum()).orElse(0);
        exceptionTotal += Optional.ofNullable(data.getHttpClientExceptionNum()).orElse(0);
        exceptionTotal += Optional.ofNullable(data.getDubboExceptionNum()).orElse(0);
        exceptionTotal += Optional.ofNullable(data.getDubboPExceptionNum()).orElse(0);
        exceptionTotal += Optional.ofNullable(data.getSqlExceptionNum()).orElse(0);
        exceptionTotal += Optional.ofNullable(data.getOracleExceptionNum()).orElse(0);
        exceptionTotal += Optional.ofNullable(data.getRedisExceptionNum()).orElse(0);
        exceptionTotal += Optional.ofNullable(data.getGrpcServerErrorNum()).orElse(0);
        exceptionTotal += Optional.ofNullable(data.getGrpcClientErrorNum()).orElse(0);
        exceptionTotal += Optional.ofNullable(data.getApusServerErrorNum()).orElse(0);
        exceptionTotal += Optional.ofNullable(data.getApusClientErrorNum()).orElse(0);
        exceptionTotal += Optional.ofNullable(data.getThriftServerErrorNum()).orElse(0);
        exceptionTotal += Optional.ofNullable(data.getThriftClientErrorNum()).orElse(0);
        exceptionTotal += Optional.ofNullable(data.getEsExceptionNum()).orElse(0);
        data.setExceptionTotal(exceptionTotal);
    }
    /**
     * 计算慢查询总数
     * @return
     */
    private void calSlowQueryTotal(AppAlarmData data) {
        Integer slowQueryTotal = 0;
        slowQueryTotal += Optional.ofNullable(data.getHttpSlowNum()).orElse(0);
        slowQueryTotal += Optional.ofNullable(data.getHttpClientSlowNum()).orElse(0);
        slowQueryTotal += Optional.ofNullable(data.getDubboCSlowQueryNum()).orElse(0);
        slowQueryTotal += Optional.ofNullable(data.getDubboProviderSlowQueryNum()).orElse(0);
        slowQueryTotal += Optional.ofNullable(data.getSqlSlowQueryNum()).orElse(0);
        slowQueryTotal += Optional.ofNullable(data.getOracleSlowNum()).orElse(0);
        slowQueryTotal += Optional.ofNullable(data.getRedisSlowNum()).orElse(0);
        slowQueryTotal += Optional.ofNullable(data.getGrpcClientSlowQueryNum()).orElse(0);
        slowQueryTotal += Optional.ofNullable(data.getGrpcServerSlowQueryNum()).orElse(0);
        slowQueryTotal += Optional.ofNullable(data.getApusClientSlowQueryNum()).orElse(0);
        slowQueryTotal += Optional.ofNullable(data.getApusServerSlowQueryNum()).orElse(0);
        slowQueryTotal += Optional.ofNullable(data.getThriftClientSlowQueryNum()).orElse(0);
        slowQueryTotal += Optional.ofNullable(data.getEsSlowNum()).orElse(0);
        data.setSlowTotal(slowQueryTotal);
    }

}
