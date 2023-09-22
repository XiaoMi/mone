package com.xiaomi.hera.trace.etl.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.hera.trace.etl.config.TraceConfig;
import com.xiaomi.hera.trace.etl.constant.LockUtil;
import com.xiaomi.hera.trace.etl.constant.SpanKind;
import com.xiaomi.hera.trace.etl.constant.SpanType;
import com.xiaomi.hera.trace.etl.domain.DriverDomain;
import com.xiaomi.hera.trace.etl.domain.HeraTraceEtlConfig;
import com.xiaomi.hera.trace.etl.domain.JaegerTracerDomain;
import com.xiaomi.hera.trace.etl.domain.MetricsParseResult;
import com.xiaomi.hera.trace.etl.service.HeraContextService;
import com.xiaomi.hera.trace.etl.service.WriteEsService;
import com.xiaomi.hera.trace.etl.util.ThriftUtil;
import com.xiaomi.hera.tspandata.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Slf4j
public class MetricsParseService {

    @NacosValue(value = "${query.excludeMethod}", autoRefreshed = true)
    private String excludeMethod;

    @NacosValue(value = "${query.exclude.httpServer}", autoRefreshed = true)
    private String excludeHttpServer;

    @NacosValue(value = "${query.excludeThread}", autoRefreshed = true)
    private String excludeThread;

    @NacosValue(value = "${query.excludeDB}", autoRefreshed = true)
    private String excludeDB;

    @NacosValue(value = "${query.excludeHttpurl}", autoRefreshed = true)
    private String excludeHttpurl;

    @NacosValue(value = "${query.excludeUA}", autoRefreshed = true)
    private String excludeUA;

    @NacosValue(value = "${query.env}", autoRefreshed = true)
    private String env;

    @NacosValue(value = "${query.dispatcher.excludeServiceName}", autoRefreshed = true)
    private String excludeServiceName;

    @NacosValue(value = "${query.slowtime.http}", autoRefreshed = true)
    private int httpSlowTime;

    @NacosValue(value = "${query.slowtime.dubbo}", autoRefreshed = true)
    private int dubboSlowTime;

    @NacosValue(value = "${query.slowtime.mysql}", autoRefreshed = true)
    private int mysqlSlowTime;

    @Value("${es.domain}")
    private String esDomain;

    @Autowired
    private WriteEsService esService;
    @Autowired
    private TraceConfig traceConfig;
    @Autowired
    private HeraContextService heraContextService;

    @Autowired
    private MutiMetricsCall mutiMetrics;

    @PostConstruct
    public void init() {
        mutiMetrics.init(env, "");
    }

    private static final double[] redisBuckets = new double[]{0.1D, 0.5D, 1.0D, 10.0D, 100.0D, 500.0D, 1000.0D};
    private static final double[] aopDubboBuckets = new double[]{50.0D, 100.0D, 150.0D, 200.0D, 250.0D, 300.0D, 400.0D, 500.0D, 700.0D, 1000.0D, 2000.0D, 3000.0D, 5000.0D};
    private static final double[] sqlBuckets = new double[]{10.0D, 50.0D, 100.0D, 500.0D, 1000.0D};
    private static final String DB_DRIVER = "dbDriver";

    public void parse(TSpanData tSpanData) {
        try {
            String serviceName = tSpanData.getExtra().getServiceName();
            if (StringUtils.isEmpty(serviceName)) {
                log.error("serviceName is empty : " + tSpanData);
                return;
            }
            // Collect information such as span source, QPS, daily total, etc.
            traceStatistics(serviceName);
            // Parse TSpanData and convert it into an indicator class.
            MetricsParseResult metricsParseResult = metricsParse(tSpanData);
            if (metricsParseResult.isIgnore()) {
                return;
            }
            if (metricsParseResult.isValidate()) {
                computeMetrics(metricsParseResult.getJaegerTracerDomain(), metricsParseResult.getHeraTraceEtlConfig());
            }
            if (metricsParseResult.getDriverDomain() != null) {
                esService.insertDriver(metricsParseResult.getDriverDomain());
            }
        } catch (Exception e) {
            log.error("exporte metrics error serviceName: " + tSpanData.getExtra().getServiceName()
                    + " traceId: " + tSpanData.getTraceId(), e);
        }
    }

    private void traceStatistics(String applicationName) {
        mutiMetrics.newCounter("trace_statistics_span_count", "application")
                .with(applicationName)
                .add(1, applicationName);
    }

    public MetricsParseResult metricsParse(TSpanData tSpanData) {
        String serviceName = tSpanData.getExtra().getServiceName();
        if (StringUtils.isEmpty(serviceName) || exclude(excludeServiceName, serviceName)) {
            return new MetricsParseResult(true);
        }
        HeraTraceEtlConfig config = defaultConfig(traceConfig.getConfig(serviceName));
        String operationName = tSpanData.getName();
        if (StringUtils.isEmpty(operationName) || exclude(config == null ? excludeMethod : config.getExcludeMethod(), operationName)) {
            return new MetricsParseResult(true);
        }
        DriverDomain driverDomain = null;
        if (operationName.equals(DB_DRIVER)) {
            driverDomain = new DriverDomain();
            driverDomain.setTimeStamp(String.valueOf(System.currentTimeMillis()));
            driverDomain.setAppName(formatServiceName(serviceName));
        }
        JaegerTracerDomain jtd = new JaegerTracerDomain();
        jtd.setServiceName(serviceName);
        jtd.setTraceId(tSpanData.getTraceId());
        long startTime = tSpanData.getStartEpochNanos();
        if (startTime > 0) {
            // ns to ms
            jtd.setStartTime(startTime / (1000 * 1000));
        }
        long duration = tSpanData.getEndEpochNanos() - startTime;
        // Nanoseconds to microseconds, microseconds to milliseconds need to keep decimal
        long durationUs = duration / 1000;
        if (duration > 0) {
            jtd.setDuration(durationUs);
        }
        jtd.setEndTime(jtd.getStartTime() + durationUs / 1000);
        jtd.setMethod(operationName);
        jtd.setServerIp(tSpanData.getExtra().getIp());
        TAttributes attributes = tSpanData.getAttributes();
        List<TAttributeKey> tagsKeys = attributes.getKeys();
        List<TValue> tagsValues = attributes.getValues();
        boolean isValidate = true;
        if (tagsKeys != null && tagsValues != null && tagsKeys.size() > 0 && tagsKeys.size() == tagsValues.size()) {
            for (int i = 0; i < tagsKeys.size(); i++) {
                String key = tagsKeys.get(i).getValue();
                String value = ThriftUtil.getStringValue(tagsValues.get(i), tagsKeys.get(i).getType());
                if ("thread.name".equals(key) && exclude(config == null ? excludeThread : config.getExcludeThread(), value)) {
                    isValidate = false;
                    break;
                }
                if ("http.url".equals(key) && exclude(config == null ? excludeHttpurl : config.getExcludeHttpUrl(), value)) {
                    isValidate = false;
                    break;
                }
                if ("http.user_agent".equals(key) && exclude(config == null ? excludeUA : config.getExcludeUa(), value)) {
                    isValidate = false;
                    break;
                }
                if ("db.statement".equals(key) && exclude(config == null ? excludeDB : config.getExcludeSql(), value)) {
                    isValidate = false;
                    break;
                }
                if ("http.status_code".equals(key)) {
                    jtd.setHttpCode(value);
                }
                if ("rpc.system".equals(key)) {
                    jtd.setType(value);
                }
                if ("rpc.method".equals(key)) {
                    jtd.setMethod(value);
                }
                if ("rpc.service".equals(key)) {
                    jtd.setDubboServiceName(value);
                }
                if ("span.kind".equals(key)) {
                    jtd.setKind(value);
                }
                if ("db.system".equals(key)) {
                    jtd.setType(value);
                }
                if ("db.statement".equals(key)) {
                    jtd.setStatement(value);
                }
                if ("http.method".equals(key)) {
                    jtd.setType("aop");
                }
                if ("net.peer.name".equals(key) || "net.peer.ip".equals(key)) {
                    jtd.setDbHost(value);
                }
                if ("net.peer.port".equals(key)) {
                    jtd.setDbPort(value);
                }
                if ("error".equals(key) && Boolean.valueOf(value)) {
                    jtd.setSuccess(false);
                }
                if ("db.connection_string".equals(key)) {
                    jtd.setDataSource(value);
                }
                if ("db.operation".equals(key)) {
                    jtd.setSqlMethod(value);
                }
                if ("db.name".equals(key)) {
                    jtd.setDbName(value);
                }
                if ("messaging.system".equals(key)) {
                    jtd.setType(value);
                }
                if ("messaging.destination".equals(key)) {
                    jtd.setTopic(value);
                }
                if ("hera.annotations".equals(key) && Boolean.valueOf(value)) {
                    jtd.setType(SpanType.CUSTOMIZE_MTTHOD);
                }
                //deal db driver
                if (driverDomain != null && "db.driver.domainPort".equals(key)) {
                    driverDomain.setDomainPort(value);
                }
                if (driverDomain != null && "db.driver.userName".equals(key)) {
                    driverDomain.setUserName(value);
                }
                if (driverDomain != null && "db.driver.password".equals(key)) {
                    driverDomain.setPassword(value);
                }
                if (driverDomain != null && "db.driver.type".equals(key)) {
                    driverDomain.setType(value);
                }
                if (driverDomain != null && "db.driver.dbName".equals(key)) {
                    driverDomain.setDataBaseName(value);
                }
            }
        }
        // Gets the properties in process
        TResource resource = tSpanData.getResouce();
        if (resource != null) {
            TAttributes resourceAttributes = resource.getAttributes();
            List<TAttributeKey> resourceKeys = resourceAttributes.getKeys();
            List<TValue> resourceValues = resourceAttributes.getValues();
            if (resourceKeys != null && resourceValues != null && resourceKeys.size() > 0) {
                for (int i = 0; i < resourceKeys.size(); i++) {
                    String key = resourceKeys.get(i).getValue();
                    String value = ThriftUtil.getStringValue(resourceValues.get(i), resourceKeys.get(i).getType());
                    if ("service.env".equals(key)) {
                        jtd.setServiceEnv(value);
                    }
                    if ("service.function.module".equals(key)) {
                        jtd.setFunctionModule(value);
                    }
                    if ("service.function.name".equals(key)) {
                        jtd.setFunctionName(value);
                    }
                    if ("service.function.id".equals(key)) {
                        jtd.setFunctionId(value);
                    }
                    if ("service.env.id".equals(key)) {
                        jtd.setServiceEnvId(value);
                    }
                }
            }
        }
        if (StringUtils.isEmpty(jtd.getServiceEnv())) {
            jtd.setServiceEnv("default_env");
        }
        return new MetricsParseResult(jtd, driverDomain, false, isValidate, config);
    }

    private void computeMetrics(JaegerTracerDomain jtc, HeraTraceEtlConfig config) {
        if (StringUtils.isEmpty(jtc.getType())) {
            return;
        }
        // Request type processing
        if ("redis".equals(jtc.getType())) {
            redisBuild(jtc.getStatement(), jtc);
        }
        if (SpanType.MYSQL.equals(jtc.getType()) || SpanType.MONGODB.equals(jtc.getType())) {
            jtc.setSql(jtc.getStatement());
        }
        String serviceName = jtc.getServiceName();
        String metricsServiceName = formatServiceName(serviceName);
        jtc.setMetricsServiceName(metricsServiceName);
        // http request
        if (SpanType.HTTP.equals(jtc.getType())) {
            String httpMetricsName = "hera_";
            if (SpanKind.SERVER.equals(jtc.getKind())) {
                // Filters http server counters
                if (exclude(config == null ? excludeHttpServer : config.getExcludeHttpserverMethod(), jtc.getMethod())) {
                    return;
                }
                mutiMetrics.newCounter(httpMetricsName + jtc.getType() + "TotalMethodCount", "methodName", "application", "serverIp", "serverEnv", "serverEnvId")
                        .with(jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .add(1, jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                // success or fail counter
                if (jtc.isSuccess()) {
                    mutiMetrics.newCounter(httpMetricsName + jtc.getType() + "SuccessMethodCount", "methodName", "application", "serverIp", "serverEnv", "serverEnvId")
                            .with(jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                            .add(1, jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                    // slow query
                    if (jtc.getDuration() > (config == null ? httpSlowTime : config.getHttpSlowThreshold())) {
                        mutiMetrics.newCounter(httpMetricsName + "httpSlowQuery", "methodName", "application", "serverIp", "serverEnv", "serverEnvId")
                                .with(jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                                .add(1, jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                        esService.submitErrorEsTrace(esDomain, jtc.getMethod(), metricsServiceName, jtc.getTraceId(), "http", jtc.getServerIp(), String.valueOf(jtc.getEndTime()), "", String.valueOf(jtc.getDuration()), "timeout", jtc.getHttpCode(), jtc.getServiceEnv());
                    }
                } else {
                    mutiMetrics.newCounter(httpMetricsName + "httpError", "methodName", "application", "serverIp", "errorCode", "serverEnv", "serverEnvId")
                            .with(jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getHttpCode(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                            .add(1, jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getHttpCode(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                    esService.submitErrorEsTrace(esDomain, jtc.getMethod(), metricsServiceName, jtc.getTraceId(), "http", jtc.getServerIp(), String.valueOf(jtc.getEndTime()), "", String.valueOf(jtc.getDuration()), "error", jtc.getHttpCode(), jtc.getServiceEnv());
                }
                mutiMetrics.newHistogram(httpMetricsName + jtc.getType() + "MethodTimeCount", aopDubboBuckets, new String[]{"methodName", "application", "serverIp", "serverEnv", "serverEnvId"})
                        .with(jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .observe(jtc.getDuration(), jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                mutiMetrics.newHistogram(httpMetricsName + jtc.getType() + "MethodTimeCount_without_methodName", aopDubboBuckets, new String[]{"application", "serverIp", "serverEnv", "serverEnvId"})
                        .with(metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .observe(jtc.getDuration(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
            } else if (SpanKind.CLIENT.equals(jtc.getKind())) {
                mutiMetrics.newCounter(httpMetricsName + jtc.getType() + "ClientTotalMethodCount", "methodName", "application", "serverIp", "serverEnv", "serverEnvId")
                        .with(jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .add(1, jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                if (jtc.isSuccess()) {
                    mutiMetrics.newCounter(httpMetricsName + jtc.getType() + "ClientSuccessMethodCount", "methodName", "application", "serverIp", "serverEnv", "serverEnvId")
                            .with(jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                            .add(1, jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                    if (jtc.getDuration() > (config == null ? httpSlowTime : config.getHttpSlowThreshold())) {
                        mutiMetrics.newCounter(httpMetricsName + "httpClientSlowQuery", "methodName", "application", "serverIp", "serverEnv", "serverEnvId")
                                .with(jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                                .add(1, jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                        esService.submitErrorEsTrace(esDomain, jtc.getMethod(), metricsServiceName, jtc.getTraceId(), "http_client", jtc.getServerIp(), String.valueOf(jtc.getEndTime()), "", String.valueOf(jtc.getDuration()), "timeout", jtc.getHttpCode(), jtc.getServiceEnv());
                    }
                } else {
                    mutiMetrics.newCounter(httpMetricsName + "httpClientError", "methodName", "application", "serverIp", "errorCode", "serverEnv", "serverEnvId")
                            .with(jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getHttpCode(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                            .add(1, jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getHttpCode(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                    esService.submitErrorEsTrace(esDomain, jtc.getMethod(), metricsServiceName, jtc.getTraceId(), "http_client", jtc.getServerIp(), String.valueOf(jtc.getEndTime()), "", String.valueOf(jtc.getDuration()), "error", jtc.getHttpCode(), jtc.getServiceEnv());
                }
                mutiMetrics.newHistogram(httpMetricsName + jtc.getType() + "ClientMethodTimeCount", aopDubboBuckets, new String[]{"methodName", "application", "serverIp", "serverEnv", "serverEnvId"})
                        .with(jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .observe(jtc.getDuration(), jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                mutiMetrics.newHistogram(httpMetricsName + jtc.getType() + "ClientMethodTimeCount_without_methodName", aopDubboBuckets, new String[]{"application", "serverIp", "serverEnv", "serverEnvId"})
                        .with(metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .observe(jtc.getDuration(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
            }
        }
        // dubbo request
        if (SpanType.DUBBO.equals(jtc.getType())) {
            String dubboMetricsName = "hera_";
            if (SpanKind.CLIENT.equals(jtc.getKind())) {
                mutiMetrics.newHistogram(dubboMetricsName + "dubboConsumerTimeCost", aopDubboBuckets, new String[]{"serviceName", "methodName", "application", "serverIp", "serverEnv", "serverEnvId"})
                        .with(jtc.getDubboServiceName(), jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .observe(jtc.getDuration(), jtc.getDubboServiceName(), jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                mutiMetrics.newHistogram(dubboMetricsName + "dubboConsumerTimeCost_without_methodName", aopDubboBuckets, new String[]{"serviceName", "application", "serverIp", "serverEnv", "serverEnvId"})
                        .with(jtc.getDubboServiceName(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .observe(jtc.getDuration(), jtc.getDubboServiceName(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                mutiMetrics.newCounter(dubboMetricsName + "dubboBisTotalCount", "serviceName", "methodName", "application", "serverIp", "serverEnv", "serverEnvId")
                        .with(jtc.getDubboServiceName(), jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .add(1, jtc.getDubboServiceName(), jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                if (jtc.isSuccess()) {
                    mutiMetrics.newCounter(dubboMetricsName + "dubboBisSuccessCount", "serviceName", "methodName", "application", "serverIp", "serverEnv", "serverEnvId")
                            .with(jtc.getDubboServiceName(), jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                            .add(1, jtc.getDubboServiceName(), jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                    if (jtc.getDuration() > (config == null ? dubboSlowTime : config.getDubboSlowThreshold())) {
                        mutiMetrics.newCounter(dubboMetricsName + "dubboConsumerSlowQuery", "serviceName", "methodName", "application", "serverIp", "serverEnv", "serverEnvId")
                                .with(jtc.getDubboServiceName(), jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                                .add(1, jtc.getDubboServiceName(), jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                        esService.submitErrorEsTrace(esDomain, jtc.getDubboServiceName() + "/" + jtc.getMethod(), metricsServiceName, jtc.getTraceId(), "dubbo_consumer", jtc.getServerIp(), String.valueOf(jtc.getEndTime()), "", String.valueOf(jtc.getDuration()), "timeout", jtc.getHttpCode(), jtc.getServiceEnv());
                    }
                } else {
                    mutiMetrics.newCounter(dubboMetricsName + "dubboConsumerError", "serviceName", "methodName", "application", "serverIp", "serverEnv", "serverEnvId")
                            .with(jtc.getDubboServiceName(), jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                            .add(1, jtc.getDubboServiceName(), jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                    esService.submitErrorEsTrace(esDomain, jtc.getDubboServiceName() + "/" + jtc.getMethod(), metricsServiceName, jtc.getTraceId(), "dubbo_consumer", jtc.getServerIp(), String.valueOf(jtc.getEndTime()), "", String.valueOf(jtc.getDuration()), "error", jtc.getHttpCode(), jtc.getServiceEnv());
                }
            } else if (SpanKind.SERVER.equals(jtc.getKind())) {
                mutiMetrics.newCounter(dubboMetricsName + "dubboMethodCalledCount", "serviceName", "methodName", "application", "serverIp", "serverEnv", "serverEnvId")
                        .with(jtc.getDubboServiceName(), jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .add(1, jtc.getDubboServiceName(), jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                mutiMetrics.newHistogram(dubboMetricsName + "dubboProviderCount", aopDubboBuckets, new String[]{"serviceName", "methodName", "application", "serverIp", "serverEnv", "serverEnvId"})
                        .with(jtc.getDubboServiceName(), jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .observe(jtc.getDuration(), jtc.getDubboServiceName(), jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                mutiMetrics.newHistogram(dubboMetricsName + "dubboProviderCount_without_methodName", aopDubboBuckets, new String[]{"serviceName", "application", "serverIp", "serverEnv", "serverEnvId"})
                        .with(jtc.getDubboServiceName(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .observe(jtc.getDuration(), jtc.getDubboServiceName(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                if (jtc.isSuccess()) {
                    mutiMetrics.newCounter(dubboMetricsName + "dubboMethodCalledSuccessCount", "serviceName", "methodName", "application", "serverIp", "serverEnv", "serverEnvId")
                            .with(jtc.getDubboServiceName(), jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                            .add(1, jtc.getDubboServiceName(), jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                    if (jtc.getDuration() > (config == null ? dubboSlowTime : config.getDubboSlowThreshold())) {
                        mutiMetrics.newCounter(dubboMetricsName + "dubboProviderSlowQuery", "serviceName", "methodName", "application", "serverIp", "serverEnv", "serverEnvId")
                                .with(jtc.getDubboServiceName(), jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                                .add(1, jtc.getDubboServiceName(), jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                        esService.submitErrorEsTrace(esDomain, jtc.getDubboServiceName() + "/" + jtc.getMethod(), metricsServiceName, jtc.getTraceId(), "dubbo_provider", jtc.getServerIp(), String.valueOf(jtc.getEndTime()), "", String.valueOf(jtc.getDuration()), "timeout", jtc.getHttpCode(), jtc.getServiceEnv());
                    }
                } else {
                    mutiMetrics.newCounter(dubboMetricsName + "dubboProviderError", "serviceName", "methodName", "application", "serverIp", "serverEnv", "serverEnvId")
                            .with(jtc.getDubboServiceName(), jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                            .add(1, jtc.getDubboServiceName(), jtc.getMethod(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                    esService.submitErrorEsTrace(esDomain, jtc.getDubboServiceName() + "/" + jtc.getMethod(), metricsServiceName, jtc.getTraceId(), "dubbo_provider", jtc.getServerIp(), String.valueOf(jtc.getEndTime()), "", String.valueOf(jtc.getDuration()), "error", jtc.getHttpCode(), jtc.getServiceEnv());
                }
            }
        }
        // redis
        if (SpanType.REDIS.equals(jtc.getType())) {
            String redisMetricsName = "hera_";
            if (jtc.isSuccess()) {
                mutiMetrics.newCounter(redisMetricsName + "RedisSuccessCount", "method", "host", "port", "application", "serverIp", "serverEnv", "serverEnvId")
                        .with(reduceString(jtc.getMethod(), 100), jtc.getDbHost(), jtc.getDbPort(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .add(1, reduceString(jtc.getMethod(), 100), jtc.getDbHost(), jtc.getDbPort(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
            } else {
                mutiMetrics.newCounter(redisMetricsName + "redisError", "method", "host", "port", "application", "serverIp", "serverEnv", "serverEnvId")
                        .with(reduceString(jtc.getMethod(), 100), jtc.getDbHost(), jtc.getDbPort(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .add(1, reduceString(jtc.getMethod(), 100), jtc.getDbHost(), jtc.getDbPort(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                esService.submitErrorEsTrace(esDomain, jtc.getMethod(), metricsServiceName, jtc.getTraceId(), "redis", jtc.getServerIp(), String.valueOf(jtc.getEndTime()), jtc.getDbHost() + ":" + jtc.getDbPort(), String.valueOf(jtc.getDuration()), "error", jtc.getHttpCode(), jtc.getServiceEnv());
            }
            mutiMetrics.newCounter(redisMetricsName + "RedisTotalCount", "method", "host", "port", "application", "serverIp", "serverEnv", "serverEnvId")
                    .with(reduceString(jtc.getMethod(), 100), jtc.getDbHost(), jtc.getDbPort(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                    .add(1, reduceString(jtc.getMethod(), 100), jtc.getDbHost(), jtc.getDbPort(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
            mutiMetrics.newHistogram(redisMetricsName + "RedisMethodTimeCost", redisBuckets, new String[]{"method", "host", "port", "application", "serverIp", "serverEnv", "serverEnvId"})
                    .with(reduceString(jtc.getMethod(), 100), jtc.getDbHost(), jtc.getDbPort(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                    .observe(jtc.getDuration(), reduceString(jtc.getMethod(), 100), jtc.getDbHost(), jtc.getDbPort(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
        }
        // mysql
        if (SpanType.MYSQL.equals(jtc.getType())) {
            String mysqlMetricsName = "hera_";
            if (jtc.isSuccess()) {
                mutiMetrics.newCounter(mysqlMetricsName + "sqlSuccessCount", "dataSource", "sqlMethod", "sql", "application", "serverIp", "serverEnv", "serverEnvId")
                        .with(jtc.getDataSource() + "/" + jtc.getDbName(), jtc.getSqlMethod(), reduceString(jtc.getSql(), 100), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .add(1, jtc.getDataSource() + "/" + jtc.getDbName(), jtc.getSqlMethod(), reduceString(jtc.getSql(), 100), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                if (jtc.getDuration() > (config == null ? mysqlSlowTime : config.getMysqlSlowThreshold())) {
                    mutiMetrics.newCounter(mysqlMetricsName + "dbSlowQuery", "dataSource", "sqlMethod", "sql", "application", "serverIp", "serverEnv", "serverEnvId")
                            .with(jtc.getDataSource() + "/" + jtc.getDbName(), jtc.getSqlMethod(), reduceString(jtc.getSql(), 100), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                            .add(1, jtc.getDataSource() + "/" + jtc.getDbName(), jtc.getSqlMethod(), reduceString(jtc.getSql(), 100), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                    esService.submitErrorEsTrace(esDomain, reduceString(jtc.getSql(), 200), metricsServiceName, jtc.getTraceId(), "mysql", jtc.getServerIp(), String.valueOf(jtc.getEndTime()), jtc.getDataSource() + "/" + jtc.getDbName(), String.valueOf(jtc.getDuration()), "timeout", jtc.getHttpCode(), jtc.getServiceEnv());
                }
            } else {
                mutiMetrics.newCounter(mysqlMetricsName + "dbError", "dataSource", "sqlMethod", "sql", "application", "serverIp", "serverEnv", "serverEnvId")
                        .with(jtc.getDataSource() + "/" + jtc.getDbName(), jtc.getSqlMethod(), reduceString(jtc.getSql(), 100), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .add(1, jtc.getDataSource() + "/" + jtc.getDbName(), jtc.getSqlMethod(), reduceString(jtc.getSql(), 100), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                esService.submitErrorEsTrace(esDomain, reduceString(jtc.getSql(), 200), metricsServiceName, jtc.getTraceId(), "mysql", jtc.getServerIp(), String.valueOf(jtc.getEndTime()), jtc.getDataSource() + "/" + jtc.getDbName(), String.valueOf(jtc.getDuration()), "error", jtc.getHttpCode(), jtc.getServiceEnv());
            }
            mutiMetrics.newCounter(mysqlMetricsName + "sqlTotalCount", "dataSource", "sqlMethod", "sql", "application", "serverIp", "serverEnv", "serverEnvId")
                    .with(jtc.getDataSource() + "/" + jtc.getDbName(), jtc.getSqlMethod(), reduceString(jtc.getSql(), 100), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                    .add(1, jtc.getDataSource() + "/" + jtc.getDbName(), jtc.getSqlMethod(), reduceString(jtc.getSql(), 100), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
            mutiMetrics.newHistogram(mysqlMetricsName + "sqlTotalTimer", sqlBuckets, new String[]{"dataSource", "sqlMethod", "sql", "application", "serverIp", "serverEnv", "serverEnvId"})
                    .with(jtc.getDataSource() + "/" + jtc.getDbName(), jtc.getSqlMethod(), reduceString(jtc.getSql(), 100), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                    .observe(jtc.getDuration(), jtc.getDataSource() + "/" + jtc.getDbName(), jtc.getSqlMethod(), reduceString(jtc.getSql(), 100), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
        }
        // MongoDB
        if (SpanType.MONGODB.equals(jtc.getType())) {
            String mysqlMetricsName = "hera_";
            if (jtc.isSuccess()) {
                mutiMetrics.newCounter(mysqlMetricsName + "mongoSuccessCount", "dataSource", "method", "command", "application", "serverIp", "serverEnv", "serverEnvId")
                        .with(jtc.getDataSource(), jtc.getSqlMethod(), reduceString(jtc.getSql(), 100), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .add(1, jtc.getDataSource(), jtc.getSqlMethod(), reduceString(jtc.getSql(), 100), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                if (jtc.getDuration() > mysqlSlowTime) {
                    mutiMetrics.newCounter(mysqlMetricsName + "mongodbSlowQuery", "dataSource", "method", "command", "application", "serverIp", "serverEnv", "serverEnvId")
                            .with(jtc.getDataSource(), jtc.getSqlMethod(), reduceString(jtc.getSql(), 100), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                            .add(1, jtc.getDataSource(), jtc.getSqlMethod(), reduceString(jtc.getSql(), 100), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                    esService.submitErrorEsTrace(esDomain, reduceString(jtc.getSql(), 200), metricsServiceName, jtc.getTraceId(), SpanType.MONGODB, jtc.getServerIp(), String.valueOf(jtc.getEndTime()), jtc.getDataSource(), String.valueOf(jtc.getDuration()), "timeout", jtc.getHttpCode(), jtc.getServiceEnv());
                }
            } else {
                mutiMetrics.newCounter(mysqlMetricsName + "mongodbError", "dataSource", "method", "command", "application", "serverIp", "serverEnv", "serverEnvId")
                        .with(jtc.getDataSource(), jtc.getSqlMethod(), reduceString(jtc.getSql(), 100), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .add(1, jtc.getDataSource(), jtc.getSqlMethod(), reduceString(jtc.getSql(), 100), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                esService.submitErrorEsTrace(esDomain, reduceString(jtc.getSql(), 200), metricsServiceName, jtc.getTraceId(), SpanType.MONGODB, jtc.getServerIp(), String.valueOf(jtc.getEndTime()), jtc.getDataSource(), String.valueOf(jtc.getDuration()), "error", jtc.getHttpCode(), jtc.getServiceEnv());
            }
            mutiMetrics.newCounter(mysqlMetricsName + "mongoTotalCount", "dataSource", "method", "command", "application", "serverIp", "serverEnv", "serverEnvId")
                    .with(jtc.getDataSource(), jtc.getSqlMethod(), reduceString(jtc.getSql(), 100), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                    .add(1, jtc.getDataSource(), jtc.getSqlMethod(), reduceString(jtc.getSql(), 100), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
            mutiMetrics.newHistogram(mysqlMetricsName + "mongoTotalTimer", sqlBuckets, new String[]{"dataSource", "method", "command", "application", "serverIp", "serverEnv", "serverEnvId"})
                    .with(jtc.getDataSource(), jtc.getSqlMethod(), reduceString(jtc.getSql(), 100), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                    .observe(jtc.getDuration(), jtc.getDataSource(), jtc.getSqlMethod(), reduceString(jtc.getSql(), 100), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
        }
        // rocketmq
        if (SpanType.ROCKETMQ.equals(jtc.getType())) {
            String metricsName = "hera_";
            if (SpanKind.PRODUCER.equals(jtc.getKind())) {
                mutiMetrics.newHistogram(metricsName + "rocketmqProducerTimeCost", aopDubboBuckets, new String[]{"topic", "application", "serverIp", "serverEnv", "serverEnvId"})
                        .with(jtc.getTopic(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .observe(jtc.getDuration(), jtc.getTopic(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                mutiMetrics.newCounter(metricsName + "rocketmqProducerTotalCount", "topic", "application", "serverIp", "serverEnv", "serverEnvId")
                        .with(jtc.getTopic(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .add(1, jtc.getTopic(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                if (jtc.isSuccess()) {
                    mutiMetrics.newCounter(metricsName + "rocketmqProducerSuccessCount", "topic", "application", "serverIp", "serverEnv", "serverEnvId")
                            .with(jtc.getTopic(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                            .add(1, jtc.getTopic(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                    if (jtc.getDuration() > dubboSlowTime) {
                        mutiMetrics.newCounter(metricsName + "rocketmqProducerSlowQuery", "topic", "application", "serverIp", "serverEnv", "serverEnvId")
                                .with(jtc.getTopic(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                                .add(1, jtc.getTopic(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                        esService.submitErrorEsTrace(esDomain, jtc.getMethod(), metricsServiceName, jtc.getTraceId(), "rocketmq_producer", jtc.getServerIp(), String.valueOf(jtc.getEndTime()), "", String.valueOf(jtc.getDuration()), "timeout", jtc.getHttpCode(), jtc.getServiceEnv());
                    }
                } else {
                    mutiMetrics.newCounter(metricsName + "rocketmqProducerError", "topic", "application", "serverIp", "serverEnv", "serverEnvId")
                            .with(jtc.getTopic(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                            .add(1, jtc.getTopic(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                    esService.submitErrorEsTrace(esDomain, jtc.getMethod(), metricsServiceName, jtc.getTraceId(), "rocketmq_producer", jtc.getServerIp(), String.valueOf(jtc.getEndTime()), "", String.valueOf(jtc.getDuration()), "error", jtc.getHttpCode(), jtc.getServiceEnv());
                }
            } else if (SpanKind.CONSUMER.equals(jtc.getKind())) {
                mutiMetrics.newHistogram(metricsName + "rocketmqConsumerTimeCost", aopDubboBuckets, new String[]{"topic", "application", "serverIp", "serverEnv", "serverEnvId"})
                        .with(jtc.getTopic(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .observe(jtc.getDuration(), jtc.getTopic(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                mutiMetrics.newCounter(metricsName + "rocketmqConsumerTotalCount", "topic", "application", "serverIp", "serverEnv", "serverEnvId")
                        .with(jtc.getTopic(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .add(1, jtc.getTopic(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                if (jtc.isSuccess()) {
                    mutiMetrics.newCounter(metricsName + "rocketmqConsumerSuccessCount", "topic", "application", "serverIp", "serverEnv", "serverEnvId")
                            .with(jtc.getTopic(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                            .add(1, jtc.getTopic(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                    if (jtc.getDuration() > dubboSlowTime) {
                        mutiMetrics.newCounter(metricsName + "rocketmqConsumerSlowQuery", "topic", "application", "serverIp", "serverEnv", "serverEnvId")
                                .with(jtc.getTopic(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                                .add(1, jtc.getTopic(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                        esService.submitErrorEsTrace(esDomain, jtc.getMethod(), metricsServiceName, jtc.getTraceId(), "rocketmq_consumer", jtc.getServerIp(), String.valueOf(jtc.getEndTime()), "", String.valueOf(jtc.getDuration()), "timeout", jtc.getHttpCode(), jtc.getServiceEnv());
                    }
                } else {
                    mutiMetrics.newCounter(metricsName + "rocketmqConsumerError", "topic", "application", "serverIp", "serverEnv", "serverEnvId")
                            .with(jtc.getTopic(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                            .add(1, jtc.getTopic(), metricsServiceName, jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                    esService.submitErrorEsTrace(esDomain, jtc.getMethod(), metricsServiceName, jtc.getTraceId(), "rocketmq_consumer", jtc.getServerIp(), String.valueOf(jtc.getEndTime()), "", String.valueOf(jtc.getDuration()), "error", jtc.getHttpCode(), jtc.getServiceEnv());
                }
            }
        }
        // customizeAnno
        if (SpanType.CUSTOMIZE_MTTHOD.equals(jtc.getType())) {
            String metricsName = "hera_";
            mutiMetrics.newHistogram(metricsName + "CustomizeMethodTimeCost", aopDubboBuckets, new String[]{"application", "methodName", "serverIp", "serverEnv", "serverEnvId"})
                    .with(metricsServiceName, jtc.getMethod(), jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                    .observe(jtc.getDuration(), metricsServiceName, jtc.getMethod(), jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
            mutiMetrics.newCounter(metricsName + "CustomizeMethodTotalCount", "application", "methodName", "serverIp", "serverEnv", "serverEnvId")
                    .with(metricsServiceName, jtc.getMethod(), jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                    .add(1, metricsServiceName, jtc.getMethod(), jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
            if (jtc.isSuccess()) {
                mutiMetrics.newCounter(metricsName + "CustomizeMethodSuccessCount", "application", "methodName", "serverIp", "serverEnv", "serverEnvId")
                        .with(metricsServiceName, jtc.getMethod(), jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .add(1, metricsServiceName, jtc.getMethod(), jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                if (jtc.getDuration() > dubboSlowTime) {
                    mutiMetrics.newCounter(metricsName + "CustomizeMethodSlowQuery", "application", "methodName", "serverIp", "serverEnv", "serverEnvId")
                            .with(metricsServiceName, jtc.getMethod(), jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                            .add(1, metricsServiceName, jtc.getMethod(), jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                    esService.submitErrorEsTrace(esDomain, jtc.getMethod(), metricsServiceName, jtc.getTraceId(), "customize_method", jtc.getServerIp(), String.valueOf(jtc.getEndTime()), "", String.valueOf(jtc.getDuration()), "timeout", jtc.getHttpCode(), jtc.getServiceEnv());
                }
            } else {
                mutiMetrics.newCounter(metricsName + "CustomizeMethodError", "application", "methodName", "serverIp", "serverEnv", "serverEnvId")
                        .with(metricsServiceName, jtc.getMethod(), jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId())
                        .add(1, metricsServiceName, jtc.getMethod(), jtc.getServerIp(), jtc.getServiceEnv(), jtc.getServiceEnvId());
                esService.submitErrorEsTrace(esDomain, jtc.getMethod(), metricsServiceName, jtc.getTraceId(), "customize_method", jtc.getServerIp(), String.valueOf(jtc.getEndTime()), "", String.valueOf(jtc.getDuration()), "error", jtc.getHttpCode(), jtc.getServiceEnv());
            }
        }
    }

    private boolean exclude(String excludeList, String excludeString) {
        String[] splits = excludeList.split("\\|");
        for (String split : splits) {
            if (StringUtils.isNotEmpty(split) && excludeString.contains(split)) {
                return true;
            }
        }
        return false;
    }

    /**
     * deal redis method\key
     *
     * @param statement
     * @param jtc
     */
    private void redisBuild(String statement, JaegerTracerDomain jtc) {
        if (StringUtils.isNotEmpty(statement)) {
            if (statement.indexOf("?") >= 0) {
                statement = statement.replace("?", "").trim();
            }
            String[] s = statement.split(" ");
            if (s.length == 1) {
                jtc.setMethod(s[0]);
                jtc.setKey("");
            } else if (s.length == 2) {
                jtc.setMethod(s[0]);
                jtc.setKey(s[1]);
            } else if (s.length > 2) {
                jtc.setMethod(s[0]);
                String key = "";
                for (int i = 1; i < s.length; i++) {
                    key += s[i] + ",";
                }
                jtc.setKey(key.substring(0, key.length() - 1));
            }
        }
    }

    public String reduceString(String ori, int size) {
        if (StringUtils.isNotEmpty(ori) && ori.length() > size) {
            String substring = ori.substring(0, size - 1);
            return substring;
        }
        return ori;
    }

    private String formatServiceName(String serviceName) {
        return serviceName.replace("-", "_");
    }

    private HeraTraceEtlConfig defaultConfig(HeraTraceEtlConfig config) {
        if (config == null) {
            return null;
        }
        if (StringUtils.isEmpty(config.getExcludeHttpserverMethod())) {
            config.setExcludeHttpserverMethod(excludeHttpServer);
        }
        if (StringUtils.isEmpty(config.getExcludeMethod())) {
            config.setExcludeMethod(excludeMethod);
        }
        if (StringUtils.isEmpty(config.getExcludeSql())) {
            config.setExcludeSql(excludeDB);
        }
        if (StringUtils.isEmpty(config.getExcludeHttpUrl())) {
            config.setExcludeHttpUrl(excludeHttpurl);
        }
        if (StringUtils.isEmpty(config.getExcludeThread())) {
            config.setExcludeThread(excludeThread);
        }
        if (StringUtils.isEmpty(config.getExcludeUa())) {
            config.setExcludeUa(excludeUA);
        }
        if (config.getDubboSlowThreshold() == null) {
            config.setDubboSlowThreshold(dubboSlowTime);
        }
        if (config.getHttpSlowThreshold() == null) {
            config.setHttpSlowThreshold(httpSlowTime);
        }
        if (config.getMysqlSlowThreshold() == null) {
            config.setMysqlSlowThreshold(mysqlSlowTime);
        }
        return config;
    }

}
