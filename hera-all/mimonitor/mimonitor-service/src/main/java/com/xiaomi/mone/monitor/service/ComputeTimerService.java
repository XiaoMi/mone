package com.xiaomi.mone.monitor.service;

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

        metricExecutor = new ThreadPoolExecutor(30, 100, 5, TimeUnit.MINUTES, new LinkedBlockingQueue(300),
                (Runnable r) -> new Thread(r, "compute-metric-thread-v2"), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @PreDestroy
    public void destory() {
        executor.shutdownNow();
    }

    public Result<List<AppAlarmData>> getProjectStatistics(AppMonitorRequest param) {
        final Long endTime = CommonUtil.toSeconds(System.currentTimeMillis());
        final Long startTime = endTime - param.getDuration();
        final String timeDurarion = param.getDuration() + "s";
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
                    return getAppAlarmData(project, startTime, endTime, timeDurarion);
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
     */
    public AppAlarmData getAppAlarmData(ProjectInfo project, Long startTime, Long endTime, String timeDurarion) {
        String appName = new StringBuilder().append(project.getId()).append("_").append(project.getName().replaceAll("-","_")).toString();

        AppAlarmData data = null;
//        AppAlarmData data = redisHelper.getAppAlarmData(appName);
//        if (data != null) {
//            log.info("ComputeTimerServiceV2.getAppAlarmData cache-result appName={}, data={}", appName, data);
//            return data;
//        }

        AppAlarmData.AppAlarmDataBuilder dataBuilder = AppAlarmData.builder();
        dataBuilder.id(project.getId()).name(project.getName()).iamTreeId(project.getIamTreeId());

        CountDownLatch latch = new CountDownLatch(7);

        try {

            metricExecutor.execute(() -> {
                computByMetricType(appName,MetricKind.http,dataBuilder,startTime,endTime,timeDurarion,latch);
            });
            metricExecutor.execute(() -> {
                computByMetricType(appName,MetricKind.dubbo,dataBuilder,startTime,endTime,timeDurarion,latch);
            });
            metricExecutor.execute(() -> {
                computByMetricType(appName,MetricKind.grpc,dataBuilder,startTime,endTime,timeDurarion,latch);
            });
            metricExecutor.execute(() -> {
                computByMetricType(appName,MetricKind.apus,dataBuilder,startTime,endTime,timeDurarion,latch);
            });
            metricExecutor.execute(() -> {
                computByMetricType(appName,MetricKind.thrift,dataBuilder,startTime,endTime,timeDurarion,latch);
            });
            metricExecutor.execute(() -> {
                computByMetricType(appName,MetricKind.db,dataBuilder,startTime,endTime,timeDurarion,latch);
            });

            metricExecutor.execute(() -> {
                computByMetricType(appName,MetricKind.redis,dataBuilder,startTime,endTime,timeDurarion,latch);
            });


            dataBuilder.alertTotal(alertHelper.queryAlertEventNum(project.getId().intValue(), project.getIamTreeId().intValue(), startTime, endTime));
            dataBuilder.logExceptionNum(0);

            latch.await();

            data = dataBuilder.build();
            calExceptionTotal(data);
            calSlowQueryTotal(data);
            log.info("ComputeTimerServiceV2.getAppAlarmData new-result appName={}, data={}", appName, data);
//            redisHelper.setAppAlarmData(appName, data);
        } catch (Exception e) {
            log.error("ComputeTimerServiceV2.getAppAlarmData error! appName={}", appName, e);
        }
        return data;
    }

    private void computByMetricType(String appName,MetricKind metricKind,AppAlarmData.AppAlarmDataBuilder dataBuilder,Long startTime, Long endTime, String timeDurarion,CountDownLatch latch){
        try {

            switch (metricKind){
                case http:

                    // http请求异常统计
                    Result<PageData> httpExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.httpError.getCode(), null, appName, MetricSuffix._total.name(), startTime, endTime, null, timeDurarion);
                    dataBuilder.httpExceptionNum(countRecordMetric(httpExceptions));

                    // http请求异常统计
                    Result<PageData> httpClientExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.httpClientError.getCode(), null, appName, MetricSuffix._total.name(), startTime, endTime, null, timeDurarion);
                    dataBuilder.httpClientExceptionNum(countRecordMetric(httpClientExceptions));

                    break;

                case dubbo:

                    // dubbo请求异常统计
                    Result<PageData> dubboExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.dubboConsumerError.getCode(), null, appName, MetricSuffix._total.name(), startTime, endTime, null, timeDurarion);
                    dataBuilder.dubboExceptionNum(countRecordMetric(dubboExceptions));
                    // dubbo请求异常统计
                    Result<PageData> dubboPExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.dubboProvider.getCode(), null, appName, MetricSuffix._total.name(), startTime, endTime, null, timeDurarion);
                    dataBuilder.dubboPExceptionNum(countRecordMetric(dubboPExceptions));
                    // dubbo consumer慢请求统计
                    Result<PageData> dubboConsumerSlowQuerys = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.dubboConsumerSlowQuery.getCode(), null, appName, MetricSuffix._total.name(), startTime, endTime, null, timeDurarion);
                    dataBuilder.dubboCSlowQueryNum(countRecordMetric(dubboConsumerSlowQuerys));
                    log.info("projectName:{},dubboConsumerSlowQuerys:{}",appName,dubboConsumerSlowQuerys);
                    // dubbo provider慢请求统计
                    Result<PageData> dubboProviderSlowQuerys = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.dubboProviderSlowQuery.getCode(), null, appName, MetricSuffix._total.name(), startTime, endTime, null, timeDurarion);
                    dataBuilder.dubboProviderSlowQueryNum(countRecordMetric(dubboProviderSlowQuerys));
                    log.info("projectName:{},dubboProviderSlowQuerys:{}",appName,dubboProviderSlowQuerys);

                    break;

//                case grpc :
//                    // grpc请求异常统计
//                    Result<PageData> grpcServerExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.grpcServerError.getCode(), null, appName, MetricSuffix._total.name(), startTime, endTime, null, timeDurarion);
//                    dataBuilder.grpcServerErrorNum(countRecordMetric(grpcServerExceptions));
//
//                    Result<PageData> grpcClientExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.grpcClientError.getCode(), null, appName, MetricSuffix._total.name(), startTime, endTime, null, timeDurarion);
//                    dataBuilder.grpcClientErrorNum(countRecordMetric(grpcClientExceptions));
//
//                    Result<PageData> grpcClientSlowQuery = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.grpcClientSlowQuery.getCode(), null, appName, MetricSuffix._total.name(), startTime, endTime, null, timeDurarion);
//                    dataBuilder.grpcClientSlowQueryNum(countRecordMetric(grpcClientSlowQuery));
//
//                    Result<PageData> grpcServerSlowQuery = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.grpcServerSlowQuery.getCode(), null, appName, MetricSuffix._total.name(), startTime, endTime, null, timeDurarion);
//                    dataBuilder.grpcServerSlowQueryNum(countRecordMetric(grpcServerSlowQuery));
//
//                    break;
//
//                case apus :
//
//                    // apus请求异常统计
//                    Result<PageData> apusServerExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.apusServerError.getCode(), null, appName, MetricSuffix._total.name(), startTime, endTime, null, timeDurarion);
//                    dataBuilder.apusServerErrorNum(countRecordMetric(apusServerExceptions));
//
//                    Result<PageData> apusClientExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.apusClientError.getCode(), null, appName, MetricSuffix._total.name(), startTime, endTime, null, timeDurarion);
//                    dataBuilder.apusClientErrorNum(countRecordMetric(apusClientExceptions));
//
//                    Result<PageData> apusClientSlowQuery = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.apusClientSlowQuery.getCode(), null, appName, MetricSuffix._total.name(), startTime, endTime, null, timeDurarion);
//                    dataBuilder.apusClientErrorNum(countRecordMetric(apusClientSlowQuery));
//
//                    Result<PageData> apusServerSlowQuery = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.apusServerSlowQuery.getCode(), null, appName, MetricSuffix._total.name(), startTime, endTime, null, timeDurarion);
//                    dataBuilder.apusServerSlowQueryNum(countRecordMetric(apusServerSlowQuery));
//
//                    break;
//
//
//                case thrift :
//
//                    // thrift请求异常统计
//                    Result<PageData> thriftServerExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.thriftServerError.getCode(), null, appName, MetricSuffix._total.name(), startTime, endTime, null, timeDurarion);
//                    dataBuilder.thriftServerErrorNum(countRecordMetric(thriftServerExceptions));
//
//                    Result<PageData> thriftClientExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.thriftClientError.getCode(), null, appName, MetricSuffix._total.name(), startTime, endTime, null, timeDurarion);
//                    dataBuilder.thriftClientErrorNum(countRecordMetric(thriftClientExceptions));
//
//                    Result<PageData> thriftClientSlowQuery = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.thriftClientSlowQuery.getCode(),  null, appName, MetricSuffix._total.name(), startTime, endTime, null, timeDurarion);
//                    dataBuilder.thriftClientSlowQueryNum(countRecordMetric(thriftClientSlowQuery));
//
//                    Result<PageData> thriftServerSlowQuery = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.thriftServerSlowQuery.getCode(),  null, appName, MetricSuffix._total.name(), startTime, endTime, null, timeDurarion);
//                    dataBuilder.thriftServerSlowQueryNum(countRecordMetric(thriftServerSlowQuery));
//
//                    break;

                case db :
                    // db请求异常统计
                    Result<PageData> sqlExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.dbError.getCode(), null, appName, MetricSuffix._total.name(), startTime, endTime, null, timeDurarion);
                    dataBuilder.sqlExceptionNum(countRecordMetric(sqlExceptions));
                    // db慢请求统计
                    Result<PageData> sqlSlowQuerys = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.dbSlowQuery.getCode(), null, appName, MetricSuffix._total.name(), startTime, endTime, null, timeDurarion);
                    dataBuilder.sqlSlowQueryNum(countRecordMetric(sqlSlowQuerys));
                    log.info("projectName:{},sqlSlowQuerys:{}",appName,sqlSlowQuerys);

                    break;

                case redis :
                    // redis请求异常统计
                    Result<PageData> redisExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.redisError.getCode(), null, appName, MetricSuffix._total.name(), startTime, endTime, null, timeDurarion);
                    dataBuilder.redisExceptionNum(countRecordMetric(redisExceptions));
                    break;

                default:
                    log.error("invalid metric kind assign! metricType:{}",metricKind);
                    break;

            }

        } catch (Exception e) {
            log.error("ComputeTimerServiceV2.getAppAlarmData error! appName={}", appName, e);
        }finally {
            latch.countDown();
            log.info("latch.countDown latch:{},count:{}",latch,latch.getCount());
        }
    }

    public AppAlarmData countAppMetricData(AppMonitorRequest param) {


        ProjectInfo project = param.getProjectList().get(0);
        String appName = new StringBuilder().append(project.getId()).append("_").append(project.getName().replaceAll("-","_")).toString();

        AppAlarmData data = null;

        try {
            AppAlarmData.AppAlarmDataBuilder dataBuilder = AppAlarmData.builder();
            dataBuilder.id(project.getId()).name(project.getName()).iamTreeId(project.getIamTreeId());

            String metricTypeCode = param.getMetricType();

            //当前页面
            MetricKind.MetricType metricType = MetricKind.getMetricTypeByCode(metricTypeCode);

            Result<PageData> httpExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.httpError.getCode(),
                    getLable(MetricKind.MetricType.http_exception,metricType,param), appName, MetricSuffix._total.name(),
                    getSTime(MetricKind.MetricType.http_exception,metricType,param),
                    getETime(MetricKind.MetricType.http_exception,metricType,param), null,
                    getDurarion(MetricKind.MetricType.http_exception,metricType,param));
            dataBuilder.httpExceptionNum(countRecordMetric(httpExceptions));

            Result<PageData> httpClientExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.httpClientError.getCode(),
                    getLable(MetricKind.MetricType.http_client_exception,metricType,param), appName, MetricSuffix._total.name(),
                    getSTime(MetricKind.MetricType.http_client_exception,metricType,param),
                    getETime(MetricKind.MetricType.http_client_exception,metricType,param), null,
                    getDurarion(MetricKind.MetricType.http_client_exception,metricType,param));
            dataBuilder.httpClientExceptionNum(countRecordMetric(httpClientExceptions));

            // dubbo请求异常统计
            Result<PageData> dubboExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.dubboConsumerError.getCode(),
                    getLable(MetricKind.MetricType.dubbo_consumer_exception,metricType,param), appName, MetricSuffix._total.name(),
                    getSTime(MetricKind.MetricType.dubbo_consumer_exception,metricType,param),
                    getETime(MetricKind.MetricType.dubbo_consumer_exception,metricType,param), null,
                    getDurarion(MetricKind.MetricType.dubbo_consumer_exception,metricType,param));
            dataBuilder.dubboExceptionNum(countRecordMetric(dubboExceptions));

            // dubbo请求异常统计
            Result<PageData> dubboPExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.dubboProvider.getCode(),
                    getLable(MetricKind.MetricType.dubbo_provider_exception,metricType,param), appName, MetricSuffix._total.name(),
                    getSTime(MetricKind.MetricType.dubbo_provider_exception,metricType,param),
                    getETime(MetricKind.MetricType.dubbo_provider_exception,metricType,param), null,
                    getDurarion(MetricKind.MetricType.dubbo_provider_exception,metricType,param));
            dataBuilder.dubboPExceptionNum(countRecordMetric(dubboPExceptions));

//            // grpc
//            Result<PageData> grpcServerExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.grpcServerError.getCode(),
//                    getLable(MetricKind.MetricType.grpc_server_exception,metricType,param), appName, MetricSuffix._total.name(),
//                    getSTime(MetricKind.MetricType.grpc_server_exception,metricType,param),
//                    getETime(MetricKind.MetricType.grpc_server_exception,metricType,param), null,
//                    getDurarion(MetricKind.MetricType.grpc_server_exception,metricType,param));
//            dataBuilder.grpcServerErrorNum(countRecordMetric(grpcServerExceptions));
//
//            // grpc
//            Result<PageData> grpcClientExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.grpcClientError.getCode(),
//                    getLable(MetricKind.MetricType.grpc_client_exception,metricType,param), appName, MetricSuffix._total.name(),
//                    getSTime(MetricKind.MetricType.grpc_client_exception,metricType,param),
//                    getETime(MetricKind.MetricType.grpc_client_exception,metricType,param), null,
//                    getDurarion(MetricKind.MetricType.grpc_client_exception,metricType,param));
//            dataBuilder.grpcClientErrorNum(countRecordMetric(grpcClientExceptions));
//
//            // apus
//            Result<PageData> apusServerExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.apusServerError.getCode(),
//                    getLable(MetricKind.MetricType.apus_server_exception,metricType,param), appName, MetricSuffix._total.name(),
//                    getSTime(MetricKind.MetricType.apus_server_exception,metricType,param),
//                    getETime(MetricKind.MetricType.apus_server_exception,metricType,param), null,
//                    getDurarion(MetricKind.MetricType.apus_server_exception,metricType,param));
//            dataBuilder.apusServerErrorNum(countRecordMetric(apusServerExceptions));
//
//            Result<PageData> apusClientExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.apusClientError.getCode(),
//                    getLable(MetricKind.MetricType.apus_client_exception,metricType,param), appName, MetricSuffix._total.name(),
//                    getSTime(MetricKind.MetricType.apus_client_exception,metricType,param),
//                    getETime(MetricKind.MetricType.apus_client_exception,metricType,param), null,
//                    getDurarion(MetricKind.MetricType.apus_client_exception,metricType,param));
//            dataBuilder.apusClientErrorNum(countRecordMetric(apusClientExceptions));
//
//            // thrift
//            Result<PageData> thriftServerExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.thriftServerError.getCode(),
//                    getLable(MetricKind.MetricType.thrift_server_exception,metricType,param), appName, MetricSuffix._total.name(),
//                    getSTime(MetricKind.MetricType.thrift_server_exception,metricType,param),
//                    getETime(MetricKind.MetricType.thrift_server_exception,metricType,param), null,
//                    getDurarion(MetricKind.MetricType.thrift_server_exception,metricType,param));
//            dataBuilder.thriftServerErrorNum(countRecordMetric(thriftServerExceptions));
//
//            Result<PageData> thriftClientExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.thriftClientError.getCode(),
//                    getLable(MetricKind.MetricType.thrift_client_exception,metricType,param), appName, MetricSuffix._total.name(),
//                    getSTime(MetricKind.MetricType.thrift_client_exception,metricType,param),
//                    getETime(MetricKind.MetricType.thrift_client_exception,metricType,param), null,
//                    getDurarion(MetricKind.MetricType.thrift_client_exception,metricType,param));
//            dataBuilder.thriftClientErrorNum(countRecordMetric(thriftClientExceptions));


            // db请求异常统计
            Result<PageData> sqlExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.dbError.getCode(),
                    getLable(MetricKind.MetricType.db_exception,metricType,param), appName, MetricSuffix._total.name(),
                    getSTime(MetricKind.MetricType.db_exception,metricType,param),
                    getETime(MetricKind.MetricType.db_exception,metricType,param), null,
                    getDurarion(MetricKind.MetricType.db_exception,metricType,param));
            dataBuilder.sqlExceptionNum(countRecordMetric(sqlExceptions));

            // redis请求异常统计
            Result<PageData> redisExceptions = prometheusService.queryRangeSumOverTime(ReqErrorMetrics.redisError.getCode(),
                    getLable(MetricKind.MetricType.redis_exception,metricType,param), appName, MetricSuffix._total.name(),
                    getSTime(MetricKind.MetricType.redis_exception,metricType,param),
                    getETime(MetricKind.MetricType.redis_exception,metricType,param), null,
                    getDurarion(MetricKind.MetricType.redis_exception,metricType,param));
            dataBuilder.redisExceptionNum(countRecordMetric(redisExceptions));

            // dubbo consumer慢请求统计
            Result<PageData> dubboConsumerSlowQuerys = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.dubboConsumerSlowQuery.getCode(),
                    getLable(MetricKind.MetricType.dubbo_consumer_slow_query,metricType,param), appName, MetricSuffix._total.name(),
                    getSTime(MetricKind.MetricType.dubbo_consumer_slow_query,metricType,param),
                    getETime(MetricKind.MetricType.dubbo_consumer_slow_query,metricType,param), null,
                    getDurarion(MetricKind.MetricType.dubbo_consumer_slow_query,metricType,param));
            dataBuilder.dubboCSlowQueryNum(countRecordMetric(dubboConsumerSlowQuerys));

            // dubbo provider慢请求统计
            Result<PageData> dubboProviderSlowQuerys = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.dubboProviderSlowQuery.getCode(),
                    getLable(MetricKind.MetricType.dubbo_provider_slow_query,metricType,param), appName, MetricSuffix._total.name(),
                    getSTime(MetricKind.MetricType.dubbo_provider_slow_query,metricType,param),
                    getETime(MetricKind.MetricType.dubbo_provider_slow_query,metricType,param), null,
                    getDurarion(MetricKind.MetricType.dubbo_provider_slow_query,metricType,param));
            dataBuilder.dubboProviderSlowQueryNum(countRecordMetric(dubboProviderSlowQuerys));
            // db慢请求统计
            Result<PageData> sqlSlowQuerys = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.dbSlowQuery.getCode(),
                    getLable(MetricKind.MetricType.db_slow_query,metricType,param), appName, MetricSuffix._total.name(),
                    getSTime(MetricKind.MetricType.db_slow_query,metricType,param),
                    getETime(MetricKind.MetricType.db_slow_query,metricType,param), null,
                    getDurarion(MetricKind.MetricType.db_slow_query,metricType,param));
            dataBuilder.sqlSlowQueryNum(countRecordMetric(sqlSlowQuerys));



//            // grpc
//            Result<PageData> grpcServerSlowQuery = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.grpcServerSlowQuery.getCode(),
//                    getLable(MetricKind.MetricType.grpc_server_slow_query,metricType,param), appName, MetricSuffix._total.name(),
//                    getSTime(MetricKind.MetricType.grpc_server_slow_query,metricType,param),
//                    getETime(MetricKind.MetricType.grpc_server_slow_query,metricType,param), null,
//                    getDurarion(MetricKind.MetricType.grpc_server_slow_query,metricType,param));
//            dataBuilder.grpcServerSlowQueryNum(countRecordMetric(grpcServerSlowQuery));
//
//            // grpc
//            Result<PageData> grpcClientSlowQuery = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.grpcClientSlowQuery.getCode(),
//                    getLable(MetricKind.MetricType.grpc_client_slow_query,metricType,param), appName, MetricSuffix._total.name(),
//                    getSTime(MetricKind.MetricType.grpc_client_slow_query,metricType,param),
//                    getETime(MetricKind.MetricType.grpc_client_slow_query,metricType,param), null,
//                    getDurarion(MetricKind.MetricType.grpc_client_slow_query,metricType,param));
//            dataBuilder.grpcClientSlowQueryNum(countRecordMetric(grpcClientSlowQuery));
//
//            // apus
//            Result<PageData> apusServerSlowQuery = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.apusServerSlowQuery.getCode(),
//                    getLable(MetricKind.MetricType.apus_server_slow_query,metricType,param), appName, MetricSuffix._total.name(),
//                    getSTime(MetricKind.MetricType.apus_server_slow_query,metricType,param),
//                    getETime(MetricKind.MetricType.apus_server_slow_query,metricType,param), null,
//                    getDurarion(MetricKind.MetricType.apus_server_slow_query,metricType,param));
//            dataBuilder.apusServerSlowQueryNum(countRecordMetric(apusServerSlowQuery));
//
//            Result<PageData> apusClientSlowQuery = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.apusClientSlowQuery.getCode(),
//                    getLable(MetricKind.MetricType.apus_client_slow_query,metricType,param), appName, MetricSuffix._total.name(),
//                    getSTime(MetricKind.MetricType.apus_client_slow_query,metricType,param),
//                    getETime(MetricKind.MetricType.apus_client_slow_query,metricType,param), null,
//                    getDurarion(MetricKind.MetricType.apus_client_slow_query,metricType,param));
//            dataBuilder.apusClientSlowQueryNum(countRecordMetric(apusClientSlowQuery));
//
//            // thrift
//            Result<PageData> thriftServerSlowQuery = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.thriftServerSlowQuery.getCode(),
//                    getLable(MetricKind.MetricType.thrift_server_slow_query,metricType,param), appName, MetricSuffix._total.name(),
//                    getSTime(MetricKind.MetricType.thrift_server_slow_query,metricType,param),
//                    getETime(MetricKind.MetricType.thrift_server_slow_query,metricType,param), null,
//                    getDurarion(MetricKind.MetricType.thrift_server_slow_query,metricType,param));
//            dataBuilder.thriftServerSlowQueryNum(countRecordMetric(thriftServerSlowQuery));
//
//            Result<PageData> thriftClientSlowQuery = prometheusService.queryRangeSumOverTime(ReqSlowMetrics.thriftClientSlowQuery.getCode(),
//                    getLable(MetricKind.MetricType.thrift_client_slow_query,metricType,param), appName, MetricSuffix._total.name(),
//                    getSTime(MetricKind.MetricType.thrift_client_slow_query,metricType,param),
//                    getETime(MetricKind.MetricType.thrift_client_slow_query,metricType,param), null,
//                    getDurarion(MetricKind.MetricType.thrift_client_slow_query,metricType,param));
//            dataBuilder.thriftClientSlowQueryNum(countRecordMetric(thriftClientSlowQuery));


            data = dataBuilder.build();
            calExceptionTotal(data);
            calSlowQueryTotal(data);
            log.info("ComputeTimerService.countAppMetricData appName={}, data={}", appName, data);
        } catch (Exception e) {
            log.error("ComputeTimerService.countAppMetricData error! appName={}", appName, e);
        }
        return data;
    }

    private Long getSTime(MetricKind.MetricType metricTypeTarget,MetricKind.MetricType metricTypeParam,AppMonitorRequest param){
        if(metricTypeTarget == null || metricTypeParam == null){
            return  param.getStartTime();
        }
        return metricTypeTarget == metricTypeParam ? param.getStartTimeCurrent() : param.getStartTime();
    }
    private Long getETime(MetricKind.MetricType metricTypeTarget,MetricKind.MetricType metricTypeParam,AppMonitorRequest param){
        if(metricTypeTarget == null || metricTypeParam == null){
            param.getEndTime();
        }
        return metricTypeTarget == metricTypeParam ? param.getEndTimeCurrent() : param.getEndTime();
    }
    private String getDurarion(MetricKind.MetricType metricTypeTarget,MetricKind.MetricType metricTypeParam,AppMonitorRequest param){
        if(metricTypeTarget == null || metricTypeParam == null){
            return (param.getEndTime() - param.getStartTime()) + "s";
        }
        return metricTypeTarget == metricTypeParam ? (param.getEndTimeCurrent() - param.getStartTimeCurrent()) + "s" : (param.getEndTime() - param.getStartTime()) + "s";
    }
    private Map<String,String> getLable(MetricKind.MetricType metricTypeTarget,MetricKind.MetricType metricTypeParam,AppMonitorRequest param){
        if(metricTypeTarget == null || metricTypeParam == null){
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

    private Integer computeNumMetric(Result<PageData> result) {
        if (ErrorCode.success.getCode() != result.getCode()) {
            log.error("ComputeTimerService.computeNumMetric error! result : {}", result.toString());
            return 0;
        }
        PageData data = result.getData();
        List<Metric> metrics = (List<Metric>) data.getList();
        log.info("computeNumMetric#metrics:{}",metrics);
        if (CollectionUtils.isEmpty(metrics)) {
            return 0;
        }
        Double ret = 0d;
        for (Metric metric : metrics) {
            ret += metric.getValue();
        }
        Long cret = Math.round(ret);
        log.info("ComputeTimerService.computeNumMetric ret : {},cret : {}", ret, cret);
        return cret.intValue();
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
        exceptionTotal += Optional.ofNullable(data.getRedisExceptionNum()).orElse(0);
        exceptionTotal += Optional.ofNullable(data.getGrpcServerErrorNum()).orElse(0);
        exceptionTotal += Optional.ofNullable(data.getGrpcClientErrorNum()).orElse(0);
        exceptionTotal += Optional.ofNullable(data.getApusServerErrorNum()).orElse(0);
        exceptionTotal += Optional.ofNullable(data.getApusClientErrorNum()).orElse(0);
        exceptionTotal += Optional.ofNullable(data.getThriftServerErrorNum()).orElse(0);
        exceptionTotal += Optional.ofNullable(data.getThriftClientErrorNum()).orElse(0);
        data.setExceptionTotal(exceptionTotal);
    }
    /**
     * 计算慢查询总数
     * @return
     */
    private void calSlowQueryTotal(AppAlarmData data) {
        Integer slowQueryTotal = 0;
        slowQueryTotal += Optional.ofNullable(data.getDubboCSlowQueryNum()).orElse(0);
        slowQueryTotal += Optional.ofNullable(data.getDubboProviderSlowQueryNum()).orElse(0);
        slowQueryTotal += Optional.ofNullable(data.getSqlSlowQueryNum()).orElse(0);
        slowQueryTotal += Optional.ofNullable(data.getGrpcClientSlowQueryNum()).orElse(0);
        slowQueryTotal += Optional.ofNullable(data.getGrpcServerSlowQueryNum()).orElse(0);
        slowQueryTotal += Optional.ofNullable(data.getApusClientSlowQueryNum()).orElse(0);
        slowQueryTotal += Optional.ofNullable(data.getApusServerSlowQueryNum()).orElse(0);
        slowQueryTotal += Optional.ofNullable(data.getThriftClientSlowQueryNum()).orElse(0);
        slowQueryTotal += Optional.ofNullable(data.getThriftServerSlowQueryNum()).orElse(0);
        data.setSlowTotal(slowQueryTotal);
    }

}
