package com.xiaomi.mone.monitor.service;

import com.google.common.collect.Lists;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.api.ComputeTimerServiceExtension;
import com.xiaomi.mone.monitor.service.helper.AlertHelper;
import com.xiaomi.mone.monitor.service.model.AppMonitorRequest;
import com.xiaomi.mone.monitor.service.model.ProjectInfo;
import com.xiaomi.mone.monitor.service.model.prometheus.MetricKind;
import com.xiaomi.mone.monitor.service.model.redis.AppAlarmData;
import com.xiaomi.mone.monitor.service.prometheus.PrometheusService;
import com.xiaomi.mone.monitor.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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

    @Autowired
    private ComputeTimerServiceExtension computeTimerServiceExtension;

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
        computeTimerServiceExtension.computByMetricType(param, appName, metricKind, dataBuilder, startTime, endTime, timeDurarion, step);
    }

    public AppAlarmData countAppMetricData(AppMonitorRequest param) {
        ProjectInfo project = param.getProjectList().get(0);
        Long startTime = param.getStartTimeCurrent();
        Long endTime = param.getEndTimeCurrent();
        Long step = endTime - startTime;
        return getAppAlarmData(project, startTime, endTime, step + "s", step, param);
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
