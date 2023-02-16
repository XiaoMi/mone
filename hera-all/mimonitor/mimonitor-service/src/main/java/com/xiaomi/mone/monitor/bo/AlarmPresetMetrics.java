package com.xiaomi.mone.monitor.bo;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gaoxihui
 */
public enum AlarmPresetMetrics {


    /**
     * 系统指标
     */
    container_cpu_use_rate("container_cpu_use_rate","容器机CPU使用率", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic),
    container_cpu_average_load("container_cpu_average_load","容器负载", MetricsUnit.UNIT_NULL, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic),
    container_mem_use_rate("container_mem_use_rate","容器机内存使用率",MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic),
    container_count_monitor("container_count_monitor","容器数量",MetricsUnit.UNIT_TAI, SendAlertGroupKey.APP, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic),
//    app_restart_monitor("app_restart_monitor","应用重启",MetricsUnit.UNIT_TAI, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic),
    app_crash_monitor("app_crash_monitor","应用宕机",MetricsUnit.UNIT_TAI, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic,true),

    k8s_container_cpu_use_rate("k8s_container_cpu_use_rate","k8s容器机CPU使用率", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic),
    k8s_container_cpu_average_load("k8s_container_cpu_average_load","k8s容器负载", MetricsUnit.UNIT_NULL, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic),
    k8s_container_mem_use_rate("k8s_container_mem_use_rate","k8s容器机内存使用率",MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic),
    k8s_container_count_monitor("k8s_container_count_monitor","k8s容器数量",MetricsUnit.UNIT_TAI, SendAlertGroupKey.APP, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic),

    //资源利用率报警
    container_cpu_resource_use_rate("container_cpu_resource_use_rate","容器CPU资源利用率（1d）", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic),
    container_mem_resource_use_rate("container_mem_resource_use_rate","容器内存资源利用率（1d）", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic),
    k8s_cpu_resource_use_rate("k8s_cpu_resource_use_rate","k8s容器CPU资源利用率（1d）", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic),
    k8s_mem_resource_use_rate("k8s_mem_resource_use_rate","k8s容器内存资源利用率（1d）", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic),

    k8s_cpu_avg_use_rate("k8s_cpu_avg_use_rate","k8s容器CPU平均使用率", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic),

    /**
     * jvm指标
     */

    jvm_heap_mem_use_rate("jvm_heap_mem_use_rate","HeapUsed", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic),
    jvm_no_heap_mem_use_rate("jvm_no_heap_mem_use_rate","Non-HeapUsed", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic),
    jvm_thread_num("jvm_thread_num","线程数量", MetricsUnit.UNIT_NULL, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic),
    jvm_gc_times("jvm_gc_times","GC次数", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic),
    jvm_gc_cost("jvm_gc_cost","GC耗时", MetricsUnit.UNIT_S, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic),
    jvm_full_gc_times("jvm_full_gc_times","FullGC次数", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic),
    jvm_full_gc_cost("jvm_full_gc_cost","FullGC耗时", MetricsUnit.UNIT_S, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic),

    /**
     * 业务指标-http
     */
    http_error_times("http_error_times","Http异常数", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.error_times),
    http_availability("http_availability","Http可用性", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.availability),
    http_qps("http_qps","HttpServer_qps", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.qps),
    http_cost("http_cost","HttpServer平均耗时", MetricsUnit.UNIT_MS, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.time_cost),

    http_client_error_times("http_client_error_times","HttpClient异常数", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.error_times),
    http_client_availability("http_client_availability","HttpClient可用性", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.availability),
    http_client_qps("http_client_qps","HttpClient_qps", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.qps),
    http_client_cost("http_client_cost","HttpClient平均耗时", MetricsUnit.UNIT_MS, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.time_cost),

    /**
     * 业务指标-dobbo
     */
    dubbo_error_times("dubbo_error_times","DubboConsumer异常数", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.error_times),
    dubbo_provider_error_times("dubbo_provider_error_times","DubboProvider异常数", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.error_times),
    dubbo_qps("dubbo_qps","DubboConsumer_qps", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.qps),
    dubbo_provider_qps("dubbo_provider_qps","DubboProvider_qps", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.qps),
    dubbo_cost("dubbo_cost","DubboConsumer平均耗时", MetricsUnit.UNIT_MS, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.time_cost),
    dubbo_provider_cost("dubbo_provider_cost","DubboProvider平均耗时", MetricsUnit.UNIT_MS, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.time_cost),
    dubbo_availability("dubbo_availability","DubboConsumer可用性", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.availability),
    dubbo_provider_availability("dubbo_provider_availability","DubboProvider可用性", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.availability),
//    dubbo_time_cost("dubbo_time_cost","Dubbo响应时间"),
    dubbo_slow_query("dubbo_slow_query","DubboConsumer慢查询数", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.slow_times),
    dubbo_provider_slow_query("dubbo_provider_slow_query","DubboProvider慢查询数", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.slow_times),


    /**
     * grpc server（grpc调入）
     */
    grpc_server_error_times("grpc_server_error_times","grpc调入异常数",
            "grpcServerError","grpcServer","grpcServerSlowQuery","grpcServerTimeCost",
            MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.error_times),

    grpc_server_availability("grpc_server_availability","grpc调入可用性",
            "grpcServerError","grpcServer","grpcServerSlowQuery","grpcServerTimeCost",
            MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.availability),

    grpc_server_qps("grpc_server_qps","grpc调入qps",
            "grpcServerError","grpcServer","grpcServerSlowQuery","grpcServerTimeCost",
            MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.qps),

    grpc_server_slow_times("grpc_server_slow_times","grpc调入慢查询数",
            "grpcServerError","grpcServer","grpcServerSlowQuery","grpcServerTimeCost",
            MetricsUnit.UNIT_COUNT,SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.slow_times),

    grpc_server_time_cost("grpc_server_time_cost","grpc调入平均耗时",
            "grpcServerError","grpcServer","grpcServerSlowQuery","grpcServerTimeCost",
            MetricsUnit.UNIT_MS,SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.time_cost),

    /**
     * grpc client（grpc调出）
     */
    grpc_client_error_times("grpc_client_error_times","grpc调出异常数",
            "grpcClientError","grpcClient","grpcClientSlowQuery","grpcClientTimeCost",
            MetricsUnit.UNIT_COUNT,SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.error_times),

    grpc_client_availability("grpc_client_availability","grpc调出可用性",
            "grpcClientError","grpcClient","grpcClientSlowQuery","grpcClientTimeCost",
            MetricsUnit.UNIT_PERCENT,SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.availability),

    grpc_client_qps("grpc_client_qps","grpc调出qps",
            "grpcClientError","grpcClient","grpcClientSlowQuery","grpcClientTimeCost",
            MetricsUnit.UNIT_COUNT,SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.qps),

    grpc_client_slow_times("grpc_client_slow_times","grpc调出慢查询数",
            "grpcClientError","grpcClient","grpcClientSlowQuery","grpcClientTimeCost",
            MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.slow_times),

    grpc_client_time_cost("grpc_client_time_cost","grpc调出平均耗时",
            "grpcClientError","grpcClient","grpcClientSlowQuery","grpcClientTimeCost",
            MetricsUnit.UNIT_MS, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.time_cost),


    /**
     * apus server（apus调入）
     */
    apus_server_error_times("apus_server_error_times","apus调入异常数",
            "apusServerError","apusServer","apusServerSlowQuery","apusServerTimeCost",
            MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.error_times),

    apus_server_availability("apus_server_availability","apus调入可用性",
            "apusServerError","apusServer","apusServerSlowQuery","apusServerTimeCost",
            MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.availability),

    apus_server_qps("apus_server_qps","apus调入qps",
            "apusServerError","apusServer","apusServerSlowQuery","apusServerTimeCost",
            MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.qps),

    apus_server_slow_times("apus_server_slow_times","apus调入慢查询数",
            "apusServerError","apusServer","apusServerSlowQuery","apusServerTimeCost",
            MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.slow_times),

    apus_server_time_cost("apus_server_time_cost","apus调入平均耗时",
            "apusServerError","apusServer","apusServerSlowQuery","apusServerTimeCost",
            MetricsUnit.UNIT_MS, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.time_cost),

    /**
     * apus client（apus调出）
     */
    apus_client_error_times("apus_client_error_times","apus调出异常数",
            "apusClientError","apusClient","apustClientSlowQuery","apusClientTimeCost",
            MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.error_times),

    apus_client_availability("apus_client_availability","apus调出可用性",
            "apusClientError","apusClient","apustClientSlowQuery","apusClientTimeCost",
            MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.availability),

    apus_client_qps("apus_client_qps","apus调出qps",
            "apusClientError","apusClient","apustClientSlowQuery","apusClientTimeCost",
            MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.qps),

    apus_client_slow_times("apus_client_slow_times","apus调出慢查询数",
            "apusClientError","apusClient","apusClientSlowQuery","apusClientTimeCost",
            MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.slow_times),

    apus_client_time_cost("apus_client_time_cost","apus调出平均耗时",
            "apusClientError","apusClient","apustClientSlowQuery","apusClientTimeCost",
            MetricsUnit.UNIT_MS, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.time_cost),


    /**
     * thrift server（thrift调入）
     */
    thrift_server_error_times("thrift_server_error_times","thrift调入异常数",
            "thriftServerError","thriftServer","thriftServerSlowQuery","thriftServerTimeCost",
            MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.error_times),

    thrift_server_availability("thrift_server_availability","thrift调入可用性",
            "thriftServerError","thriftServer","thriftServerSlowQuery","thriftServerTimeCost",
            MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.availability),

    thrift_server_qps("thrift_server_qps","thrift调入qps",
            "thriftServerError","thriftServer","thriftServerSlowQuery","thriftServerTimeCost",
            MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.qps),

    thrift_server_slow_times("thrift_server_slow_times","thrift调入慢查询数",
            "thriftServerError","thriftServer","thriftServerSlowQuery","thriftServerTimeCost",
            MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.slow_times),

    thrift_server_time_cost("thrift_server_time_cost","thrift调入平均耗时",
            "thriftServerError","thriftServer","thriftServerSlowQuery","thriftServerTimeCost",
            MetricsUnit.UNIT_MS, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.time_cost),


    /**
     * thrift client（thrift调出）
     */
    thrift_client_error_times("thrift_client_error_times","thrift调出异常数",
            "thriftClientError","thriftClient","thriftClientSlowQuery","thriftClientTimeCost",
            MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.error_times),

    thrift_client_availability("thrift_client_availability","thrift调出可用性",
            "thriftClientError","thriftClient","thriftClientSlowQuery","thriftClientTimeCost",
            MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.availability),

    thrift_client_qps("thrift_client_qps","thrift调出qps",
            "thriftClientError","thriftClient","thriftClientSlowQuery","thriftClientTimeCost",
            MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.qps),

    thrift_client_slow_times("thrift_client_slow_times","thrift调出慢查询数",
            "thriftClientError","thriftClient","thriftClientSlowQuery","thriftClientTimeCost",
            MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.slow_times),

    thrift_client_time_cost("thrift_client_time_cost","thrift调出平均耗时",
            "thriftClientError","thriftClient","thriftClientSlowQuery","thriftClientTimeCost",
            MetricsUnit.UNIT_MS, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.time_cost),


    /**
     * 业务指标-db
     */
    db_error_times("db_error_times","DB异常数", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_SQL_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.error_times),
    db_availability("db_availability","DB可用性", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_SQL_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.availability),
//    db_avg_time_cost("db_avg_time_cost","DB平均响应时间"),
    db_slow_query("db_slow_query","DB慢查询数", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_SQL_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.slow_times);



    private String code;
    private String message;
    private String errorMetric;
    private String totalMetric;
    private String slowQueryMetric;
    private String timeCostMetric;
    private MetricsUnit unit;
    private SendAlertGroupKey groupKey;
    private AlarmStrategyType strategyType;
    private InterfaceMetricTypes metricType;
    private Boolean hideValueConfig;//是否隐藏页面的value配置，值为true隐藏页面的value配置

    AlarmPresetMetrics(String code, String message, MetricsUnit unit, SendAlertGroupKey groupKey, AlarmStrategyType strategyType,InterfaceMetricTypes metricType){
        this.code = code;
        this.message = message;
        this.unit = unit;
        this.groupKey = groupKey;
        this.strategyType = strategyType;
        this.metricType = metricType;
    }

    AlarmPresetMetrics(String code, String message, MetricsUnit unit, SendAlertGroupKey groupKey, AlarmStrategyType strategyType,InterfaceMetricTypes metricType,Boolean hideValueConfig){
        this.code = code;
        this.message = message;
        this.unit = unit;
        this.groupKey = groupKey;
        this.strategyType = strategyType;
        this.metricType = metricType;
        this.hideValueConfig = hideValueConfig;
    }

    AlarmPresetMetrics(String code, String message, String errorMetric,String totalMetric,String slowQueryMetric,String timeCostMetric,MetricsUnit unit, SendAlertGroupKey groupKey, AlarmStrategyType strategyType,InterfaceMetricTypes metricType){
        this.code = code;
        this.message = message;
        this.errorMetric = errorMetric;
        this.totalMetric = totalMetric;
        this.slowQueryMetric = slowQueryMetric;
        this.timeCostMetric = timeCostMetric;
        this.unit = unit;
        this.groupKey = groupKey;
        this.strategyType = strategyType;
        this.metricType = metricType;
    }

    AlarmPresetMetrics(String code, String message, String errorMetric,String totalMetric,String slowQueryMetric,String timeCostMetric,MetricsUnit unit, SendAlertGroupKey groupKey, AlarmStrategyType strategyType,InterfaceMetricTypes metricType,Boolean hideValueConfig){
        this.code = code;
        this.message = message;
        this.errorMetric = errorMetric;
        this.totalMetric = totalMetric;
        this.slowQueryMetric = slowQueryMetric;
        this.timeCostMetric = timeCostMetric;
        this.unit = unit;
        this.groupKey = groupKey;
        this.strategyType = strategyType;
        this.metricType = metricType;
        this.hideValueConfig = hideValueConfig;
    }

    public InterfaceMetricTypes getMetricType() {
        return metricType;
    }

    public String getErrorMetric() {
        return errorMetric;
    }

    public String getTotalMetric() {
        return totalMetric;
    }

    public String getSlowQueryMetric() {
        return slowQueryMetric;
    }

    public String getTimeCostMetric() {
        return timeCostMetric;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MetricsUnit getUnit() {
        return unit;
    }

    public SendAlertGroupKey getGroupKey() {
        return groupKey;
    }

    public AlarmStrategyType getStrategyType() {
        return strategyType;
    }

    public static AlarmPresetMetrics getByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        for (AlarmPresetMetrics metrics : AlarmPresetMetrics.values()) {
            if (metrics.code.equals(code)) {
                return metrics;
            }
        }
        return null;
    }
    
    public static Map<String,String> getEnumMap(){
        Map<String,String> map = new LinkedHashMap<>();
        AlarmPresetMetrics[] values = AlarmPresetMetrics.values();
        for(AlarmPresetMetrics value : values){
            map.put(value.getCode(),value.getMessage());
        }
        return map;
    }

    public static List<MetricsRule> getEnumList(){
        Map<AlarmPresetMetrics,MetricLabelKind> map = MetricLabelKind.getMetricLabelKindMap();
        MetricLabelKind kind = null;
        List <MetricsRule> list = new ArrayList<>();
        AlarmPresetMetrics[] values = AlarmPresetMetrics.values();
        for(AlarmPresetMetrics value : values){
            MetricsRule rule = new MetricsRule(value.getCode(),value.getMessage(), value.getUnit().getCode(), value.strategyType.getCode(),value.getMetricType().getName(),value.hideValueConfig);
            kind = map.get(value);
            if (kind != null) {
                rule.setKind(kind.getKind());
            }
            list.add(rule);
        }
        return list;
    }

    public static void main(String[] args) {
        System.out.println(new Gson().toJson(getEnumList()));
    }

}
