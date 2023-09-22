package com.xiaomi.mone.monitor.bo;

/**
 * @author gaoxihui
 */
public enum AlarmPresetMetrics {

    /**
     * 系统指标
     */
    container_cpu_use_rate("container_cpu_use_rate","容器机CPU使用率", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic, BasicUrlType.cn_grafana_ip, "2"),
    container_cpu_average_load("container_cpu_average_load","容器负载", MetricsUnit.UNIT_NULL, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic, BasicUrlType.cn_grafana_ip, "9"),
    container_mem_use_rate("container_mem_use_rate","容器机内存使用率",MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic, BasicUrlType.cn_grafana_ip, "11"),
    container_disk_use_rate("container_disk_use_rate","容器机磁盘使用率",MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic, BasicUrlType.cn_grafana_disk_rate, "2"),
    container_count_monitor("container_count_monitor","容器数量",MetricsUnit.UNIT_TAI, SendAlertGroupKey.APP, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic, BasicUrlType.hera_dash_ip, "148"),
    app_crash_monitor("app_crash_monitor","应用宕机",MetricsUnit.UNIT_TAI, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic,true),

    k8s_container_cpu_use_rate("k8s_container_cpu_use_rate","k8s容器机CPU使用率", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic, BasicUrlType.cn_grafana_ip, "2"),
    k8s_container_cpu_average_load("k8s_container_cpu_average_load","k8s容器负载", MetricsUnit.UNIT_NULL, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic, BasicUrlType.cn_grafana_ip, "9"),
    k8s_container_mem_use_rate("k8s_container_mem_use_rate","k8s容器机内存使用率",MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic, BasicUrlType.cn_grafana_ip, "11"),
    k8s_container_count_monitor("k8s_container_count_monitor","k8s容器数量",MetricsUnit.UNIT_TAI, SendAlertGroupKey.APP, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic, BasicUrlType.hera_dash_ip, "148"),

    //资源利用率报警
    container_cpu_resource_use_rate("container_cpu_resource_use_rate","容器CPU资源利用率（1d）", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic, BasicUrlType.cn_grafana_ip_1d, "2"),
    container_mem_resource_use_rate("container_mem_resource_use_rate","容器内存资源利用率（1d）", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic, BasicUrlType.cn_grafana_ip_1d, "11"),
    k8s_cpu_resource_use_rate("k8s_cpu_resource_use_rate","k8s容器CPU资源利用率（1d）", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic, BasicUrlType.cn_grafana_ip_1d, "2"),
    k8s_mem_resource_use_rate("k8s_mem_resource_use_rate","k8s容器内存资源利用率（1d）", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic, BasicUrlType.cn_grafana_ip_1d, "11"),

    k8s_cpu_avg_use_rate("k8s_cpu_avg_use_rate","k8s容器CPU平均使用率", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic, BasicUrlType.cn_grafana_ip_1d, "2"),
//    k8s_pod_restart_times("k8s_pod_restart_times","k8s-POD重启", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic, true,BasicUrlType.cn_grafana_ip_1d, "2"),

    /**
     * jvm指标
     */

    jvm_heap_mem_use_rate("jvm_heap_mem_use_rate","HeapUsed", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic, BasicUrlType.hera_dash_sip, "176"),
    jvm_no_heap_mem_use_rate("jvm_no_heap_mem_use_rate","Non-HeapUsed", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic,BasicUrlType.hera_dash_sip, "178"),
    jvm_thread_num("jvm_thread_num","线程数量", MetricsUnit.UNIT_NULL, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic, BasicUrlType.hera_dash_sip, "68"),
    jvm_gc_times("jvm_gc_times","GC次数", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic, BasicUrlType.hera_dash_sip, "74"),
    jvm_gc_cost("jvm_gc_cost","GC耗时", MetricsUnit.UNIT_S, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic, BasicUrlType.hera_dash_sip, "76"),
    jvm_full_gc_times("jvm_full_gc_times","FullGC次数", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic, BasicUrlType.hera_dash_sip, "74"),
    jvm_full_gc_cost("jvm_full_gc_cost","FullGC耗时", MetricsUnit.UNIT_S, SendAlertGroupKey.APP_INSTANCE, AlarmStrategyType.SYSTEM,InterfaceMetricTypes.basic, BasicUrlType.hera_dash_sip, "76"),

    /**
     * 业务指标-http
     */
    http_error_times("http_error_times","Http调入异常数", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.error_times),
    http_availability("http_availability","Http调入可用性", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.availability),
    http_qps("http_qps","Http调入qps", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.qps, BasicUrlType.hera_dash_sip, "116"),
    http_cost("http_cost","Http调入平均耗时", MetricsUnit.UNIT_MS, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.time_cost, BasicUrlType.hera_dash_sip, "128"),
    http_slow_query("http_slow_query","Http调入慢查询", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.slow_times),

    http_client_error_times("http_client_error_times","Http调出异常数", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.error_times),
    http_client_availability("http_client_availability","Http调出可用性", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.availability),
    http_client_qps("http_client_qps","Http调出qps", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.qps, BasicUrlType.hera_dash_sip, "172"),
    http_client_cost("http_client_cost","Http调出平均耗时", MetricsUnit.UNIT_MS, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.time_cost, BasicUrlType.hera_dash_sip, "173"),
    http_client_slow_query("http_client_slow_query","Http调出慢查询", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.slow_times),

    /**
     * 业务指标-dobbo
     */

    dubbo_provider_error_times("dubbo_provider_error_times","Dubbo调入异常数", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.error_times),
    dubbo_provider_availability("dubbo_provider_availability","Dubbo调入可用性", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.availability),
    dubbo_provider_qps("dubbo_provider_qps","Dubbo调入qps", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.qps, BasicUrlType.hera_dash_sip, "118"),
    dubbo_provider_cost("dubbo_provider_cost","Dubbo调入平均耗时", MetricsUnit.UNIT_MS, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.time_cost, BasicUrlType.hera_dash_sip, "169"),
    dubbo_provider_slow_query("dubbo_provider_slow_query","Dubbo调入慢查询数", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.slow_times),

    dubbo_error_times("dubbo_error_times","Dubbo调出异常数", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.error_times),
    dubbo_availability("dubbo_availability","Dubbo调出可用性", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.availability),
    dubbo_qps("dubbo_qps","Dubbo调出qps", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.qps, BasicUrlType.hera_dash_sip, "150"),
    dubbo_cost("dubbo_cost","Dubbo调出平均耗时", MetricsUnit.UNIT_MS, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.time_cost, BasicUrlType.hera_dash_sip, "130"),
    dubbo_slow_query("dubbo_slow_query","Dubbo调出慢查询数", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.slow_times),

    dubbo_sla_error_times("dubbo_sla_error_times","DubboSLA异常数", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.error_times),
    dubbo_sla_availability("dubbo_sla_availability","DubboSLA可用性", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.availability),

    /**
     * 业务指标-db
     */
    db_error_times("db_error_times","mysql异常数", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_SQL_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.error_times),
    db_availability("db_availability","mysql可用性", MetricsUnit.UNIT_PERCENT, SendAlertGroupKey.APP_SQL_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.availability),
//    db_avg_time_cost("db_avg_time_cost","DB平均响应时间"),
    db_slow_query("db_slow_query","mysql慢查询数", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_SQL_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.slow_times),


    redis_error_times("redis_error_times","redis异常数", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_SQL_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.error_times),
    redis_slow_query("redis_slow_query","redis慢查询数", MetricsUnit.UNIT_COUNT, SendAlertGroupKey.APP_SQL_METHOD, AlarmStrategyType.INTERFACE,InterfaceMetricTypes.slow_times),

    ;
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
    private BasicUrlType basicUrlType;
    private String viewPanel;
    private String env;
    private String domain;


    AlarmPresetMetrics(String code, String message, MetricsUnit unit, SendAlertGroupKey groupKey, AlarmStrategyType strategyType, InterfaceMetricTypes metricType){
        this.code = code;
        this.message = message;
        this.unit = unit;
        this.groupKey = groupKey;
        this.strategyType = strategyType;
        this.metricType = metricType;
    }

    AlarmPresetMetrics(String code, String message, MetricsUnit unit, SendAlertGroupKey groupKey, AlarmStrategyType strategyType, InterfaceMetricTypes metricType, BasicUrlType basicUrlType, String viewPanel){
        this.code = code;
        this.message = message;
        this.unit = unit;
        this.groupKey = groupKey;
        this.strategyType = strategyType;
        this.metricType = metricType;
        this.basicUrlType = basicUrlType;
        this.viewPanel = viewPanel;
    }

    AlarmPresetMetrics(String code, String message, MetricsUnit unit, SendAlertGroupKey groupKey, AlarmStrategyType strategyType, BasicUrlType basicUrlType, String env, String domain, String viewPanel){
        this.code = code;
        this.message = message;
        this.unit = unit;
        this.groupKey = groupKey;
        this.strategyType = strategyType;
        this.metricType = metricType;
        this.basicUrlType = basicUrlType;
        this.env = env;
        this.domain = domain;
        this.viewPanel = viewPanel;
    }

    AlarmPresetMetrics(String code, String message, MetricsUnit unit, SendAlertGroupKey groupKey, AlarmStrategyType strategyType, InterfaceMetricTypes metricType, Boolean hideValueConfig){
        this.code = code;
        this.message = message;
        this.unit = unit;
        this.groupKey = groupKey;
        this.strategyType = strategyType;
        this.metricType = metricType;
        this.hideValueConfig = hideValueConfig;
    }

    AlarmPresetMetrics(String code, String message, MetricsUnit unit, SendAlertGroupKey groupKey, AlarmStrategyType strategyType, InterfaceMetricTypes metricType, Boolean hideValueConfig, BasicUrlType basicUrlType, String viewPanel){
        this.code = code;
        this.message = message;
        this.unit = unit;
        this.groupKey = groupKey;
        this.strategyType = strategyType;
        this.metricType = metricType;
        this.hideValueConfig = hideValueConfig;
        this.basicUrlType = basicUrlType;
        this.viewPanel = viewPanel;
    }

    AlarmPresetMetrics(String code, String message, String errorMetric, String totalMetric, String slowQueryMetric, String timeCostMetric, MetricsUnit unit, SendAlertGroupKey groupKey, AlarmStrategyType strategyType, InterfaceMetricTypes metricType){
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

    AlarmPresetMetrics(String code, String message, String errorMetric, String totalMetric, String slowQueryMetric, String timeCostMetric, MetricsUnit unit, SendAlertGroupKey groupKey, AlarmStrategyType strategyType, InterfaceMetricTypes metricType, BasicUrlType basicUrlType, String viewPanel){
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
        this.basicUrlType = basicUrlType;
        this.viewPanel = viewPanel;
    }

    AlarmPresetMetrics(String code, String message, String errorMetric, String totalMetric, String slowQueryMetric, String timeCostMetric, MetricsUnit unit, SendAlertGroupKey groupKey, AlarmStrategyType strategyType, InterfaceMetricTypes metricType, Boolean hideValueConfig){
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

    public Boolean getHideValueConfig() {
        return hideValueConfig;
    }

    public String getEnv() {
        return env;
    }

    public String getDomain() {
        return domain;
    }

    public String getViewPanel() {
        return viewPanel;
    }

    public BasicUrlType getBasicUrlType(){
        return basicUrlType;
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

}
