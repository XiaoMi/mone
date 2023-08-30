package com.xiaomi.mone.monitor.service.prometheus;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.bo.*;
import com.xiaomi.mone.monitor.dao.model.AppAlarmRule;
import com.xiaomi.mone.monitor.dao.model.AppMonitor;
import com.xiaomi.mone.monitor.enums.BasicAlarmLevel;
import com.xiaomi.mone.monitor.enums.KeyCenterRequestType;
import com.xiaomi.mone.monitor.pojo.AlarmPresetMetricsPOJO;
import com.xiaomi.mone.monitor.pojo.ReqErrorMetricsPOJO;
import com.xiaomi.mone.monitor.pojo.ReqSlowMetricsPOJO;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.AppMonitorService;
import com.xiaomi.mone.monitor.service.alertmanager.AlertServiceAdapt;
import com.xiaomi.mone.monitor.service.api.AlarmPresetMetricsService;
import com.xiaomi.mone.monitor.service.api.AlarmServiceExtension;
import com.xiaomi.mone.monitor.service.api.MetricsLabelKindService;
import com.xiaomi.mone.monitor.service.api.ReqErrorMetricsService;
import com.xiaomi.mone.monitor.service.api.ReqSlowMetricsService;
import com.xiaomi.mone.monitor.service.api.TeslaService;
import com.xiaomi.mone.monitor.service.helper.AlertUrlHelper;
import com.xiaomi.mone.monitor.service.model.PageData;
import com.xiaomi.mone.monitor.service.model.prometheus.AlarmRuleData;
import com.xiaomi.mone.monitor.service.model.prometheus.Metric;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author gaoxihui
 * @date 2021/9/5 5:24 下午
 */
@Slf4j
@Service
public class AlarmService {

    /**
     * 业务异常指标
     */
    private static final String http_error_metric = "httpError";
    private static final String http_client_error_metric = "httpClientError";//http client 错误数
    private static final String db_error_metric = "dbError";
    private static final String oracle_error_metric = "oracleError";
    private static final String dubbo_consumer_error_metric = "dubboConsumerError";
    private static final String dubbo_provider_error_metric = "dubboProviderError";
    private static final String dubbo_provier_sla_error_metric = "dubboProviderSLAError";
    private static final String redis_error_metric = "redisError";
    private static final String es_error_metric = "elasticsearchClientError";
    private static final String hbase_error_metric = "hbaseClientError";

    /**
     * 业务慢查询指标
     */
    private static final String http_slow_query_metric = "httpSlowQuery";
    private static final String http_client_slow_query_metric = "httpClientSlowQuery";//http client 慢查询
    private static final String dubbo_consumer_slow_query_metric = "dubboConsumerSlowQuery";
    private static final String dubbo_provider_slow_query_metric = "dubboProviderSlowQuery";
    private static final String db_slow_query_metric = "dbSlowQuery";
    private static final String oracle_slow_query_metric = "oracleSlowQuery";
    private static final String redis_slow_query_metric = "redisSlowQuery";
    private static final String es_slow_query_metric = "elasticsearchClientSlowQuery";
    private static final String hbase_slow_query_metric = "hbaseClientSlowQuery";


    /**
     * 可用率指标
     */
    //http
    private static final String http_avalible_success_metric = "aopSuccessMethodCount";
    private static final String http_avalible_total_metric = "aopTotalMethodCount";
    private static final String http_method_time_count = "aopMethodTimeCount";

    private static final String http_client_method_total_metric = "aopClientTotalMethodCount";
    private static final String http_client_method_success_metric = "aopClientSuccessMethodCount";
    private static final String http_client_method_time_count = "aopClientMethodTimeCount";

    //dubbo
    private static final String dubbo_avalible_success_metric = "dubboBisSuccessCount";
    private static final String dubbo_avalible_total_metric = "dubboBisTotalCount";
    private static final String dubbo_provider_avalible_total_metric = "dubboMethodCalledCount";
    private static final String dubbo_provider_sla_avalible_total_metric = "dubboProviderSLACount";
    private static final String dubbo_consumer_time_cost = "dubboConsumerTimeCost";
    private static final String dubbo_provider_time_cost = "dubboProviderCount";
    //db
    private static final String db_avalible_success_metric = "sqlSuccessCount";
    private static final String db_avalible_total_metric = "sqlTotalCount";
    private static final String oracle_avalible_total_metric = "oracleTotalCount";
    private static final String es_avalible_total_metric = "elasticsearchClient";
    private static final String hbase_avalible_total_metric = "hbaseClient";


    /**
     * 可用率默认计算时间区间 30s
     */
    private static final String avalible_duration_time = "30s";

    public static final String alarm_staging_env = "staging";
    public static final String alarm_online_env = "production";
    public static final String alarm_preview_env = "preview";

    public static final Integer stagingDefaultIamId = 16360;
    public static final Integer onlineDefaultIamId = 15272;

    private static final String metric_total_suffix = "_total";
    private static final String metric_sum_suffix = "_sum";
    private static final String metric_count_suffix = "_count";


    @NacosValue("${iam.ak:noconfig}")
    private String cloudAk;
    @NacosValue("${iam.sk:noconfig}")
    private String cloudSk;

    @NacosValue(value = "${prometheus.alarm.env:staging}",autoRefreshed = true)
    private String prometheusAlarmEnv;

    @Value("${server.type}")
    private String serverType;

    @NacosValue(value = "${rule.evaluation.interval:30}",autoRefreshed = true)
    private Integer evaluationInterval;

    @NacosValue(value = "${rule.evaluation.duration:30}",autoRefreshed = true)
    private Integer evaluationDuration;

    @NacosValue(value = "${rule.evaluation.unit:s}",autoRefreshed = true)
    private String evaluationUnit;

    @NacosValue(value = "${tesla.alert.intranet.url:noconfig}",autoRefreshed = true)
    private String teslaAlertIntranetUrl;

    @NacosValue(value = "${tesla.alert.intranet.grafana.url:noconfig}",autoRefreshed = true)
    private String teslaAlertIntranetGrafanaUrl;

    @NacosValue(value = "${tesla.alert.outnet.grafana.url:noconfig}",autoRefreshed = true)
    private String teslaAlertOutnetGrafanaUrl;

    @NacosValue(value = "${tesla.alert.outnet.url:noconfig}",autoRefreshed = true)
    private String teslaAlertOutnetUrl;

    @NacosValue(value = "${tesla.alert.time.cost.url:noconfig}",autoRefreshed = true)
    private String teslaAlertTimeCostnetUrl;

    @NacosValue(value = "${resource.use.rate.url}",autoRefreshed = true)
    private String resourceUseRateUrl;

    @Value("${alert.manager.env:staging}")
    private String alertManagerEnv;

    @Autowired
    PrometheusService prometheusService;

    @Autowired
    AppMonitorService appMonitorService;

    @Autowired
    AlertServiceAdapt alertServiceAdapt;

    @Autowired
    private AlertUrlHelper alertUrlHelper;

    @Autowired
    private AlarmPresetMetricsService alarmPresetMetricsService;

    @Autowired
    private MetricsLabelKindService metricsLabelKindService;

    @Autowired
    private ReqErrorMetricsService reqErrorMetricsService;

    @Autowired
    private ReqSlowMetricsService reqSlowMetricsService;

    @Autowired
    private TeslaService teslaService;

    @Autowired
    private AlarmServiceExtension alarmServiceExtension;

    public String getExpr(AppAlarmRule rule,String scrapeIntervel,AlarmRuleData ruleData, AppMonitor app){

        if(StringUtils.isBlank(rule.getAlert())){
            return null;
        }

        Map<String, String> includLabels = new HashMap<>();
        Map<String, String> exceptLabels = new HashMap<>();

        if(metricsLabelKindService.httpType(rule.getAlert())){
            includLabels = getLabels(ruleData, AppendLabelType.http_include_uri);
            Map<String, String> httpIncludeErrorCode = getLabels(ruleData, AppendLabelType.http_include_errorCode);
            includLabels.putAll(httpIncludeErrorCode);

            exceptLabels = getLabels(ruleData, AppendLabelType.http_except_uri);
            Map<String, String> httpExceptErrorCode = getLabels(ruleData, AppendLabelType.http_except_errorCode);
            exceptLabels.putAll(httpExceptErrorCode);

            Map<String, String> httpClientIncludeDomains = getLabels(ruleData, AppendLabelType.http_client_inclue_domain);
            includLabels.putAll(httpClientIncludeDomains);

            Map<String, String> httpClientExcludeDomains = getLabels(ruleData, AppendLabelType.http_client_excpet_domain);
            exceptLabels.putAll(httpClientExcludeDomains);
        }

        if(metricsLabelKindService.dubboType(rule.getAlert())){
            includLabels = getLabels(ruleData, AppendLabelType.dubbo_include_method);
            Map<String, String> httpIncludeErrorCode = getLabels(ruleData, AppendLabelType.dubbo_include_service);
            includLabels.putAll(httpIncludeErrorCode);

            exceptLabels = getLabels(ruleData, AppendLabelType.dubbo_except_method);
            Map<String, String> httpExceptErrorCode = getLabels(ruleData, AppendLabelType.dubbo_except_service);
            exceptLabels.putAll(httpExceptErrorCode);
        }


        includLabels.putAll(getEnvLabels(ruleData, true));
        exceptLabels.putAll(getEnvLabels(ruleData, false));

        switch (rule.getAlert()){
            case "http_error_times" :
                return getPresetMetricErrorAlarm(http_error_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());
            case "http_qps" :
                return getPresetMetricQpsAlarm(http_avalible_total_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix, scrapeIntervel,null,rule.getOp(),rule.getValue());
            case "http_cost" :
                return getPresetMetricCostAlarm(http_method_time_count,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels, scrapeIntervel,null, rule.getOp(),rule.getValue());
            case "http_availability":
                return getAvailableRate(http_error_metric,http_avalible_total_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,avalible_duration_time,null,rule.getOp(),rule.getValue());
            case "http_slow_query":
                return getPresetMetricErrorAlarm(http_slow_query_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());
            case "http_client_availability":
                return getAvailableRate(http_client_error_metric,http_client_method_total_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,avalible_duration_time,null,rule.getOp(),rule.getValue());
            case "http_client_error_times" :
                return getPresetMetricErrorAlarm(http_client_error_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());
            case "http_client_cost" :
                return getPresetMetricCostAlarm(http_client_method_time_count,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels, scrapeIntervel,null, rule.getOp(),rule.getValue());
            case "http_client_qps" :
                return getPresetMetricQpsAlarm(http_client_method_total_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix, scrapeIntervel,null,rule.getOp(),rule.getValue());
            case "http_client_slow_query":
                return getPresetMetricErrorAlarm(http_client_slow_query_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());
            case "dubbo_error_times" :
                return getPresetMetricErrorAlarm(dubbo_consumer_error_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());
            case "dubbo_provider_error_times" :
                return getPresetMetricErrorAlarm(dubbo_provider_error_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());
            case "dubbo_qps" :
                return getPresetMetricQpsAlarm(dubbo_avalible_total_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix, scrapeIntervel, null,rule.getOp(),rule.getValue());
            case "dubbo_provider_qps" :
                return getPresetMetricQpsAlarm(dubbo_provider_avalible_total_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix, scrapeIntervel, null,rule.getOp(),rule.getValue());
            case "dubbo_cost" :
                return getPresetMetricCostAlarm(dubbo_consumer_time_cost,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels, scrapeIntervel,null, rule.getOp(),rule.getValue());
            case "dubbo_provider_cost" :
                return getPresetMetricCostAlarm(dubbo_provider_time_cost,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels, scrapeIntervel,null, rule.getOp(),rule.getValue());
            case "dubbo_slow_query":
                return getPresetMetricErrorAlarm(dubbo_consumer_slow_query_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());
            case "dubbo_provider_slow_query":
                return getPresetMetricErrorAlarm(dubbo_provider_slow_query_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());
            case "dubbo_availability":
                return getAvailableRate(dubbo_consumer_error_metric,dubbo_avalible_total_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,avalible_duration_time,null,rule.getOp(),rule.getValue());
            case "dubbo_provider_availability":
                return getAvailableRate(dubbo_provider_error_metric,dubbo_provider_avalible_total_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,avalible_duration_time,null,rule.getOp(),rule.getValue());

            case "dubbo_sla_error_times":
                return getPresetMetricSLAErrorAlarm(dubbo_provier_sla_error_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());
            case "dubbo_sla_availability":
                return getSlaAvailableRate(dubbo_provier_sla_error_metric,dubbo_provider_sla_avalible_total_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,avalible_duration_time,null,rule.getOp(),rule.getValue());

            case "db_error_times":
                return getPresetMetricErrorAlarm(db_error_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());
            case "db_slow_query":
                return getPresetMetricErrorAlarm(db_slow_query_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());
            case "db_availability":
                return getAvailableRate(db_error_metric,db_avalible_total_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,avalible_duration_time,null,rule.getOp(),rule.getValue());
            case "oracle_error_times":
                return getPresetMetricErrorAlarm(oracle_error_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());
            case "oracle_slow_query":
                return getPresetMetricErrorAlarm(oracle_slow_query_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());
            case "oracle_availability":
                return getAvailableRate(oracle_error_metric,oracle_avalible_total_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,avalible_duration_time,null,rule.getOp(),rule.getValue());
            case "hbase_error_times":
                return getPresetMetricErrorAlarm(hbase_error_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());
            case "hbase_slow_query":
                return getPresetMetricErrorAlarm(hbase_slow_query_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());
            case "hbase_availability":
                return getAvailableRate(hbase_error_metric,hbase_avalible_total_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,avalible_duration_time,null,rule.getOp(),rule.getValue());
            case "redis_error_times":
                return getPresetMetricErrorAlarm(redis_error_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());
            case "redis_slow_query":
                return getPresetMetricErrorAlarm(redis_slow_query_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());

            case "es_error_times":
                return getPresetMetricErrorAlarm(es_error_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());
            case "es_slow_query":
                return getPresetMetricErrorAlarm(es_slow_query_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());
            case "es_availability":
                return getAvailableRate(es_error_metric,es_avalible_total_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,avalible_duration_time,null,rule.getOp(),rule.getValue());
            case "container_cpu_use_rate":
                return getContainerCpuAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),false,ruleData);
            case "container_cpu_average_load":
                return getContainerLoadAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),false,ruleData);
            case "container_mem_use_rate":
                return getContainerMemAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),false,ruleData);
            case "container_count_monitor":
                return getContainerCountAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),false,ruleData);
            case "app_restart_monitor":
                return getAppRestartAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),false);
            case "app_crash_monitor":
                return getAppCrashAlarmExpr(rule.getProjectId(),app.getProjectName(),ruleData);

            case "container_cpu_resource_use_rate":
                return getContainerCpuResourceAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),false,ruleData);
            case "container_mem_resource_use_rate":
                return getContainerMemReourceAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),false,ruleData);
            case "container_disk_use_rate":
                return getContainerDiskReourceAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),false,ruleData);

            case "k8s_container_cpu_use_rate":
                return getContainerCpuAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),true,ruleData);
            case "k8s_container_cpu_average_load":
                return getContainerLoadAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),true,ruleData);
            case "k8s_container_mem_use_rate":
                return getContainerMemAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),true,ruleData);
            case "k8s_container_count_monitor":
                return getContainerCountAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),true,ruleData);

            case "k8s_cpu_resource_use_rate":
                return getContainerCpuResourceAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),true,ruleData);
            case "k8s_mem_resource_use_rate":
                return getContainerMemReourceAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),true,ruleData);

            case "k8s_cpu_avg_use_rate":
                return getK8sCpuAvgUsageAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),ruleData);

            case "k8s_pod_restart_times":
                return getK8sPodRestartExpr(rule.getProjectId(),app.getProjectName(),ruleData);

            case "jvm_heap_mem_use_rate":
                return getJvmMemAlarmExpr(rule.getProjectId(),app.getProjectName(),"heap", rule.getOp(), rule.getValue(),ruleData);
            case "jvm_no_heap_mem_use_rate":
                return getJvmMemAlarmExpr(rule.getProjectId(),app.getProjectName(),"nonheap", rule.getOp(), rule.getValue(),ruleData);
            case "jvm_thread_num":
                return getJvmThreadAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData);
            case "jvm_gc_cost":
                return getJvmGcCostExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),false,ruleData);
            case "jvm_gc_times":
                return getJvmGcCountExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),false,ruleData);
            case "jvm_full_gc_cost":
                return getJvmGcCostExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),true,ruleData);
            case "jvm_full_gc_times":
                return getJvmGcCountExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),true,ruleData);

            //cpu usage
            case "container_cpu_use_rate_new":
                return getCpuUsageAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.container);
            case "instance_cpu_use_rate":
                return getCpuUsageAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.instance);
            case "cluster_cpu_use_rate":
                return getCpuUsageAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.cluster);

            //cpu average load
            case "container_cpu_average_load_new":
                return getCpuLoadAverageAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.container);
            case "instance_cpu_average_load":
                return getCpuLoadAverageAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.instance);
            case "cluster_cpu_average_load":
                return getCpuLoadAverageAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.cluster);

            //mem usage
            case "container_mem_use_rate_new":
                return getMemUsageAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.container);
            case "instance_mem_use_rate":
                return getMemUsageAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.instance);
            case "cluster_mem_use_rate":
                return getMemUsageAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.cluster);

            //disk has used
            case "container_disk_has_used":
                return getDiskHasUsedAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.container);
            case "instance_disk_has_used":
                return getDiskHasUsedAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.instance);

            //disk usage
            case "instance_disk_use_rate":
                return getDiskUsageAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.instance);
            //log disk usage
            case "instance_log_dir_usage":
                return getLogDirDiskUsageAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData);

            //disk write
            case "container_disk_write_5m":
                return getDiskWriteAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.container);
            case "instance_disk_write_5m":
                return getDiskWriteAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.instance);

            //disk read
            case "container_disk_read_5m":
                return getDiskReadAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.container);
            case "instance_disk_read_5m":
                return getDiskReadAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.instance);

            //net work transmit
            case "instance_network_transmit_5m":
                return getNetworkTransmitAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.cluster);
            case "cluster_network_transmit_5m":
                return getNetworkTransmitAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.instance);

            //net work receive
            case "cluster_network_receive_5m":
                return getNetworkReceiveAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.cluster);
            case "instance_network_receive_5m":
                return getNetworkReceiveAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.instance);

            //container restart
            case "container_restart_5m":
                return getContainerRestartAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.container);
            case "instance_restart_5m":
                return getContainerRestartAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.instance);

            //container process num
            case "container_process_num":
                return getProcessNumAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.container);
            case "instance_process_num":
                return getProcessNumAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.instance);

            //container  process num
            case "container_thread_num":
                return getThreadsNumAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.container);
            case "instance_thread_num":
                return getThreadsNumAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.instance);

            //container  process num
            case "container_socket_num":
                return getSocketNumAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.container);
            case "instance_socket_num":
                return getSocketNumAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,BasicAlarmLevel.instance);

            //container  key center request
            case "thrift_request_kc_agent_fail_num":
                return getKeyCenterRequestAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,KeyCenterRequestType.thrift);
            case "http_request_kc_agent_fail_num":
                return getKeyCenterRequestAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData,KeyCenterRequestType.http);

            case "instance_tpc_listen_drops_num":
                return getTpcNumAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData);

            case "instance_file_descriptor_num":
                return getFileDescriptorsAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData);

            case "cluster_instance_exception_num":
                return getDeployUnitExceptionInstanceAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData);

            case "cluster_instance_valid_rate":
                return getDeployUnitInstanceUsageAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),ruleData);

            default:

                if (rule.getAlert().endsWith("_tesla_availability")) {
                    return getTeslaAvailability(ruleData);
                }

                if(rule.getAlert().endsWith("_tesla_p99_time_cost")){
                    return getTeslaTimeCost4P99(ruleData);
                }


                AlarmPresetMetricsPOJO presetMetric = alarmPresetMetricsService.getByCode(rule.getAlert());
                if(presetMetric == null){
                    log.error("no metric found for code :{},ruleData:{},app{}",rule.getAlert(),ruleData,app);
                    return null;
                }

                /**
                 * rpc系错误数报警
                 */
                if(rule.getAlert().endsWith("_error_times")){
                    return getPresetMetricErrorAlarm(presetMetric.getErrorMetric(),rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());
                }

                /**
                 * rpc系可用率报警
                 */
                if(rule.getAlert().endsWith("_availability")){
                    return getAvailableRate(presetMetric.getErrorMetric(),presetMetric.getTotalMetric(),rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,avalible_duration_time,null,rule.getOp(),rule.getValue());
                }

                /**
                 * rpc系qps
                 */
                if(rule.getAlert().endsWith("_qps")){
                    return getPresetMetricQpsAlarm(presetMetric.getTotalMetric(),rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix, scrapeIntervel, null,rule.getOp(),rule.getValue());
                }

                /**
                 * rpc系慢查询
                 */
                if(rule.getAlert().endsWith("_slow_times")){
                    return getPresetMetricErrorAlarm(presetMetric.getSlowQueryMetric(),rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());
                }

                /**
                 * rpc系耗时
                 */
                if(rule.getAlert().endsWith("_time_cost")){
                    return getPresetMetricCostAlarm(presetMetric.getTimeCostMetric(),rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels, scrapeIntervel,null, rule.getOp(),rule.getValue());
                }

                return null;

        }
    }

    @Value("${server.type}")
    private String env;

    public String getTeslaTimeCost4P99(AlarmRuleData rule){
        return teslaService.getTeslaTimeCost4P99(rule);
    }

    public String getTeslaAvailability(AlarmRuleData rule){
        return teslaService.getTeslaAvailability(rule);
    }


    private Map<String,String> getLabels(AlarmRuleData ruleData, AppendLabelType appendLabelType){
        Map<String,String> map = new HashMap<>();
        switch (appendLabelType){
            case http_include_uri :
                fillLabels(map, PresetMetricLabels.http_uri.getLabelName(),ruleData.getIncludeMethods());
                return map;
            case http_except_uri :
                fillLabels(map, PresetMetricLabels.http_uri.getLabelName(),ruleData.getExceptMethods());
                return map;
            case http_include_errorCode :
                fillLabels(map, PresetMetricLabels.http_error_code.getLabelName(),ruleData.getIncludeErrorCodes().replaceAll("4xx","4.*").replaceAll("5xx","5.*"));
                return map;
            case http_except_errorCode :
                fillLabels(map, PresetMetricLabels.http_error_code.getLabelName(),ruleData.getExceptErrorCodes().replaceAll("4xx","4.*").replaceAll("5xx","5.*"));
                return map;
            case http_client_inclue_domain :
                fillLabels(map, PresetMetricLabels.http_client_server_domain.getLabelName(),ruleData.getIncludeHttpDomains());
                return map;
            case http_client_excpet_domain :
                fillLabels(map, PresetMetricLabels.http_client_server_domain.getLabelName(),ruleData.getExceptHttpDomains());
                return map;
            case dubbo_include_method :
                fillLabels(map, PresetMetricLabels.dubbo_method.getLabelName(),ruleData.getIncludeMethods());
                return map;
            case dubbo_except_method :
                fillLabels(map, PresetMetricLabels.dubbo_method.getLabelName(),ruleData.getExceptMethods());
                return map;
            case dubbo_include_service:
                fillLabels(map, PresetMetricLabels.dubbo_service.getLabelName(),ruleData.getIncludeDubboServices());
                return map;
            case dubbo_except_service:
                fillLabels(map, PresetMetricLabels.dubbo_service.getLabelName(),ruleData.getExceptDubboServices());
                return map;
            default:
                return map;
        }

    }

    private Map<String,String> getEnvLabels(AlarmRuleData ruleData,boolean isInclude){
        Map<String,String> map = new HashMap<>();
        if(isInclude){
            if(!CollectionUtils.isEmpty(ruleData.getIncludeEnvs())){
                fillLabels(map,"serverEnv",String.join(",",ruleData.getIncludeEnvs()));
            }
            if(!CollectionUtils.isEmpty(ruleData.getIncludeZones())){
                fillLabels(map,"serverZone",String.join(",",ruleData.getIncludeZones()));
            }

//            if(!CollectionUtils.isEmpty(ruleData.getIncludeModules())){
//                fillLabels(map,"functionModule",String.join(",",ruleData.getIncludeModules()));
//            }

            if(!CollectionUtils.isEmpty(ruleData.getIncludeFunctions())){
                fillLabels(map,"functionId",String.join(",",ruleData.getIncludeFunctions()));
            }

            if(!CollectionUtils.isEmpty(ruleData.getIncludeContainerName())){
                fillLabels(map,"containerName",String.join(",",ruleData.getIncludeContainerName()));
            }

        }
        if(!isInclude){
            if(!CollectionUtils.isEmpty(ruleData.getExceptEnvs())){
                fillLabels(map,"serverEnv",String.join(",",ruleData.getExceptEnvs()));
            }
            if(!CollectionUtils.isEmpty(ruleData.getExceptZones())){
                fillLabels(map,"serverZone",String.join(",",ruleData.getExceptZones()));
            }

//            if(!CollectionUtils.isEmpty(ruleData.getExceptModules())){
//                fillLabels(map,"functionModule",String.join(",",ruleData.getExceptModules()));
//            }
            if(!CollectionUtils.isEmpty(ruleData.getExceptFunctions())){
                fillLabels(map,"functionId",String.join(",",ruleData.getExceptFunctions()));
            }

            if(!CollectionUtils.isEmpty(ruleData.getExceptContainerName())){
                fillLabels(map,"containerName",String.join(",",ruleData.getExceptContainerName()));
            }

        }
        return map;
    }

    private void fillLabels(Map<String,String> map,String key,String values){
        if(StringUtils.isNotBlank(values)){
            String[] uris = values.split(",");
            if(uris.length > 0){
                StringBuilder labelValues = new StringBuilder();
                for(String lv : uris){
                    labelValues.append(lv).append("|");
                }

                log.debug("labelValues:{}",labelValues.toString());
                map.put(key,labelValues.toString().substring(0,(labelValues.length() - 1)));
            }

        }
    }

    public String getAvailableRate(String errorMetric,String totalMetric,Integer projectId,String projectName,Map includeLabels,Map exceptLabels,String metricSuffix,String duration,String offset,String op,Float value){

        String errorMetricComplete = prometheusService.completeMetricForAlarm(errorMetric, includeLabels,exceptLabels, projectId,projectName, metricSuffix,  duration, null);

        if(!CollectionUtils.isEmpty(includeLabels)){
            Iterator iterator = includeLabels.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry next = (Map.Entry) iterator.next();
                if(next.getKey().equals(PresetMetricLabels.http_error_code.getLabelName())){
                    iterator.remove();
                }
            }
        }

        if(!CollectionUtils.isEmpty(exceptLabels)){
            Iterator iterator = exceptLabels.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry next = (Map.Entry) iterator.next();
                if(next.getKey().equals(PresetMetricLabels.http_error_code.getLabelName())){
                    iterator.remove();
                }
            }
        }


        String totalMetricComplate = prometheusService.completeMetricForAlarm(totalMetric, includeLabels,exceptLabels, projectId,projectName, metricSuffix,  duration, null);

        StringBuilder expBuilder = new StringBuilder();
        expBuilder
                .append("clamp_min((1-(")
                .append("sum(sum_over_time(").append(errorMetricComplete).append("))").append(" by (application,system,serverIp,serviceName,methodName,sqlMethod,serverEnv,serverZone,containerName,sql,dataSource,functionModule,functionName)")
                .append("/")
                .append("sum(sum_over_time(").append(totalMetricComplate).append("))").append(" by (application,system,serverIp,serviceName,methodName,sqlMethod,serverEnv,serverZone,containerName,sql,dataSource,functionModule,functionName)")
                .append(")),0) * 100")
                .append(op).append(value);


        log.info("AlarmService.getAvailableRate param" +
                        ":errorMetric:{},totalMetric:{},projectId:{},projectName:{},includeLabels:{},exceptLabels:{},metricSuffix:{},duration:{},offset:{},op:{},value:{},return : {}"
                ,errorMetric,totalMetric,projectId,projectName,includeLabels,exceptLabels,metricSuffix,duration,offset,op,value,expBuilder.toString());
        return expBuilder.toString();
    }

    public String getSlaAvailableRate(String errorMetric,String totalMetric,Integer projectId,String projectName,Map includeLabels,Map exceptLabels,String metricSuffix,String duration,String offset,String op,Float value){

        String errorMetricComplete = prometheusService.completeMetricForAlarm(errorMetric, includeLabels,exceptLabels, projectId,projectName, metricSuffix,  duration, null);

        if(!CollectionUtils.isEmpty(includeLabels)){
            Iterator iterator = includeLabels.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry next = (Map.Entry) iterator.next();
                if(next.getKey().equals(PresetMetricLabels.http_error_code.getLabelName())){
                    iterator.remove();
                }
            }
        }

        if(!CollectionUtils.isEmpty(exceptLabels)){
            Iterator iterator = exceptLabels.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry next = (Map.Entry) iterator.next();
                if(next.getKey().equals(PresetMetricLabels.http_error_code.getLabelName())){
                    iterator.remove();
                }
            }
        }


        String totalMetricComplate = prometheusService.completeMetricForAlarm(totalMetric, includeLabels,exceptLabels, projectId,projectName, metricSuffix,  duration, null);

        StringBuilder expBuilder = new StringBuilder();
        expBuilder
                .append("clamp_min((1-(")
                .append("sum(sum_over_time(").append(errorMetricComplete).append("))").append(" by (application,system,serverIp,serviceName,methodName,sqlMethod,serverEnv,serverZone,containerName,sql,dataSource,functionModule,functionName,clientProjectId,clientProjectName,clientEnv,clientEnvId,clientIp)")
                .append("/")
                .append("sum(sum_over_time(").append(totalMetricComplate).append("))").append(" by (application,system,serverIp,serviceName,methodName,sqlMethod,serverEnv,serverZone,containerName,sql,dataSource,functionModule,functionName,clientProjectId,clientProjectName,clientEnv,clientEnvId,clientIp)")
                .append(")),0) * 100")
                .append(op).append(value);


        log.info("AlarmService.getSlaAvailableRate param" +
                        ":errorMetric:{},totalMetric:{},projectId:{},projectName:{},includeLabels:{},exceptLabels:{},metricSuffix:{},duration:{},offset:{},op:{},value:{},return : {}"
                ,errorMetric,totalMetric,projectId,projectName,includeLabels,exceptLabels,metricSuffix,duration,offset,op,value,expBuilder.toString());
        return expBuilder.toString();
    }

    /**
     * basic alarm -cpu usage alarm
     * @param projectId
     * @param projectName
     * @param op
     * @param value
     * @param ruleData
     * @param alarmLevel
     * @return
     */
    public String getCpuUsageAlarmExpr(Integer projectId, String projectName, String op, double value, AlarmRuleData ruleData, BasicAlarmLevel alarmLevel){

        if(alarmLevel == null || !BasicAlarmLevel.isValid(alarmLevel)){
            log.error("getCpuUsageAlarmExpr no valid alarmLevel assign! alarmLevel : {}",alarmLevel);
            return null;
        }

        String projectSign = new StringBuilder().append(projectId).append("_").append(projectName.replaceAll("-","_")).toString();
        String sumBy = null;
        StringBuilder metric_usage_builder = new StringBuilder();
        StringBuilder metric_quota_builder = new StringBuilder();

        String labelProperties = getEnvLabelProperties(ruleData);

        switch (alarmLevel){
            case cluster :
                sumBy = "sum by(application, serverZone, serverEnv, deploy_space, cluster, system)";
                metric_usage_builder.append("container_cpu_usage_seconds_total{container!='', application='").append(projectSign).append("'");
                metric_quota_builder.append("container_spec_cpu_quota{container!='', application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder.append(",").append(labelProperties);
                    metric_quota_builder.append(",").append(labelProperties);
                }
                metric_usage_builder.append("}");
                metric_quota_builder.append("}");

            case container:
                sumBy = "sum by(application, serverZone, serverEnv, pod, container, id, system, ip)";
                metric_usage_builder.append("container_cpu_usage_seconds_total{container!='', pod!='POD', application='").append(projectSign).append("'");
                metric_quota_builder.append("container_spec_cpu_quota{container!='', pod!='POD', application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder.append(",").append(labelProperties);
                    metric_quota_builder.append(",").append(labelProperties);
                }
                metric_usage_builder.append("}");
                metric_quota_builder.append("}");
            case instance:
                sumBy = "sum by(application, serverZone, serverEnv, pod, system)";
                metric_usage_builder.append("container_cpu_usage_seconds_total{container!='', pod!='', application='").append(projectSign).append("'");
                metric_quota_builder.append("container_spec_cpu_quota{container!='', pod!='', application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder.append(",").append(labelProperties);
                    metric_quota_builder.append(",").append(labelProperties);
                }
                metric_usage_builder.append("}");
                metric_quota_builder.append("}");
            default:
                ;
        }

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("(").append(sumBy).append(" ").append(" (irate(").append(metric_usage_builder.toString()).append("[5m])))");
        exprBuilder.append("/");
        exprBuilder.append("(").append(sumBy).append(" ").append("(").append(metric_quota_builder.toString()).append(") / 100000").append(")");
        exprBuilder.append(" * 100 ");
        exprBuilder.append(op);
        exprBuilder.append(value);

        log.info("getCpuUsageAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getCpuLoadAverageAlarmExpr(Integer projectId, String projectName, String op, double value, AlarmRuleData ruleData, BasicAlarmLevel alarmLevel){

        if(alarmLevel == null || !BasicAlarmLevel.isValid(alarmLevel)){
            log.error("getCpuLoadAverageAlarmExpr no valid alarmLevel assign! alarmLevel : {}",alarmLevel);
            return null;
        }

        String projectSign = new StringBuilder().append(projectId).append("_").append(projectName.replaceAll("-","_")).toString();
        String sumBy = "";
        StringBuilder metric_usage_builder = new StringBuilder();

        String labelProperties = getEnvLabelProperties(ruleData);

        switch (alarmLevel){

            case cluster :
                sumBy = "sum by(application, serverZone, serverEnv, deploy_space, cluster,system) ";
                metric_usage_builder.append("(container_cpu_load_average_10s{application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder.append(",").append(labelProperties);
                }
                metric_usage_builder.append("})");

            case container:
                metric_usage_builder.append("avg_over_time(container_cpu_load_average_10s{image!='',application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder.append(",").append(labelProperties);
                }
                metric_usage_builder.append("}[1m])");

            case instance:
                sumBy = "sum by(application, serverZone, serverEnv, pod,system) ";

                metric_usage_builder.append("(container_cpu_load_average_10s{pod!='',application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder.append(",").append(labelProperties);
                }
                metric_usage_builder.append("})");

            default:
                ;
        }

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append(sumBy);
        exprBuilder.append(metric_usage_builder.toString());
        exprBuilder.append("/");
        exprBuilder.append("1000");
        exprBuilder.append(op);
        exprBuilder.append(value);

        log.info("getCpuLoadAverageAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    /**
     *
     * @param projectId
     * @param projectName
     * @param op
     * @param value
     * @param ruleData
     * @param alarmLevel
     * @return
     */
    public String getMemUsageAlarmExpr(Integer projectId, String projectName, String op, double value, AlarmRuleData ruleData, BasicAlarmLevel alarmLevel){

        if(alarmLevel == null || !BasicAlarmLevel.isValid(alarmLevel)){
            log.error("getMemUsageAlarmExpr no valid alarmLevel assign! alarmLevel : {}",alarmLevel);
            return null;
        }

        String projectSign = new StringBuilder().append(projectId).append("_").append(projectName.replaceAll("-","_")).toString();
        String sumBy = null;
        StringBuilder metric_usage_builder = new StringBuilder();
        StringBuilder metric_quota_builder = new StringBuilder();

        String labelProperties = getEnvLabelProperties(ruleData);

        switch (alarmLevel){
            case cluster :
                sumBy = "sum by(application, serverZone, serverEnv, deploy_space, cluster,system)";
                metric_usage_builder.append("container_memory_rss{container!='', application='").append(projectSign).append("'");
                metric_quota_builder.append("container_spec_memory_limit_bytes{container='', application='").append(projectSign).append("'");

                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder.append(",").append(labelProperties);
                    metric_quota_builder.append(",").append(labelProperties);
                }
                metric_usage_builder.append("}");
                metric_quota_builder.append("}");
            case container:
                sumBy = "sum by(application, serverZone, serverEnv, pod, container, id,system, ip) ";
                metric_usage_builder.append("container_memory_rss{container!='',container!='POD', application='").append(projectSign).append("'");
                metric_quota_builder.append("container_spec_memory_limit_bytes{container!='', pod!='POD', application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder.append(",").append(labelProperties);
                    metric_quota_builder.append(",").append(labelProperties);
                }
                metric_usage_builder.append("}");
                metric_quota_builder.append("}");
            case instance:
                sumBy = "sum by(application, serverZone, serverEnv, pod,system)";
                metric_usage_builder.append("container_memory_rss{container!='', pod!='', application='").append(projectSign).append("'");
                metric_quota_builder.append("container_spec_memory_limit_bytes{container!='', pod!='', application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder.append(",").append(labelProperties);
                    metric_quota_builder.append(",").append(labelProperties);
                }
                metric_usage_builder.append("}");
                metric_quota_builder.append("}");
            default:
                ;
        }

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append(sumBy).append(" (").append(metric_usage_builder.toString()).append(")");
        exprBuilder.append("/");
        exprBuilder.append(sumBy).append(" (").append(metric_quota_builder.toString()).append(")");
        exprBuilder.append(" * 100 ");
        exprBuilder.append(op);
        exprBuilder.append(value);

        log.info("getMemUsageAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getDiskHasUsedAlarmExpr(Integer projectId, String projectName, String op, double value, AlarmRuleData ruleData, BasicAlarmLevel alarmLevel){

        if(alarmLevel == null || !BasicAlarmLevel.isValid(alarmLevel)){
            log.error("getDiskHasUsedAlarmExpr no valid alarmLevel assign! alarmLevel : {}",alarmLevel);
            return null;
        }

        String projectSign = new StringBuilder().append(projectId).append("_").append(projectName.replaceAll("-","_")).toString();
        String sumBy = null;
        StringBuilder metric_usage_builder = new StringBuilder();

        String labelProperties = getEnvLabelProperties(ruleData);

        switch (alarmLevel){

            case container:
                sumBy = "sum by(application, serverZone, serverEnv, pod, container, id,system) ";
                metric_usage_builder.append("container_fs_usage_bytes{container!='',container!='POD', application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder.append(",").append(labelProperties);
                }
                metric_usage_builder.append("}");
            case instance:
                sumBy = "sum by(application, serverZone, serverEnv, pod,system) ";
                metric_usage_builder.append("container_fs_usage_bytes{pod!='', application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder.append(",").append(labelProperties);
                }
                metric_usage_builder.append("}");
            default:
                ;
        }

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append(sumBy).append(" (").append(metric_usage_builder.toString()).append(")");
        exprBuilder.append("/");
        exprBuilder.append("1024 * 1024 * 1024 ");
        exprBuilder.append(op);
        exprBuilder.append(value);

        log.info("getDiskHasUsedAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getDiskUsageAlarmExpr(Integer projectId, String projectName, String op, double value, AlarmRuleData ruleData, BasicAlarmLevel alarmLevel){

        if(alarmLevel == null || !BasicAlarmLevel.isValid(alarmLevel)){
            log.error("getDiskUsageAlarmExpr no valid alarmLevel assign! alarmLevel : {}",alarmLevel);
            return null;
        }

        String projectSign = new StringBuilder().append(projectId).append("_").append(projectName.replaceAll("-","_")).toString();
        String sumBy = null;
        StringBuilder metric_usage_builder = new StringBuilder();

        String labelProperties = getEnvLabelProperties(ruleData);

        switch (alarmLevel){

            case instance:
                sumBy = "sum by(application, serverZone, serverEnv, pod,system) ";
                metric_usage_builder.append("container_fs_usage_bytes{pod!='', application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder.append(",").append(labelProperties);
                }
                metric_usage_builder.append("}");
            default:
                ;
        }

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append(sumBy).append(" (").append(metric_usage_builder.toString()).append(")");
        exprBuilder.append("/");
        exprBuilder.append("(10 * 1024 * 1024 * 1024) * 100");
        exprBuilder.append(op);
        exprBuilder.append(value);

        log.info("getDiskUsageAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getLogDirDiskUsageAlarmExpr(Integer projectId, String projectName, String op, double value, AlarmRuleData ruleData){


        String projectSign = new StringBuilder().append(projectId).append("_").append(projectName.replaceAll("-","_")).toString();
        String sumBy = null;
        StringBuilder metric_usage_builder = new StringBuilder();

        String labelProperties = getEnvLabelProperties(ruleData);

        sumBy = "sum by(application, serverZone, serverEnv, pod,device,system) ";
        metric_usage_builder.append("(kube_pod_log_dir_file_usage{pod!='',application=").append(projectSign).append("'");
        if (StringUtils.isNotBlank(labelProperties)) {
            metric_usage_builder.append(",").append(labelProperties);
        }
        metric_usage_builder.append("})");


        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append(sumBy).append(" (").append(metric_usage_builder.toString()).append(")");
        exprBuilder.append("/");
        exprBuilder.append("(100 * 1024 * 1024) * 100");
        exprBuilder.append(op);
        exprBuilder.append(value);

        log.info("getLogDirDiskUsageAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getDiskWriteAlarmExpr(Integer projectId, String projectName, String op, double value, AlarmRuleData ruleData, BasicAlarmLevel alarmLevel){

        if(alarmLevel == null || !BasicAlarmLevel.isValid(alarmLevel)){
            log.error("getDiskWriteAlarmExpr no valid alarmLevel assign! alarmLevel : {}",alarmLevel);
            return null;
        }

        String projectSign = new StringBuilder().append(projectId).append("_").append(projectName.replaceAll("-","_")).toString();
        String sumBy = null;
        StringBuilder metric_usage_builder = new StringBuilder();

        String labelProperties = getEnvLabelProperties(ruleData);

        switch (alarmLevel){

            case container:
                sumBy = "sum by(application, serverZone, serverEnv, pod, container, id,system) ";
                metric_usage_builder.append("(irate(container_fs_write_seconds_total{container!=\"\",container!=\"POD\",application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder.append(",").append(labelProperties);
                }
                metric_usage_builder.append("}[5m]))");
            case instance:
                sumBy = "sum by(application, serverZone, serverEnv, pod,system) ";
                metric_usage_builder.append("(irate(container_fs_write_seconds_total{pod!=\"\", application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder.append(",").append(labelProperties);
                }
                metric_usage_builder.append("}[5m]))");
            default:
                ;
        }

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append(sumBy).append(metric_usage_builder.toString());
        exprBuilder.append("/");
        exprBuilder.append("(1024 * 1024 * 1024) ");
        exprBuilder.append(op);
        exprBuilder.append(value);

        log.info("getDiskWriteAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getDiskReadAlarmExpr(Integer projectId, String projectName, String op, double value, AlarmRuleData ruleData, BasicAlarmLevel alarmLevel){

        if(alarmLevel == null || !BasicAlarmLevel.isValid(alarmLevel)){
            log.error("getDiskReadAlarmExpr no valid alarmLevel assign! alarmLevel : {}",alarmLevel);
            return null;
        }

        String projectSign = new StringBuilder().append(projectId).append("_").append(projectName.replaceAll("-","_")).toString();
        String sumBy = null;
        StringBuilder metric_usage_builder = new StringBuilder();

        String labelProperties = getEnvLabelProperties(ruleData);

        switch (alarmLevel){

            case container:
                sumBy = "sum by(application, serverZone, serverEnv, pod, container, id,system) ";
                metric_usage_builder.append("(irate(container_fs_read_seconds_total{container!=\"\",container!=\"POD\",application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder.append(",").append(labelProperties);
                }
                metric_usage_builder.append("}[5m]))");
            case instance:
                sumBy = "sum by(application, serverZone, serverEnv, pod,system) ";
                metric_usage_builder.append("(irate(container_fs_read_seconds_total{pod!=\"\", application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder.append(",").append(labelProperties);
                }
                metric_usage_builder.append("}[5m]))");
            default:
                ;
        }

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append(sumBy).append(metric_usage_builder.toString());
        exprBuilder.append("/");
        exprBuilder.append("(1024 * 1024 * 1024) ");
        exprBuilder.append(op);
        exprBuilder.append(value);

        log.info("getDiskReadAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getNetworkTransmitAlarmExpr(Integer projectId, String projectName, String op, double value, AlarmRuleData ruleData, BasicAlarmLevel alarmLevel){

        if(alarmLevel == null || !BasicAlarmLevel.isValid(alarmLevel)){
            log.error("getNetworkTransmitAlarmExpr no valid alarmLevel assign! alarmLevel : {}",alarmLevel);
            return null;
        }

        String projectSign = new StringBuilder().append(projectId).append("_").append(projectName.replaceAll("-","_")).toString();
        String sumBy = null;
        StringBuilder metric_usage_builder = new StringBuilder();

        String labelProperties = getEnvLabelProperties(ruleData);

        switch (alarmLevel){

            case cluster:
                sumBy = "sum by(application, serverZone, serverEnv, deploy_space, cluster,system) ";
                metric_usage_builder.append("(irate(container_network_transmit_bytes_total{application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder.append(",").append(labelProperties);
                }
                metric_usage_builder.append("}[5m]))");
            case instance:
                sumBy = "sum by(application, serverZone, serverEnv, pod,system) ";
                metric_usage_builder.append("(irate(container_network_transmit_bytes_total{pod!=\"\", application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder.append(",").append(labelProperties);
                }
                metric_usage_builder.append("}[5m]))");
            default:
                ;
        }

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append(sumBy).append(metric_usage_builder.toString());
        exprBuilder.append("/");
        exprBuilder.append("(1024 * 1024 * 1024) ");
        exprBuilder.append(op);
        exprBuilder.append(value);

        log.info("getNetworkTransmitAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getNetworkReceiveAlarmExpr(Integer projectId, String projectName, String op, double value, AlarmRuleData ruleData, BasicAlarmLevel alarmLevel){

        if(alarmLevel == null || !BasicAlarmLevel.isValid(alarmLevel)){
            log.error("getNetworkReceiveAlarmExpr no valid alarmLevel assign! alarmLevel : {}",alarmLevel);
            return null;
        }

        String projectSign = new StringBuilder().append(projectId).append("_").append(projectName.replaceAll("-","_")).toString();
        String sumBy = null;
        StringBuilder metric_usage_builder = new StringBuilder();

        String labelProperties = getEnvLabelProperties(ruleData);

        switch (alarmLevel){

            case cluster:
                sumBy = "sum by(application, serverZone, serverEnv, deploy_space, cluster,system) ";
                metric_usage_builder.append("(irate(container_network_receive_bytes_total{application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder.append(",").append(labelProperties);
                }
                metric_usage_builder.append("}[5m]))");
            case instance:
                sumBy = "sum by(application, serverZone, serverEnv, pod,system) ";
                metric_usage_builder.append("(irate(container_network_receive_bytes_total{pod!=\"\", application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder.append(",").append(labelProperties);
                }
                metric_usage_builder.append("}[5m]))");
            default:
                ;
        }

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append(sumBy).append(metric_usage_builder.toString());
        exprBuilder.append("/");
        exprBuilder.append("(1024 * 1024 * 1024) ");
        exprBuilder.append(op);
        exprBuilder.append(value);

        log.info("getNetworkReceiveAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getContainerRestartAlarmExpr(Integer projectId, String projectName, String op, double value, AlarmRuleData ruleData, BasicAlarmLevel alarmLevel){

        if(alarmLevel == null || !BasicAlarmLevel.isValid(alarmLevel)){
            log.error("getContainerRestartAlarmExpr no valid alarmLevel assign! alarmLevel : {}",alarmLevel);
            return null;
        }

        String projectSign = new StringBuilder().append(projectId).append("_").append(projectName.replaceAll("-","_")).toString();
        String sumBy1 = "";
        String sumBy2 = "";
        StringBuilder metric_usage_builder1 = new StringBuilder();
        StringBuilder metric_usage_builder2 = new StringBuilder();

        String labelProperties = getEnvLabelProperties(ruleData);

        switch (alarmLevel){

            case container:
                sumBy1 = "sum by(application, serverZone, serverEnv, pod, container, id,system) ";
                sumBy2 = "sum by(application, serverZone, serverEnv, pod, container, id,system, ip) ";
                metric_usage_builder1.append("(round(increase(kube_pod_container_status_restarts_total{instance!=\"\",container!=\"POD\",pod!=\"\",application='").append(projectSign).append("'");
                metric_usage_builder2.append("(round(increase(kube_pod_container_restarts_record{instance!=\"\",container!=\"POD\",pod!=\"\",application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder1.append(",").append(labelProperties);
                    metric_usage_builder2.append(",").append(labelProperties);
                }
                metric_usage_builder1.append("}[5m])))");
                metric_usage_builder2.append("}[5m])))");
            case instance:
                sumBy1 = "sum by(application, serverZone, serverEnv, pod,container,system) ";
                metric_usage_builder1.append("(round(increase(kube_pod_container_status_restarts_total{container=\"main\",instance!=\"\",pod!=\"\",application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder1.append(",").append(labelProperties);
                }
                metric_usage_builder1.append("}[5m])))");
            default:
                ;
        }


        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("(");
        exprBuilder.append(sumBy1).append(metric_usage_builder1.toString());
        if(StringUtils.isNotBlank(sumBy2) && StringUtils.isNotBlank(metric_usage_builder2.toString())){
            exprBuilder.append(" or ");
            exprBuilder.append(sumBy2).append(metric_usage_builder2.toString());
        }
        exprBuilder.append(")");

        exprBuilder.append(op);
        exprBuilder.append(value);

        log.info("getContainerRestartAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getProcessNumAlarmExpr(Integer projectId, String projectName, String op, double value, AlarmRuleData ruleData, BasicAlarmLevel alarmLevel){

        if(alarmLevel == null || !BasicAlarmLevel.isValid(alarmLevel)){
            log.error("getProcessNumAlarmExpr no valid alarmLevel assign! alarmLevel : {}",alarmLevel);
            return null;
        }

        String projectSign = new StringBuilder().append(projectId).append("_").append(projectName.replaceAll("-","_")).toString();
        String sumBy1 = "";
        StringBuilder metric_usage_builder1 = new StringBuilder();

        String labelProperties = getEnvLabelProperties(ruleData);

        switch (alarmLevel){

            case container:
                sumBy1 = "sum by(application, serverZone, serverEnv, pod, container, id,system) ";
                metric_usage_builder1.append("(container_processes{container!=\"\",container!=\"POD\",pod!=\"\",application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder1.append(",").append(labelProperties);
                }
                metric_usage_builder1.append("})");
            case instance:
                sumBy1 = "sum by(application, serverZone, serverEnv, pod,container,system) ";
                metric_usage_builder1.append("(container_processes{container=\"main\",pod!=\"\",application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder1.append(",").append(labelProperties);
                }
                metric_usage_builder1.append("})");
            default:
                ;
        }

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append(sumBy1).append(metric_usage_builder1.toString());

        exprBuilder.append(op);
        exprBuilder.append(value);

        log.info("getProcessNumAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getThreadsNumAlarmExpr(Integer projectId, String projectName, String op, double value, AlarmRuleData ruleData, BasicAlarmLevel alarmLevel){

        if(alarmLevel == null || !BasicAlarmLevel.isValid(alarmLevel)){
            log.error("getThreadsNumAlarmExpr no valid alarmLevel assign! alarmLevel : {}",alarmLevel);
            return null;
        }

        String projectSign = new StringBuilder().append(projectId).append("_").append(projectName.replaceAll("-","_")).toString();
        String sumBy1 = "";
        StringBuilder metric_usage_builder1 = new StringBuilder();

        String labelProperties = getEnvLabelProperties(ruleData);

        switch (alarmLevel){

            case container:
                sumBy1 = "sum by(application, serverZone, serverEnv, pod,system) ";
                metric_usage_builder1.append("(container_threads{container!=\"\",container!=\"POD\",pod!=\"\",application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder1.append(",").append(labelProperties);
                }
                metric_usage_builder1.append("})");
            case instance:
                sumBy1 = "sum by(application, serverZone, serverEnv, pod,container,system) ";
                metric_usage_builder1.append("(container_threads{container=\"main\",pod!=\"\",application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder1.append(",").append(labelProperties);
                }
                metric_usage_builder1.append("})");
            default:
                ;
        }

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append(sumBy1).append(metric_usage_builder1.toString());

        exprBuilder.append(op);
        exprBuilder.append(value);

        log.info("getThreadsNumAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getSocketNumAlarmExpr(Integer projectId, String projectName, String op, double value, AlarmRuleData ruleData, BasicAlarmLevel alarmLevel){

        if(alarmLevel == null || !BasicAlarmLevel.isValid(alarmLevel)){
            log.error("getSocketNumAlarmExpr no valid alarmLevel assign! alarmLevel : {}",alarmLevel);
            return null;
        }

        String projectSign = new StringBuilder().append(projectId).append("_").append(projectName.replaceAll("-","_")).toString();
        String sumBy1 = "";
        StringBuilder metric_usage_builder1 = new StringBuilder();

        String labelProperties = getEnvLabelProperties(ruleData);

        switch (alarmLevel){

            case container:
                sumBy1 = "sum by(application, serverZone, serverEnv, pod,system) ";
                metric_usage_builder1.append("(container_sockets{container!=\"\",container!=\"POD\",pod!=\"\",application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder1.append(",").append(labelProperties);
                }
                metric_usage_builder1.append("})");
            case instance:
                sumBy1 = "sum by(application, serverZone, serverEnv, pod,container,system) ";
                metric_usage_builder1.append("(container_sockets{container=\"main\",pod!=\"\",application='").append(projectSign).append("'");
                if (StringUtils.isNotBlank(labelProperties)) {
                    metric_usage_builder1.append(",").append(labelProperties);
                }
                metric_usage_builder1.append("})");
            default:
                ;
        }

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append(sumBy1).append(metric_usage_builder1.toString());

        exprBuilder.append(op);
        exprBuilder.append(value);

        log.info("getSocketNumAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }


    public String getKeyCenterRequestAlarmExpr(Integer projectId, String projectName, String op, double value, AlarmRuleData ruleData, KeyCenterRequestType requestType){

        String projectSign = new StringBuilder().append(projectId).append("_").append(projectName.replaceAll("-","_")).toString();
        String labelProperties = getEnvLabelProperties(ruleData);
        StringBuilder exprBuilder = new StringBuilder();

        switch (requestType){

            case thrift:
                exprBuilder.append("sum by(application, serverZone, serverEnv, pod, container, id, system) ")
                        .append("(increase(keycenter_thrift_request_count_total{code!=\"200\",pod!=\"\",application='").append(projectSign).append("'");
            case http:
                exprBuilder.append("sum by(application, serverZone, serverEnv, pod, container, id, system) ")
                        .append("(increase(keycenter_http_request_count_total{code!=\"200\",pod!=\"\",application='").append(projectSign).append("'");
            default:
                ;
        }

        if (StringUtils.isNotBlank(labelProperties)) {
            exprBuilder.append(",").append(labelProperties);
        }
        exprBuilder.append("}[1m])) ");
        exprBuilder.append(op);
        exprBuilder.append(value);

        log.info("getKeyCenterRequestAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getTpcNumAlarmExpr(Integer projectId, String projectName, String op, double value, AlarmRuleData ruleData){

        String projectSign = new StringBuilder().append(projectId).append("_").append(projectName.replaceAll("-","_")).toString();
        String labelProperties = getEnvLabelProperties(ruleData);
        StringBuilder exprBuilder = new StringBuilder();

        exprBuilder.append("sum by(application, serverZone, serverEnv, pod,system) ")
                .append("(increase(container_network_advance_tcp_stats_total{container=\"POD\",pod!=\"\", tcp_state=\"listendrops\", application='").append(projectSign).append("'");

        if (StringUtils.isNotBlank(labelProperties)) {
            exprBuilder.append(",").append(labelProperties);
        }
        exprBuilder.append("}[5m])) ");
        exprBuilder.append(op);
        exprBuilder.append(value);

        log.info("getTpcNumAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getFileDescriptorsAlarmExpr(Integer projectId, String projectName, String op, double value, AlarmRuleData ruleData){

        String projectSign = new StringBuilder().append(projectId).append("_").append(projectName.replaceAll("-","_")).toString();
        String labelProperties = getEnvLabelProperties(ruleData);
        StringBuilder exprBuilder = new StringBuilder();

        exprBuilder.append("sum by(application, serverZone, serverEnv, pod,system) ")
                .append("(container_file_descriptors{container!=\"\",pod!=\"\", application='").append(projectSign).append("'");

        if (StringUtils.isNotBlank(labelProperties)) {
            exprBuilder.append(",").append(labelProperties);
        }
        exprBuilder.append("}) ");
        exprBuilder.append(op);
        exprBuilder.append(value);

        log.info("getFileDescriptorsAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getDeployUnitExceptionInstanceAlarmExpr(Integer projectId, String projectName, String op, double value, AlarmRuleData ruleData){

        String projectSign = new StringBuilder().append(projectId).append("_").append(projectName.replaceAll("-","_")).toString();
        String labelProperties = getEnvLabelProperties(ruleData);
        StringBuilder exprBuilder = new StringBuilder();

        exprBuilder.append("sum by(application, serverZone, serverEnv, deploy_space, cluster,system) ")
                .append("(kruise_cloneset_spec_replicas{application='").append(projectSign).append("'");

        if (StringUtils.isNotBlank(labelProperties)) {
            exprBuilder.append(",").append(labelProperties);
        }
        exprBuilder.append("}) ");
        exprBuilder.append(" - ");
        exprBuilder.append("sum by(application, serverZone, serverEnv, deploy_space, cluster,system) ")
                .append("(kruise_cloneset_status_replicas_ready{application='").append(projectSign).append("'");

        if (StringUtils.isNotBlank(labelProperties)) {
            exprBuilder.append(",").append(labelProperties);
        }
        exprBuilder.append("}) ");


        exprBuilder.append(op);
        exprBuilder.append(value);

        log.info("getDeployUnitExceptionInstanceAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getDeployUnitInstanceUsageAlarmExpr(Integer projectId, String projectName, String op, double value, AlarmRuleData ruleData){

        String projectSign = new StringBuilder().append(projectId).append("_").append(projectName.replaceAll("-","_")).toString();
        String labelProperties = getEnvLabelProperties(ruleData);
        StringBuilder exprBuilder = new StringBuilder();

        exprBuilder.append("sum by(application, serverZone, serverEnv, deploy_space, cluster,system) ")
                .append("(kruise_cloneset_status_replicas_ready{ application='").append(projectSign).append("'");

        if (StringUtils.isNotBlank(labelProperties)) {
            exprBuilder.append(",").append(labelProperties);
        }
        exprBuilder.append("}) ");
        exprBuilder.append("/");
        exprBuilder.append("sum by(application, serverZone, serverEnv, deploy_space, cluster,system) ")
                .append("(kruise_cloneset_spec_replicas{application='").append(projectSign).append("'");

        if (StringUtils.isNotBlank(labelProperties)) {
            exprBuilder.append(",").append(labelProperties);
        }
        exprBuilder.append("}) * 100");


        exprBuilder.append(op);
        exprBuilder.append(value);

        log.info("getDeployUnitExceptionInstanceAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getContainerLoadAlarmExpr(Integer projectId,String projectName,String op,double value,boolean isK8s,AlarmRuleData ruleData){

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("avg_over_time(container_cpu_load_average_10s");
        exprBuilder.append("{system='mione',");
        exprBuilder.append("image!='',");
        if(isK8s){
            exprBuilder.append("name=~'k8s.*',");
        }else {
            exprBuilder.append("name!~'k8s.*',");
        }

        String labelProperties = getEnvLabelProperties(ruleData);
        if(StringUtils.isNotBlank(labelProperties)){
            exprBuilder.append(labelProperties).append(",");
        }

        exprBuilder.append("application='").append(projectId).append("_").append(projectName.replaceAll("-","_")).append("'");

        exprBuilder.append("}");
        exprBuilder.append("[1m]");
        exprBuilder.append(") / 1000");
        exprBuilder.append(op);
        exprBuilder.append(value);
        log.info("getContainerLoadAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    /**
     * 容器CPU使用率(最近1分钟)
     * @param projectId
     * @param projectName
     * @param op
     * @param value
     * @return
     */
    public String getContainerCpuAlarmExpr(Integer projectId,String projectName,String op,double value,boolean isK8s,AlarmRuleData ruleData){

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("rate(container_cpu_user_seconds_total{");

        exprBuilder.append("image!='',");
        exprBuilder.append("system='mione',");
        if(isK8s){
            exprBuilder.append("name=~'k8s.*',");
        }else{
            exprBuilder.append("name!~'k8s.*',");
        }

        String labelProperties = getEnvLabelProperties(ruleData);
        if(StringUtils.isNotBlank(labelProperties)){
            exprBuilder.append(labelProperties).append(",");
        }

        exprBuilder.append("application='").append(projectId).append("_").append(projectName.replaceAll("-","_")).append("'");

        exprBuilder.append("}[1m]) * 100");
        exprBuilder.append(op).append(value);
        log.info("getContainerCpuAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getContainerCpuResourceAlarmExpr(Integer projectId,String projectName,String op,double value,boolean isK8s,AlarmRuleData ruleData){

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("sum(irate(container_cpu_usage_seconds_total{");
        exprBuilder.append("image!='',");
        exprBuilder.append("system='mione',");
        if(isK8s){
            exprBuilder.append("name=~'k8s.*',");
        }else{
            exprBuilder.append("name!~'k8s.*',");
        }

        //mimonitor视为全局配置
        if(!projectName.equals("mimonitor")){
            exprBuilder.append("application='").append(projectId).append("_").append(projectName.replaceAll("-","_")).append("',");
        }

        String labelProperties = getEnvLabelProperties(ruleData);
        if(StringUtils.isNotBlank(labelProperties)){
            exprBuilder.append(labelProperties);
        }

        exprBuilder.append("}[1d])) without (cpu) * 100");
        exprBuilder.append(op).append(value);
        log.info("getContainerCpuResourceAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getContainerMemAlarmExpr(Integer projectId,String projectName,String op,double value,boolean isK8s,AlarmRuleData ruleData){

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("(sum(avg_over_time(container_memory_rss{");

        exprBuilder.append("image!='',");
        exprBuilder.append("system='mione',");
        if(isK8s){
            exprBuilder.append("name=~'k8s.*',");
        }else {
            exprBuilder.append("name!~'k8s.*',");
        }

        String labelProperties = getEnvLabelProperties(ruleData);
        if(StringUtils.isNotBlank(labelProperties)){
            exprBuilder.append(labelProperties).append(",");
        }

        exprBuilder.append("application='").append(projectId).append("_").append(projectName.replaceAll("-","_")).append("'");

        exprBuilder.append("}[1m])) by (application,ip,job,name,system,instance,id,pod,namespace,serverEnv) / ");
        exprBuilder.append("sum(avg_over_time(container_spec_memory_limit_bytes{");
        exprBuilder.append("image!='',");

        if(StringUtils.isNotBlank(labelProperties)){
            exprBuilder.append(labelProperties).append(",");
        }

        exprBuilder.append("application='").append(projectId).append("_").append(projectName.replaceAll("-","_")).append("'");
        exprBuilder.append("}[1m])) by (application,ip,job,name,system,instance,id,pod,namespace,serverEnv)) * 100");
        exprBuilder.append(op).append(value);
        log.info("getContainerMemAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getContainerMemReourceAlarmExpr(Integer projectId,String projectName,String op,double value,boolean isK8s,AlarmRuleData ruleData){

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("(sum(avg_over_time(container_memory_rss{");
        exprBuilder.append("image!='',");
        exprBuilder.append("system='mione',");

        String labelProperties = getEnvLabelProperties(ruleData);
        if(StringUtils.isNotBlank(labelProperties)){
            exprBuilder.append(labelProperties).append(",");
        }

        if(isK8s){
            exprBuilder.append("name =~'k8s.*',");
        }else{
            exprBuilder.append("container_label_PROJECT_ID!='',name !~'k8s.*',");
        }

        //mimonitor视为全局配置
        if(!projectName.equals("mimonitor")){
            exprBuilder.append("application='").append(projectId).append("_").append(projectName.replaceAll("-","_")).append("',");
        }


        exprBuilder.append("}[1d])) by (application,ip,job,name,system,instance,id,serverEnv) / ");
        exprBuilder.append("sum(avg_over_time(container_spec_memory_limit_bytes{");
        exprBuilder.append("image!='',");
        exprBuilder.append("system='mione',");

        if(isK8s){
            exprBuilder.append("name =~'k8s.*',");
        }else{
            exprBuilder.append("container_label_PROJECT_ID!='',name !~'k8s.*',");
        }

        //mimonitor视为全局配置
        if(!projectName.equals("mimonitor")){
            exprBuilder.append("application='").append(projectId).append("_").append(projectName.replaceAll("-","_")).append("',");
        }

        if(StringUtils.isNotBlank(labelProperties)){
            exprBuilder.append(labelProperties);
        }

        exprBuilder.append("}[1d])) by (container_label_PROJECT_ID,application,ip,job,name,system,instance,id,serverEnv,serverZone)) * 100");
        exprBuilder.append(op).append(value);
        log.info("getContainerMemReourceAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getContainerDiskReourceAlarmExpr(Integer projectId,String projectName,String op,double value,boolean isK8s,AlarmRuleData ruleData){

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("clamp_max(sum(container_fs_usage_bytes{");
        exprBuilder.append("system='mione',");

        String labelProperties = getEnvLabelProperties(ruleData);
        if(StringUtils.isNotBlank(labelProperties)){
            exprBuilder.append(labelProperties).append(",");
        }
        exprBuilder.append("application='").append(projectId).append("_").append(projectName.replaceAll("-","_")).append("'");
        exprBuilder.append("}) by (application,name,ip,serverEnv)/10737418240 ,1) * 100  ");
        exprBuilder.append(op).append(value);
        log.info("getContainerDiskReourceAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getK8sCpuAvgUsageAlarmExpr(Integer projectId,String projectName,String op,double value,AlarmRuleData ruleData){

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("sum(irate(container_cpu_usage_seconds_total{");
        exprBuilder.append("image!='',system='mione',");

        String labelProperties = getEnvLabelProperties(ruleData);
        if(StringUtils.isNotBlank(labelProperties)){
            exprBuilder.append(labelProperties).append(",");
        }

        //k8s标识
        exprBuilder.append("name=~'").append("k8s.*").append("',");
        exprBuilder.append("application='").append(projectId).append("_").append(projectName).append("'");

        exprBuilder.append("}[1m])) without (cpu) * 100 ");
        exprBuilder.append("/");
        exprBuilder.append("(");
        exprBuilder.append("container_spec_cpu_quota{");
        exprBuilder.append("system='mione',");

        if(StringUtils.isNotBlank(labelProperties)){
            exprBuilder.append(labelProperties).append(",");
        }

        exprBuilder.append("application='").append(projectId).append("_").append(projectName).append("'");
        exprBuilder.append("}");
        exprBuilder.append("/");

        exprBuilder.append("container_spec_cpu_period{");
        exprBuilder.append("system='mione',");
        //k8s标识
        exprBuilder.append("name=~'").append("k8s.*").append("',");
        exprBuilder.append("application='").append(projectId).append("_").append(projectName).append("'");
        exprBuilder.append("}");
        exprBuilder.append(")");

        exprBuilder.append(op).append(value);
        log.info("getK8sCpuAvgUsageAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getK8sPodRestartExpr(Integer projectId,String projectName,AlarmRuleData ruleData){

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("increase(kube_pod_container_restarts_record{system='mione',");
        String labelProperties = getEnvLabelProperties(ruleData);
        if(StringUtils.isNotBlank(labelProperties)){
            exprBuilder.append(labelProperties).append(",");
        }
        String appName = projectName.replaceAll("-","_");
        exprBuilder.append("application='").append(projectId).append("_").append(appName).append("'");
        exprBuilder.append("}[3m]) > 0");

        return exprBuilder.toString();
    }


    public String getContainerCountAlarmExpr(Integer projectId,String projectName,String op,double value,boolean isK8s,AlarmRuleData ruleData){

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("count(sum_over_time(container_spec_memory_limit_bytes{");
        exprBuilder.append("image!='',");
        exprBuilder.append("system='mione',");
        if(isK8s){
            exprBuilder.append("name=~'k8s.*',");
        }else {
            exprBuilder.append("name!~'k8s.*',");
        }

        String labelProperties = getEnvLabelProperties(ruleData);
        if(StringUtils.isNotBlank(labelProperties)){
            exprBuilder.append(labelProperties).append(",");
        }

        exprBuilder.append("application='").append(projectId).append("_").append(projectName).append("'");

        exprBuilder.append("}[2m])) by (system,job)");
        exprBuilder.append(op).append(value);
        log.info("getContainerCountAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getAppRestartAlarmExpr(Integer projectId,String projectName,String op,double value,boolean isK8s){

        StringBuilder exprBuilder = new StringBuilder();
        log.info("getContainerCountAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getAppCrashAlarmExpr(Integer projectId,String projectName,AlarmRuleData ruleData){

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("time() - container_last_seen{");
        exprBuilder.append("system='mione',");

        String labelProperties = getEnvLabelProperties(ruleData);
        if(StringUtils.isNotBlank(labelProperties)){
            exprBuilder.append(labelProperties).append(",");
        }

        exprBuilder.append("application='").append(projectId).append("_").append(projectName).append("'");
        exprBuilder.append("}").append(" > 360");

        log.info("getAppCrashAlarmExpr param: projectId:{}, projectName:{},  return:{}",projectId, projectName, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public List<String> getInstanceIpList(Integer projectId, String projectName){

        List<Metric> metrics = listInstanceMetric(projectId, projectName);
        if(CollectionUtils.isEmpty(metrics)){
            log.error("getInstanceIps no data found! projectId :{},projectName:{}",projectId,projectName);
            return null;
        }

        List<String> result = new ArrayList<>();
        for(Metric metric : metrics){
            result.add(metric.getIp());
        }

        return result;
    }

    public Map getEnvIpMapping(Integer projectId, String projectName){

        List<Metric> metrics = listInstanceMetric(projectId, projectName);
        if(CollectionUtils.isEmpty(metrics)){
            log.error("getEnvIpMapping no data found! projectId :{},projectName:{}",projectId,projectName);
            return null;
        }


        Map result = new HashMap();
        Map<String,Map<String,Object>> mapResult = new HashMap<>();
        Map<String, HashSet<String>> allZones = new HashMap<>();
        Set allIps = new HashSet();
        for(Metric metric : metrics){

            allIps.add(metric.getServerIp());

            if(StringUtils.isBlank(metric.getServerEnv())){
                continue;
            }
            mapResult.putIfAbsent(metric.getServerEnv(),new HashMap<>());
            Map<String, Object> stringObjectMap = mapResult.get(metric.getServerEnv());

            stringObjectMap.putIfAbsent("envIps", new HashSet<>());
            HashSet ipList = (HashSet<String>)stringObjectMap.get("envIps");
            ipList.add(metric.getServerIp());

            if(StringUtils.isNotBlank(metric.getServerZone())){
                allZones.putIfAbsent(metric.getServerZone(),new HashSet<String>());
                HashSet<String> zoneIps = allZones.get(metric.getServerZone());
                zoneIps.add(metric.getServerIp());

                stringObjectMap.putIfAbsent("zoneList", new HashMap<>());
                HashMap serviceList = (HashMap<String,Set<String>>)stringObjectMap.get("zoneList");

                serviceList.putIfAbsent(metric.getServerZone(), new HashSet<String>());
                HashSet<String> ips = (HashSet<String>)serviceList.get(metric.getServerZone());

                ips.add(metric.getServerIp());
            }
        }

        result.put("allIps",allIps);
        result.put("envIpMapping",mapResult);
        result.put("allZones",allZones);

        return result;
    }


    private List<Metric> listInstanceMetric(Integer projectId,String projectName){
        projectName = projectName.replaceAll("-","_");

        StringBuilder builder = new StringBuilder();
        builder.append("process_uptime_seconds{application=\"")
                .append(projectId).append("_").append(projectName)
                .append("\"").append("}");
        Result<PageData> pageDataResult = prometheusService.queryByMetric(builder.toString());
        if(pageDataResult.getCode() != ErrorCode.success.getCode() || pageDataResult.getData() == null){
            log.error("queryByMetric error! projectId :{},projectName:{}",projectId,projectName);
            return null;
        }

        List<Metric> list = (List<Metric>) pageDataResult.getData().getList();
        log.info("listInstanceMetric param projectName:{},result:{}",projectName,list.size());

        return list;
    }

    public List<String> getHttpClientServerDomain(Integer projectId, String projectName){

        List<Metric> metrics = listHttpMetric(projectId, projectName);
        if(CollectionUtils.isEmpty(metrics)){
            log.error("getHttpClientServerDomain no data found! projectId :{},projectName:{}",projectId,projectName);
            return null;
        }

        List<String> result = new ArrayList<>();
        for(Metric metric : metrics){
            result.add(metric.getServiceName());
        }

        return result;
    }

    private List<Metric> listHttpMetric(Integer projectId,String projectName){
        projectName = projectName.replaceAll("-","_");

        StringBuilder builder = new StringBuilder();
        builder.append(serverType);
        builder.append("_jaeger_aopClientTotalMethodCount_total{application=\"")
                .append(projectId).append("_").append(projectName)
                .append("\"").append(",serviceName!=''}");
        Result<PageData> pageDataResult = prometheusService.queryByMetric(builder.toString());
        if(pageDataResult.getCode() != ErrorCode.success.getCode() || pageDataResult.getData() == null){
            log.error("queryByMetric error! projectId :{},projectName:{}",projectId,projectName);
            return null;
        }

        List<Metric> list = (List<Metric>) pageDataResult.getData().getList();
        log.info("listHttpMetric param projectName:{},result:{}",projectName,list.size());

        return list;
    }

    private List<Metric> listContainerNameMetric(Integer projectId,String projectName){
        projectName = projectName.replaceAll("-","_");

        StringBuilder builder = new StringBuilder();
        builder.append("jvm_classes_loaded_classes{ containerName != '',application=\"")
                .append(projectId).append("_").append(projectName)
                .append("\"").append("}");
        Result<PageData> pageDataResult = prometheusService.queryByMetric(builder.toString());
        if(pageDataResult.getCode() != ErrorCode.success.getCode() || pageDataResult.getData() == null){
            log.error("listContainerNameMetric error! projectId :{},projectName:{}",projectId,projectName);
            return null;
        }

        List<Metric> list = (List<Metric>) pageDataResult.getData().getList();
        log.info("listContainerNameMetric param projectName:{},result:{}",projectName,list.size());

        return list;
    }

    public List<String> listContainerName(Integer projectId,String projectName){

        List<Metric> metrics = listContainerNameMetric(projectId, projectName);
        if(CollectionUtils.isEmpty(metrics)){
            return Lists.newArrayList();
        }
        return metrics.stream().map(t -> t.getContainerName()).distinct().collect(Collectors.toList());
    }


    public String getPresetMetricErrorAlarm(String sourceMetric,Integer projectId,String projectName,Map includeLabels,Map exceptLabels,String metricSuffix,String duration,String offset,String op,Float value){
        String s = prometheusService.completeMetricForAlarm(sourceMetric, includeLabels,exceptLabels, projectId,projectName, metricSuffix,  duration, null);

        StringBuilder expBuilder = new StringBuilder();
        expBuilder.append("sum(")
                .append("sum_over_time").append("(").append(s).append(")")
                .append(") by (application,system,serverIp,serviceName,methodName,sqlMethod,errorCode,serverEnv,serverZone,containerName,sql,dataSource,functionModule,functionName)")
                .append(op).append(value);


        log.info("AlarmService.getPresetMetricErrorAlarm param" +
                        ":sourceMetric:{},projectId:{},projectName:{},includeLabels:{},exceptLabels:{},metricSuffix:{},duration:{},offset:{},op:{},value:{},return : {}"
                ,sourceMetric,projectId,projectName,includeLabels,exceptLabels,metricSuffix,duration,offset,op,value,expBuilder.toString());
        return expBuilder.toString();
    }

    public String getPresetMetricSLAErrorAlarm(String sourceMetric,Integer projectId,String projectName,Map includeLabels,Map exceptLabels,String metricSuffix,String duration,String offset,String op,Float value){
        String s = prometheusService.completeMetricForAlarm(sourceMetric, includeLabels,exceptLabels, projectId,projectName, metricSuffix,  duration, null);

        StringBuilder expBuilder = new StringBuilder();
        expBuilder.append("sum(")
                .append("sum_over_time").append("(").append(s).append(")")
                .append(") by (application,system,serverIp,serviceName,methodName,sqlMethod,errorCode,serverEnv,serverZone,containerName,sql,dataSource,functionModule,functionName,clientProjectId,clientProjectName,clientEnv,clientEnvId,clientIp)")
                .append(op).append(value);


        log.info("AlarmService.getPresetMetricSLAErrorAlarm param" +
                        ":sourceMetric:{},projectId:{},projectName:{},includeLabels:{},exceptLabels:{},metricSuffix:{},duration:{},offset:{},op:{},value:{},return : {}"
                ,sourceMetric,projectId,projectName,includeLabels,exceptLabels,metricSuffix,duration,offset,op,value,expBuilder.toString());
        return expBuilder.toString();
    }

    private String getPresetMetricQpsAlarm(String sourceMetric,Integer projectId,String projectName,Map includeLabels,Map exceptLabels,String metricSuffix,String duration, String offset,String op,Float value){
        String s = prometheusService.completeMetricForAlarm(sourceMetric, includeLabels,exceptLabels, projectId,projectName, metricSuffix,  duration, null);
        StringBuilder expBuilder = new StringBuilder();
        expBuilder.append("sum(sum_over_time(").append(s).append(")/").append(evaluationDuration).append(") by (")
                .append("application,system,serverIp,serviceName,methodName,sqlMethod,errorCode,serverEnv,serverZone,containerName,functionModule,functionName").append(")").append(op).append(value);
        log.info("AlarmService.getPresetMetricQpsAlarm param" +
                        ":sourceMetric:{},projectId:{},projectName:{},includeLabels:{},exceptLabels:{},metricSuffix:{},duration:{},offset:{},op:{},value:{},return : {}"
                ,sourceMetric,projectId,projectName,includeLabels,exceptLabels,metricSuffix,duration,offset,op,value,expBuilder.toString());
        return expBuilder.toString();
    }

    private String getPresetMetricCostAlarm(String sourceMetric,Integer projectId,String projectName,Map includeLabels,Map exceptLabels,String duration, String offset, String op,Float value){
        String sumSource = prometheusService.completeMetricForAlarm(sourceMetric, includeLabels,exceptLabels, projectId,projectName, metric_sum_suffix,  duration, null);
        String countSource = prometheusService.completeMetricForAlarm(sourceMetric, includeLabels,exceptLabels, projectId,projectName, metric_count_suffix,  duration, null);
        StringBuilder expBuilder = new StringBuilder();
        expBuilder.append("sum(sum_over_time(").append(sumSource).append(")) by (")
                .append("application,system,serverIp,serviceName,methodName,sqlMethod,errorCode,serverEnv,serverZone,containerName,functionModule,functionName").append(")")
                .append(" / ")
                .append("sum(sum_over_time(").append(countSource).append(")) by (")
                .append("application,system,serverIp,serviceName,methodName,sqlMethod,errorCode,serverEnv,serverZone,containerName,functionModule,functionName").append(") ")
                .append(op).append(value);
        log.info("AlarmService.getPresetMetricQpsAlarm expr={}", expBuilder.toString());
        return expBuilder.toString();
    }

    private String getJvmMemAlarmExpr(Integer projectId,String projectName,String type, String op,Float value,AlarmRuleData ruleData){
        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("(sum(jvm_memory_used_bytes{");

        String labelProperties = getEnvLabelProperties(ruleData);
        if(StringUtils.isNotBlank(labelProperties)){
            exprBuilder.append(labelProperties).append(",");
        }
        exprBuilder.append("application=").append("'").append(projectId).append("_").append(projectName.replaceAll("-","_")).append("'").append(",");
        exprBuilder.append("area=").append("'").append(type).append("'");
        exprBuilder.append("}) by (application,area,instance,serverEnv,serverZone,containerName,serverIp,service,system)/ ");
        exprBuilder.append("sum(jvm_memory_max_bytes{");

        if(StringUtils.isNotBlank(labelProperties)){
            exprBuilder.append(labelProperties).append(",");
        }
        exprBuilder.append("application=").append("'").append(projectId).append("_").append(projectName.replaceAll("-","_")).append("'").append(",");
        exprBuilder.append("area=").append("'").append(type).append("'");
        exprBuilder.append("}) by (application,area,instance,serverEnv,serverZone,containerName,serverIp,service,system)) * 100");
        exprBuilder.append(op).append(value);
        log.info("getJvmMemAlarmExpr param: projectId:{}, projectName:{}, type:{}, return:{}",projectId, projectName,type, exprBuilder.toString());
        return exprBuilder.toString();
    }

    private String getJvmThreadAlarmExpr(Integer projectId,String projectName, String op,Float value,AlarmRuleData ruleData){
        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("max_over_time(jvm_threads_live_threads");
        exprBuilder.append("{");

        String labelProperties = getEnvLabelProperties(ruleData);
        if(StringUtils.isNotBlank(labelProperties)){
            exprBuilder.append(labelProperties).append(",");
        }
        exprBuilder.append("application=").append("'").append(projectId).append("_").append(projectName.replaceAll("-","_")).append("'").append(",");
        exprBuilder.append("serverIp!=").append("''");
        exprBuilder.append("}[1m])");
        exprBuilder.append(op).append(value);
        log.info("getJvmThreadAlarmExpr param: projectId:{}, projectName:{}, return:{}",projectId, projectName, exprBuilder.toString());
        return exprBuilder.toString();
    }

    private String getJvmGcCostExpr(Integer projectId,String projectName, String op,Float value,boolean isFullGc,AlarmRuleData ruleData){
        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("max_over_time(jvm_gc_pause_seconds_max");
        exprBuilder.append("{");

        String labelProperties = getEnvLabelProperties(ruleData);
        if(StringUtils.isNotBlank(labelProperties)){
            exprBuilder.append(labelProperties).append(",");
        }

        if(isFullGc){
            exprBuilder.append("action='end of major GC',");
        }
        exprBuilder.append("application=").append("'").append(projectId).append("_").append(projectName.replaceAll("-","_")).append("'").append(",");
        exprBuilder.append("serverIp!=").append("''");
        exprBuilder.append("}[1m])");
        exprBuilder.append(op).append(value);
        log.info("getJvmThreadAlarmExpr param: projectId:{}, projectName:{}, return:{}",projectId, projectName, exprBuilder.toString());
        return exprBuilder.toString();
    }

    private String getJvmGcCountExpr(Integer projectId,String projectName, String op,Float value,boolean isFullGc,AlarmRuleData ruleData){
        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("delta(jvm_gc_pause_seconds_count{");

        String labelProperties = getEnvLabelProperties(ruleData);
        if(StringUtils.isNotBlank(labelProperties)){
            exprBuilder.append(labelProperties).append(",");
        }

        if(isFullGc){
            exprBuilder.append("action='end of major GC',");
        }
        exprBuilder.append("application=").append("'").append(projectId).append("_").append(projectName.replaceAll("-","_")).append("'");
        exprBuilder.append("}[1m])").append(op).append(value);
        log.info("getJvmGcCountExpr param: projectId:{}, projectName:{}, return:{}",projectId, projectName,exprBuilder.toString());
        return exprBuilder.toString();
    }

    private String getEnvLabelProperties(AlarmRuleData ruleData){
        return getLabelProperties(getEnvLabels(ruleData, true), getEnvLabels(ruleData, false));
    }

    private String getLabelProperties(Map includeLabels,Map exceptLabels){
        StringBuilder labels = new StringBuilder();
        //包含标签拼接
        if (!CollectionUtils.isEmpty(includeLabels)) {

            Set<Map.Entry<String, String>> set = includeLabels.entrySet();
            for (Map.Entry<String, String> entry : set) {
                if (org.apache.commons.lang3.StringUtils.isBlank(entry.getValue())) {
                    continue;
                }
                labels.append(entry.getKey());
                labels.append("=~");
                labels.append("'");
                labels.append(entry.getValue());
                labels.append("'");
                labels.append(",");
            }
        }

        if (!CollectionUtils.isEmpty(exceptLabels)) {

            Set<Map.Entry<String, String>> set = exceptLabels.entrySet();
            for (Map.Entry<String, String> entry : set) {
                if (org.apache.commons.lang3.StringUtils.isBlank(entry.getValue())) {
                    continue;
                }
                labels.append(entry.getKey());
                labels.append("!~");
                labels.append("'");
                labels.append(entry.getValue());
                labels.append("'");
                labels.append(",");
            }
        }

        String labelsV = labels.toString();
        if (labelsV.endsWith(",")) {
            labelsV = labelsV.substring(0, labelsV.length() - 1);
        }

        return labelsV;
    }


    public Result addRule(AppMonitor app, AppAlarmRule rule, String user, AlarmRuleData ruleData){



        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("alert", rule.getAlert());


        jsonObject.addProperty("cname", rule.getCname());


        jsonObject.addProperty("for", rule.getForTime());
        jsonObject.addProperty("forTime", rule.getForTime());


        StringBuilder title = new StringBuilder().append(app.getProjectName());
        AlarmPresetMetricsPOJO metrics = alarmPresetMetricsService.getByCode(rule.getAlert());
        if (metrics != null) {
            title.append("&").append(metrics.getMessage());
        } else {
            //check tesla metrics
            teslaService.checkTeslaMetrics(title, rule.getAlert());
        }
        JsonObject jsonSummary = new JsonObject();
        jsonSummary.addProperty("title", title.toString());
        if (StringUtils.isNotBlank(rule.getRemark())) {
            jsonSummary.addProperty("summary", rule.getRemark());
        }
        if (StringUtils.isNotBlank(ruleData.getAlarmCallbackUrl())) {
            jsonSummary.addProperty("callback_url", ruleData.getAlarmCallbackUrl());
        }

        jsonObject.add("annotations", jsonSummary);

        Result<String> groupResult = alarmServiceExtension.getGroup(rule.getIamId(), user);
        if(!groupResult.isSuccess()){
            return groupResult;
        }

        jsonObject.addProperty("group", groupResult.getData());

        jsonObject.addProperty("priority", rule.getPriority());


        /**
         * env
         */
        JsonArray envArray = new JsonArray();
        envArray.add(rule.getEnv());
        jsonObject.add("env", envArray);

        /**
         * labels
         */
        JsonObject labels = new JsonObject();
        labels.addProperty("exceptViewLables","detailRedirectUrl.paramType");
        if(StringUtils.isNotBlank(ruleData.getAlarmDetailUrl())){
            labels.addProperty("detailRedirectUrl",ruleData.getAlarmDetailUrl());
            labels.addProperty("paramType","customerPromql");
        }

        alertUrlHelper.buildDetailRedirectUrl(user, app, rule.getAlert(), jsonSummary, labels);

        labels.addProperty("send_interval",rule.getSendInterval());
        labels.addProperty("app_iam_id",String.valueOf(rule.getIamId()));
        labels.addProperty("project_id",String.valueOf(rule.getProjectId()));
        labels.addProperty("project_name",app.getProjectName());
        //报警key
        if (StringUtils.isNotBlank(rule.getAlert())) {
            labels.addProperty("alert_key",rule.getAlert());
        }
        //报警操作
        if (StringUtils.isNotBlank(rule.getOp())) {
            labels.addProperty("alert_op",rule.getOp());
        }
        //报警阈值
        if(rule.getMetricType() == AlarmRuleMetricType.customer_promql.getCode()){

            String ruleExpr = ruleData.getExpr();
            int a = ruleExpr.lastIndexOf(">") > 0 ? ruleExpr.lastIndexOf(">") :
                    ruleExpr.lastIndexOf("<") > 0 ? ruleExpr.lastIndexOf("<") :
                    ruleExpr.lastIndexOf("=") > 0 ? ruleExpr.lastIndexOf("=") :
                    ruleExpr.lastIndexOf(">=") > 0 ? ruleExpr.lastIndexOf(">=") :
                    ruleExpr.lastIndexOf("<=") > 0 ? ruleExpr.lastIndexOf("<=") :
                    ruleExpr.lastIndexOf("!=") > 0 ? ruleExpr.lastIndexOf("!=") :
                    -1;
            log.info("add customer_promql ruleExpr :{},a:{}",ruleExpr,a);

            String value = "0.0";
            if (a > 0) {
                try {
                    value = ruleExpr.substring(a + 1).trim();
                } catch (NumberFormatException e) {
                    log.error(e.getMessage() + "ruleExpr : {} ; a : {}", ruleExpr, a, e);
                }
            }
            labels.addProperty("alert_value",value);

        }else if (rule.getValue() != null) {
            labels.addProperty("alert_value",rule.getValue().toString());
        }
        // 数据次数
        // labels.addProperty("data_count",rule.getDataCount().toString());
        if (metrics != null) {
            labels.addProperty("calert",metrics.getMessage());
            labels.addProperty("group_key",metrics.getGroupKey().getCode());
        } else {
            labels.addProperty("calert",rule.getAlert());
        }
        ReqErrorMetricsPOJO errMetrics = reqErrorMetricsService.getErrorMetricsByMetrics(rule.getAlert());
        if (errMetrics != null) {
            //错误指标标记
            labels.addProperty("metrics_flag","1");
            labels.addProperty("metrics",errMetrics.getCode());
        }
        ReqSlowMetricsPOJO slowMetrics = reqSlowMetricsService.getSlowMetricsByMetric(rule.getAlert());
        if (slowMetrics != null) {
            //慢指标标记
            labels.addProperty("metrics_flag","2");
            labels.addProperty("metrics",slowMetrics.getCode());
        }

        ResourceUsageMetrics errorMetricsByMetrics = ResourceUsageMetrics.getErrorMetricsByMetrics(rule.getAlert());
        if (errorMetricsByMetrics != null) {
            //资源利用率标记
            labels.addProperty("metrics_flag",errorMetricsByMetrics.getMetricsFlag());
            labels.addProperty("metrics",errorMetricsByMetrics.getCode());
        }
        jsonObject.add("labels", labels);


        if(rule.getMetricType().equals(AlarmRuleMetricType.preset.getCode())){
            String evaluationIntervalS = evaluationDuration + evaluationUnit;
            String expr = getExpr(rule,evaluationIntervalS,ruleData, app);
            log.info("presetMetric expr===========" + expr);
            if(StringUtils.isBlank(expr)){
                log.error("getExpr error!rule:{},projectName:{}",rule.toString(),app.getProjectName());
                return Result.fail(ErrorCode.unknownError);
            }
            jsonObject.addProperty("expr", expr);

            rule.setExpr(expr);

        }else if(rule.getMetricType().equals(AlarmRuleMetricType.customer_promql.getCode())){
            log.info("用户自定义表达式:projectId:{},projectName:{},expr:{}",app.getProjectId(),app.getProjectName(),ruleData.getExpr());
            jsonObject.addProperty("expr", ruleData.getExpr());
        }

        /**
         * alert team
         */
        String alertTeamJson = rule.getAlertTeam();

        List<String> alertMembers = ruleData.getAlertMembers();

        if(StringUtils.isBlank(alertTeamJson) && CollectionUtils.isEmpty(alertMembers)){
            log.error("AlarmService.addRule error! invalid alarmTeam and alertMembers param!");
            return Result.fail(ErrorCode.ALERT_TEAM_AND_ALERT_MEMBERS_BOTH_EMPTY);
        }

        if(StringUtils.isNotBlank(alertTeamJson)){
            JsonArray array = new Gson().fromJson(alertTeamJson, JsonArray.class);
            jsonObject.add("alert_team", array);
        }

        if(!CollectionUtils.isEmpty(alertMembers)){
            JsonArray array = new Gson().fromJson(JSON.toJSONString(alertMembers), JsonArray.class);
            jsonObject.add("alert_member", array);
        }

        if(!CollectionUtils.isEmpty(ruleData.getAtMembers())){
            JsonArray array = new Gson().fromJson(JSON.toJSONString(ruleData.getAtMembers()), JsonArray.class);
            jsonObject.add("alert_at_people", array);
        }

        return alertServiceAdapt.addRule(jsonObject,String.valueOf(rule.getIamId()),user);
    }

    public Result editRule(AppAlarmRule rule,AlarmRuleData ruleData,AppMonitor app,String user){

        /**
         * 接口可修改项：
         * cname
         * expr
         * for
         * labels
         * annotations
         * group
         * priority
         * env
         * alert_team
         * alert_member
         */

        JsonObject jsonObject = new JsonObject();


        /**
         * cname
         */
        if(StringUtils.isNotBlank(rule.getCname())){
            jsonObject.addProperty("cname", rule.getCname());
        }

        /**
         * for
         */
        if(StringUtils.isNotBlank(rule.getForTime())){
            jsonObject.addProperty("for", rule.getForTime());
            jsonObject.addProperty("forTime", rule.getForTime());
        }

        /**
         * annotations
         */
        StringBuilder title = new StringBuilder().append(app.getProjectName());
        AlarmPresetMetricsPOJO metrics = alarmPresetMetricsService.getByCode(rule.getAlert());
        if (metrics != null) {
            title.append("&").append(metrics.getMessage());
        } else {
            //check tesla metrics
            teslaService.checkTeslaMetrics(title, rule.getAlert());

        }
        JsonObject jsonSummary = new JsonObject();
        jsonSummary.addProperty("title", title.toString());
        if (StringUtils.isNotBlank(rule.getRemark())) {
            jsonSummary.addProperty("summary", rule.getRemark());
        }
        if (StringUtils.isNotBlank(ruleData.getAlarmCallbackUrl())) {
            jsonSummary.addProperty("callback_url", ruleData.getAlarmCallbackUrl());
        }

        jsonObject.add("annotations", jsonSummary);

        /**
         * priority
         */
        if(StringUtils.isNotBlank(rule.getPriority())){
            jsonObject.addProperty("priority", rule.getPriority());
        }


        /**
         * labels
         */
        JsonObject labels = new JsonObject();
        labels.addProperty("exceptViewLables","detailRedirectUrl.paramType");

        if(StringUtils.isNotBlank(ruleData.getAlarmDetailUrl())){
            labels.addProperty("detailRedirectUrl",ruleData.getAlarmDetailUrl());
            labels.addProperty("paramType","customerPromql");
        }

        alertUrlHelper.buildDetailRedirectUrl(user, app, rule.getAlert(), jsonSummary, labels);

        labels.addProperty("send_interval",rule.getSendInterval());
        labels.addProperty("app_iam_id",String.valueOf(rule.getIamId()));
        labels.addProperty("project_id",String.valueOf(rule.getProjectId()));
        labels.addProperty("project_name",app.getProjectName());
        //报警key
        if (StringUtils.isNotBlank(rule.getAlert())) {
            labels.addProperty("alert_key",rule.getAlert());
        }
        //报警操作
        if (StringUtils.isNotBlank(rule.getOp())) {
            labels.addProperty("alert_op",rule.getOp());
        }

        //报警阈值
        if(rule.getMetricType() == AlarmRuleMetricType.customer_promql.getCode()){

            String ruleExpr = ruleData.getExpr();
            int a = ruleExpr.lastIndexOf(">") > 0 ? ruleExpr.lastIndexOf(">") :
                    ruleExpr.lastIndexOf("<") > 0 ? ruleExpr.lastIndexOf("<") :
                            ruleExpr.lastIndexOf("=") > 0 ? ruleExpr.lastIndexOf("=") :
                                    ruleExpr.lastIndexOf(">=") > 0 ? ruleExpr.lastIndexOf(">=") :
                                            ruleExpr.lastIndexOf("<=") > 0 ? ruleExpr.lastIndexOf("<=") :
                                                    ruleExpr.lastIndexOf("!=") > 0 ? ruleExpr.lastIndexOf("!=") :
                                                            -1;
            log.info("edit customer_promql ruleExpr :{},a:{}",ruleExpr,a);

            String value = "0.0";
            if (a > 0) {
                try {
                    value = ruleExpr.substring(a + 1).trim();
                } catch (NumberFormatException e) {
                    log.error(e.getMessage() + "ruleExpr : {} ; a : {}", ruleExpr, a, e);
                }
            }
            labels.addProperty("alert_value",value);

        }else if (rule.getValue() != null) {
            labels.addProperty("alert_value",rule.getValue().toString());
        }

        if (metrics != null) {
            labels.addProperty("calert",metrics.getMessage());
            labels.addProperty("group_key",metrics.getGroupKey().getCode());
        } else {
            labels.addProperty("calert",rule.getAlert());
        }

        ReqErrorMetricsPOJO errMetrics = reqErrorMetricsService.getErrorMetricsByMetrics(rule.getAlert());
        if (errMetrics != null) {
            //错误指标标记
            labels.addProperty("metrics_flag","1");
            labels.addProperty("metrics",errMetrics.getCode());
        }
        ReqSlowMetricsPOJO slowMetrics = reqSlowMetricsService.getSlowMetricsByMetric(rule.getAlert());
        if (slowMetrics != null) {
            //慢指标标记
            labels.addProperty("metrics_flag","2");
            labels.addProperty("metrics",slowMetrics.getCode());
        }

        ResourceUsageMetrics errorMetricsByMetrics = ResourceUsageMetrics.getErrorMetricsByMetrics(rule.getAlert());
        if (errorMetricsByMetrics != null) {
            //资源利用率标记
            labels.addProperty("metrics_flag",errorMetricsByMetrics.getMetricsFlag());
            labels.addProperty("metrics",errorMetricsByMetrics.getCode());
        }
        jsonObject.add("labels", labels);


        /**
         * expr
         */
        if(rule.getMetricType().equals(AlarmRuleMetricType.preset.getCode())){
            String evaluationIntervalS = evaluationDuration + evaluationUnit;
            String expr = getExpr(rule,evaluationIntervalS,ruleData, app);
            log.info("presetMetric expr===========" + expr);
            if(StringUtils.isBlank(expr)){
                log.error("getExpr error!rule:{},projectName:{}",rule.toString(),app.getProjectName());
                return Result.fail(ErrorCode.unknownError);
            }
            jsonObject.addProperty("expr", expr);

            rule.setExpr(expr);

        }else if(rule.getMetricType().equals(AlarmRuleMetricType.customer_promql.getCode())){
            log.info("用户自定义表达式:projectId:{},projectName:{},expr:{}",app.getProjectId(),app.getProjectName(),ruleData.getExpr());
            jsonObject.addProperty("expr", ruleData.getExpr());
            rule.setExpr(ruleData.getExpr());
        }

        /**
         * alert team and alert_members
         */
        String alertTeamJson = rule.getAlertTeam();

        List<String> alertMembers = ruleData.getAlertMembers();

        if(StringUtils.isBlank(alertTeamJson) && CollectionUtils.isEmpty(alertMembers)){
            log.error("AlarmService.editRule error! invalid alarmTeam and alertMembers param!");
            return Result.fail(ErrorCode.ALERT_TEAM_AND_ALERT_MEMBERS_BOTH_EMPTY);
        }

        if(StringUtils.isNotBlank(alertTeamJson)){
            JsonArray array = new Gson().fromJson(alertTeamJson, JsonArray.class);
            jsonObject.add("alert_team", array);
        }


        JsonArray membersArray = new JsonArray();
        if(!CollectionUtils.isEmpty(alertMembers)){
            membersArray = new Gson().fromJson(JSON.toJSONString(alertMembers), JsonArray.class);
        }
        jsonObject.add("alert_member", membersArray);


        JsonArray atMembersArray = new JsonArray();
        if(!CollectionUtils.isEmpty(ruleData.getAtMembers())){
            atMembersArray = new Gson().fromJson(JSON.toJSONString(ruleData.getAtMembers()), JsonArray.class);
        }
        jsonObject.add("alert_at_people", atMembersArray);

        return alertServiceAdapt.editRule(rule.getAlarmId(),jsonObject,String.valueOf(rule.getIamId()),user);
    }

    public Result<JsonElement>  getAlarmRuleRemote(Integer alarmId,Integer iamId,String user){
        return alertServiceAdapt.getAlarmRuleRemote(alarmId,iamId,user);
    }

    public Result updateAlarm(Integer alarmId,Integer iamId,String user,String body){
        return alertServiceAdapt.updateAlarm(alarmId,iamId,user,body);
    }


    public Result deleteRule(Integer alarmId,Integer iamId, String user){
        return alertServiceAdapt.delRule(alarmId,String.valueOf(iamId),user);
    }

    public Result enabledRule(Integer alarmId,Integer pauseStatus,Integer iamId, String user){
        return alertServiceAdapt.enableRule(alarmId,pauseStatus,String.valueOf(iamId),user);
    }

    public Result<PageData> queryRuels(Integer iamId, String user, String alert, String cname, String env, String priority, String expr, Map<String,String> labels){


        JsonObject params = new JsonObject();

        if(!CollectionUtils.isEmpty(labels)){
            Set<Map.Entry<String, String>> set = labels.entrySet();
            JsonArray jsonLabels = new JsonArray();
            for(Map.Entry<String, String> entry : set){
                JsonObject jsonAlertTeam = new JsonObject();
                jsonAlertTeam.addProperty(entry.getKey(),entry.getValue());
                jsonLabels.add(jsonAlertTeam);
            }
            params.add("labels",jsonLabels);
        }

        if(StringUtils.isNotBlank(alert)){
            params.addProperty("alert",alert);
        }
        if(StringUtils.isNotBlank(cname)){
            params.addProperty("cname",cname);
        }
        if(StringUtils.isNotBlank(env)){
            params.addProperty("env",env);
        }
        if(StringUtils.isNotBlank(priority)){
            params.addProperty("priority",priority);
        }
        if(StringUtils.isNotBlank(expr)){
            params.addProperty("expr",expr);
        }

        return alertServiceAdapt.queryRuels(params,String.valueOf(iamId),user);
    }

    /**
     *
     * @param alarmGroup
     * @param iamId
     * @param user
     * @return
     * eg:
     * {
     *     "code":0,
     *     "message":"success",
     *     "data":{
     *         "id":1137
     *     }
     * }
     */
    public Result<JsonElement> addAlarmGroup(String alarmGroup,Integer iamId,String user){

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("group", alarmGroup);
        String evaluationIntervalS = evaluationInterval + evaluationUnit;
        jsonObject.addProperty("interval", evaluationIntervalS);

        return alertServiceAdapt.addAlarmGroup(jsonObject,String.valueOf(iamId),user);
    }

    public Result<JsonElement> searchAlarmGroup(String alarmGroup,Integer iamId,String user){
        return alertServiceAdapt.searchAlarmGroup(alarmGroup,String.valueOf(iamId),user);
    }

    /**
     *
     * @param name oncall报警组名称
     * @param note oncall 组简介
     * @param manager 值班经理
     * @param oncallUser oncall 成员
     * @param service oncall 服务
     * @param iamId
     * @param user
     * @param page_no 页码
     * @param page_size 每页条数
     * @return
     */
    public Result<PageData> searchAlertTeam(String name,String note,String manager,String oncallUser,String service,Integer iamId,String user,Integer page_no,Integer page_size){
        return alertServiceAdapt.searchAlertTeam(name,note,manager,oncallUser,service,iamId,user,page_no,page_size);
    }

    public Result<PageData> queryEvents(String user, Integer treeId, String alertLevel, Long startTime, Long endTime, Integer pageNo, Integer pageSize, JsonObject labels) {
        return alertServiceAdapt.queryEvents(user,treeId,alertLevel,startTime,endTime,pageNo,pageSize,labels);
    }

    public Result<PageData> queryLatestEvents(Set<Integer> treeIdSet, String alertStat, String alertLevel, Long startTime, Long endTime, Integer pageNo, Integer pageSize, JsonObject labels) {
        return alertServiceAdapt.queryLatestEvents(treeIdSet,alertStat,alertLevel,startTime,endTime,pageNo,pageSize,labels);
    }

    public Result<JsonObject> getEventById(String user, Integer treeId, String eventId) {
        return alertServiceAdapt.getEventById(user,treeId,eventId);
    }

     public Result<JsonObject> resolvedEvent(String user, Integer treeId, String alertName, String comment, Long startTime, Long endTime) {
        return alertServiceAdapt.resolvedEvent(user,treeId,alertName,comment,startTime,endTime);
    }

}
