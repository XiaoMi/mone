package com.xiaomi.mone.monitor.service.prometheus;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.bo.*;
import com.xiaomi.mone.monitor.dao.model.AppAlarmRule;
import com.xiaomi.mone.monitor.dao.model.AppMonitor;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.AppMonitorService;
import com.xiaomi.mone.monitor.service.alertmanager.AlertServiceAdapt;
import com.xiaomi.mone.monitor.service.model.PageData;
import com.xiaomi.mone.monitor.service.model.prometheus.AlarmRuleData;
import com.xiaomi.mone.monitor.service.model.prometheus.Metric;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

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
    private static final String dubbo_consumer_error_metric = "dubboConsumerError";
    private static final String dubbo_provider_error_metric = "dubboProviderError";
    private static final String redis_error_metric = "redisError";

    /**
     * 业务慢查询指标
     */
    private static final String http_slow_query_metric = "httpSlowQuery";
    private static final String http_client_slow_query_metric = "httpClientSlowQuery";//http client 慢查询
    private static final String dubbo_consumer_slow_query_metric = "dubboConsumerSlowQuery";
    private static final String dubbo_provider_slow_query_metric = "dubboProviderSlowQuery";
    private static final String db_slow_query_metric = "dbSlowQuery";
    private static final String redis_slow_query_metric = "redisSlowQuery";


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
    private static final String dubbo_consumer_time_cost = "dubboConsumerTimeCost";
    private static final String dubbo_provider_time_cost = "dubboProviderCount";
    //db
    private static final String db_avalible_success_metric = "sqlSuccessCount";
    private static final String db_avalible_total_metric = "sqlTotalCount";


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


    @Value("${alarm.domain}")
    private String alarmDomain;
    @NacosValue("${iam.ak:noconfig}")
    private String cloudAk;
    @NacosValue("${iam.sk:noconfig}")
    private String cloudSk;

    @Value("${prometheus.alarm.env:staging}")
    private String prometheusAlarmEnv;

    @NacosValue(value = "${rule.evaluation.interval:20}",autoRefreshed = true)
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

    @NacosValue(value = "${tesla.increase.duration:5m}",autoRefreshed = true)
    private String teslaIncreaseDuration;

    @Value("${alert.manager.env:staging}")
    private String alertManagerEnv;

    @Autowired
    PrometheusService prometheusService;

    @Autowired
    AppMonitorService appMonitorService;

    @Autowired
    AlertServiceAdapt alertServiceAdapt;


    public String getExpr(AppAlarmRule rule,String scrapeIntervel,AlarmRuleData ruleData, AppMonitor app){

        if(StringUtils.isBlank(rule.getAlert())){
            return null;
        }

        Map<String, String> includLabels = new HashMap<>();
        Map<String, String> exceptLabels = new HashMap<>();

        if(MetricLabelKind.httpType(rule.getAlert())){
            includLabels = getLabels(ruleData, AppendLabelType.http_include_uri);
            Map<String, String> httpIncludeErrorCode = getLabels(ruleData, AppendLabelType.http_include_errorCode);
            includLabels.putAll(httpIncludeErrorCode);

            exceptLabels = getLabels(ruleData, AppendLabelType.http_except_uri);
            Map<String, String> httpExceptErrorCode = getLabels(ruleData, AppendLabelType.http_except_errorCode);
            exceptLabels.putAll(httpExceptErrorCode);
        }

        if(MetricLabelKind.dubboType(rule.getAlert())){
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
            case "http_client_availability":
                return getAvailableRate(http_client_error_metric,http_client_method_total_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,avalible_duration_time,null,rule.getOp(),rule.getValue());
            case "http_client_error_times" :
                return getPresetMetricErrorAlarm(http_client_error_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());
            case "http_client_cost" :
                return getPresetMetricCostAlarm(http_client_method_time_count,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels, scrapeIntervel,null, rule.getOp(),rule.getValue());
            case "http_client_qps" :
                return getPresetMetricQpsAlarm(http_client_method_total_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix, scrapeIntervel,null,rule.getOp(),rule.getValue());
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
            case "db_error_times":
                return getPresetMetricErrorAlarm(db_error_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());
            case "db_slow_query":
                return getPresetMetricErrorAlarm(db_slow_query_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,scrapeIntervel,null,rule.getOp(),rule.getValue());
            case "db_availability":
                return getAvailableRate(db_error_metric,db_avalible_total_metric,rule.getProjectId(),app.getProjectName(),includLabels,exceptLabels,metric_total_suffix,avalible_duration_time,null,rule.getOp(),rule.getValue());
            case "container_cpu_use_rate":
                return getContainerCpuAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),false);
            case "container_cpu_average_load":
                return getContainerLoadAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),false);
            case "container_mem_use_rate":
                return getContainerMemAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),false);
            case "container_count_monitor":
                return getContainerCountAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),false);
            case "app_restart_monitor":
                return getAppRestartAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),false);
            case "app_crash_monitor":
                return getAppCrashAlarmExpr(rule.getProjectId(),app.getProjectName());

            case "container_cpu_resource_use_rate":
                return getContainerCpuResourceAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),false);
            case "container_mem_resource_use_rate":
                return getContainerMemReourceAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),false);

            case "k8s_container_cpu_use_rate":
                return getContainerCpuAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),true);
            case "k8s_container_cpu_average_load":
                return getContainerLoadAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),true);
            case "k8s_container_mem_use_rate":
                return getContainerMemAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),true);
            case "k8s_container_count_monitor":
                return getContainerCountAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),true);

            case "k8s_cpu_resource_use_rate":
                return getContainerCpuResourceAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),true);
            case "k8s_mem_resource_use_rate":
                return getContainerMemReourceAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue(),true);

           case "k8s_cpu_avg_use_rate":
                return getK8sCpuAvgUsageAlarmExpr(rule.getProjectId(),app.getProjectName(),rule.getOp(),rule.getValue());

            case "jvm_heap_mem_use_rate":
                return getJvmMemAlarmExpr(rule.getProjectId(),app.getProjectName(),"heap", rule.getOp(), rule.getValue());
            case "jvm_no_heap_mem_use_rate":
                return getJvmMemAlarmExpr(rule.getProjectId(),app.getProjectName(),"nonheap", rule.getOp(), rule.getValue());
            case "jvm_thread_num":
                return getJvmThreadAlarmExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue());
            case "jvm_gc_cost":
                return getJvmGcCostExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),false);
            case "jvm_gc_times":
                return getJvmGcCountExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),false);
            case "jvm_full_gc_cost":
                return getJvmGcCostExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),true);
            case "jvm_full_gc_times":
                return getJvmGcCountExpr(rule.getProjectId(),app.getProjectName(), rule.getOp(), rule.getValue(),true);
            default:

                if (rule.getAlert().endsWith("_tesla_availability")) {
                    return getTeslaAvailability(ruleData);
                }

                if(rule.getAlert().endsWith("_tesla_p99_time_cost")){
                    return getTeslaTimeCost4P99(ruleData);
                }


                AlarmPresetMetrics presetMetric = AlarmPresetMetrics.getByCode(rule.getAlert());
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

    private String getTeslaLabels(String uris){
        if(StringUtils.isBlank(uris)){
            return null;
        }

        StringBuilder builder = new StringBuilder();
        String[] urls = uris.split(",");
        for(String url : urls){
            url = url.replace("/mtop/","");
            if(url.indexOf("/") == 0 ){
                //去掉开头的/，指标的label中url不是以/开头的
                url = url.substring(1);
            }

            if(StringUtils.isNotBlank(url)){
                builder.append(url).append("|");
            }

        }

        String urlsMatch = builder.toString();
        if(StringUtils.isNotBlank(urlsMatch)){
            urlsMatch = urlsMatch.substring(0,urlsMatch.length()-1);
        }

        return urlsMatch;
    }

    public String getTeslaTimeCost4P99(AlarmRuleData rule){

        TeslaMetricInfo metricInfo = TeslaMetricGroup.TeslaMetrics.getMetricInfoByCode(rule.getAlert());
        if(metricInfo == null){
            log.error("getTeslaTimeCost4P99# no metric info found! alert:{}",rule.getAlert());
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("(histogram_quantile(0.99,sum(rate(").append(metricInfo.getTimeBucketMetric()).append("{");
        stringBuilder.append("job='").append(metricInfo.getJobName()).append("'");

        if(StringUtils.isNotBlank(rule.getTeslaGroup())){
            //tesla group config
            stringBuilder.append(",group=~'").append(rule.getTeslaGroup().replaceAll("/mtop","").replaceAll("/","")).append("'");
        }

        if(StringUtils.isNotBlank(rule.getTeslaUrls())){

            String teslaLabels = getTeslaLabels(rule.getTeslaUrls());
            stringBuilder.append(",url=~'").append(teslaLabels).append("'");

        }else if(StringUtils.isNotBlank(rule.getExcludeTeslaUrls())){
            String teslaLabels = getTeslaLabels(rule.getExcludeTeslaUrls());
            stringBuilder.append(",url!~'").append(teslaLabels).append("'");
        }

        stringBuilder.append("}[").append(teslaIncreaseDuration).append("])) by (system,le,");

        if(StringUtils.isNotBlank(rule.getTeslaGroup()) || StringUtils.isNotBlank(rule.getTeslaUrls()) || StringUtils.isNotBlank(rule.getExcludeTeslaUrls())){
            stringBuilder.append("url,");
        }

        stringBuilder.append("group,instance,ip))) ");

        stringBuilder.append(rule.getOp());
        stringBuilder.append(rule.getValue());
        return stringBuilder.toString();
    }

    public String getTeslaAvailability(AlarmRuleData rule){

        TeslaMetricInfo metricInfo = TeslaMetricGroup.TeslaMetrics.getMetricInfoByCode(rule.getAlert());
        if(metricInfo == null){
            log.error("getTeslaAvailability# no metric info found! alert:{}",rule.getAlert());
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("clamp_min(");

        stringBuilder.append("(1- (sum(increase(").append(metricInfo.getErrorCallMetric()).append("{");

        stringBuilder.append("job='").append(metricInfo.getJobName()).append("'");

        if(StringUtils.isNotBlank(rule.getTeslaGroup())){
            stringBuilder.append(",group=~'").append(rule.getTeslaGroup().replaceAll("/mtop","").replaceAll("/","")).append("'");
        }

        if(StringUtils.isNotBlank(rule.getTeslaUrls())){

            String teslaLabels = getTeslaLabels(rule.getTeslaUrls());
            stringBuilder.append(",url=~'").append(teslaLabels).append("'");

        }else if(StringUtils.isNotBlank(rule.getExcludeTeslaUrls())){
            String teslaLabels = getTeslaLabels(rule.getExcludeTeslaUrls());
            stringBuilder.append(",url!~'").append(teslaLabels).append("'");
        }

        stringBuilder.append("}[")
                .append(teslaIncreaseDuration)
                .append("])>0) by (system,");

        if(StringUtils.isNotBlank(rule.getTeslaGroup()) || StringUtils.isNotBlank(rule.getTeslaUrls()) || StringUtils.isNotBlank(rule.getExcludeTeslaUrls())){
            stringBuilder.append("url,");
        }

        stringBuilder.append("group,instance,ip) / sum(increase(").append(metricInfo.getTotalCallMetric()).append("{");

        stringBuilder.append("job='").append(metricInfo.getJobName()).append("'");

        if(StringUtils.isNotBlank(rule.getTeslaGroup())){
            stringBuilder.append(",group=~'").append(rule.getTeslaGroup().replaceAll("/mtop","").replaceAll("/","")).append("'");
        }
        if(StringUtils.isNotBlank(rule.getTeslaUrls())){
            String teslaLabels = getTeslaLabels(rule.getTeslaUrls());
            stringBuilder.append(",url=~'").append(teslaLabels).append("'");

        }else if(StringUtils.isNotBlank(rule.getExcludeTeslaUrls())){
            String teslaLabels = getTeslaLabels(rule.getExcludeTeslaUrls());
            stringBuilder.append(",url!~'").append(teslaLabels).append("'");
        }

        stringBuilder.append("}[")
                .append(teslaIncreaseDuration).append("])>0) by (system,");

        if(StringUtils.isNotBlank(rule.getTeslaGroup())  || StringUtils.isNotBlank(rule.getTeslaUrls()) || StringUtils.isNotBlank(rule.getExcludeTeslaUrls())){
            stringBuilder.append("url,");
        }
        stringBuilder.append("group,instance,ip))),0) * 100 ");


        stringBuilder.append(rule.getOp());
        stringBuilder.append(rule.getValue());
        return stringBuilder.toString();
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
            if(!CollectionUtils.isEmpty(ruleData.getIncludeServices())){
                fillLabels(map,"service",String.join(",",ruleData.getIncludeServices()));
            }

//            if(!CollectionUtils.isEmpty(ruleData.getIncludeModules())){
//                fillLabels(map,"functionModule",String.join(",",ruleData.getIncludeModules()));
//            }

            if(!CollectionUtils.isEmpty(ruleData.getIncludeFunctions())){
                fillLabels(map,"functionId",String.join(",",ruleData.getIncludeFunctions()));
            }

        }
        if(!isInclude){
            if(!CollectionUtils.isEmpty(ruleData.getExceptEnvs())){
                fillLabels(map,"serverEnv",String.join(",",ruleData.getExceptEnvs()));
            }
            if(!CollectionUtils.isEmpty(ruleData.getExceptServices())){
                fillLabels(map,"service",String.join(",",ruleData.getExceptServices()));
            }

//            if(!CollectionUtils.isEmpty(ruleData.getExceptModules())){
//                fillLabels(map,"functionModule",String.join(",",ruleData.getExceptModules()));
//            }
            if(!CollectionUtils.isEmpty(ruleData.getExceptFunctions())){
                fillLabels(map,"functionId",String.join(",",ruleData.getExceptFunctions()));
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
                .append("sum(sum_over_time(").append(errorMetricComplete).append("))").append(" by (application,system,serverIp,serviceName,methodName,sqlMethod,service,serverEnv,functionModule,functionName)")
                .append("/")
                .append("sum(sum_over_time(").append(totalMetricComplate).append("))").append(" by (application,system,serverIp,serviceName,methodName,sqlMethod,service,serverEnv,functionModule,functionName)")
                .append(")),0) * 100")
                .append(op).append(value);


        log.info("AlarmService.getAvailableRate param" +
                        ":errorMetric:{},totalMetric:{},projectId:{},projectName:{},includeLabels:{},exceptLabels:{},metricSuffix:{},duration:{},offset:{},op:{},value:{},return : {}"
                ,errorMetric,totalMetric,projectId,projectName,includeLabels,exceptLabels,metricSuffix,duration,offset,op,value,expBuilder.toString());
        return expBuilder.toString();
    }

    public String getContainerLoadAlarmExpr(Integer projectId,String projectName,String op,double value,boolean isK8s){

        String appGitName = appMonitorService.getAppGitName(projectId, projectName);
        if(StringUtils.isNotBlank(appGitName)){
            projectName = appGitName;
        }
        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("avg_over_time(container_cpu_load_average_10s");
        exprBuilder.append("{system='mione',");
        exprBuilder.append("image!='',");

        if(isK8s){

            exprBuilder.append("container=~'").append(".*-").append(projectName).append("-.*")
                    .append("|").append(projectId).append("-0").append("")
                    .append("|").append(projectId).append("-0-.*").append("'");
        }else{

            exprBuilder.append("container_label_PROJECT_ID='").append(projectId).append("',");
            exprBuilder.append("name=~'").append(projectName).append("-20.*'");
        }

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
    public String getContainerCpuAlarmExpr(Integer projectId,String projectName,String op,double value,boolean isK8s){

        String appGitName = appMonitorService.getAppGitName(projectId, projectName);
        if(StringUtils.isNotBlank(appGitName)){
            projectName = appGitName;
        }

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("rate(container_cpu_user_seconds_total{system='mione',");

        exprBuilder.append("image!='',");

        if(isK8s){

            exprBuilder.append("container=~'").append(".*-").append(projectName).append("-.*")
                    .append("|").append(projectId).append("-0").append("")
                    .append("|").append(projectId).append("-0-.*").append("'");
        }else{

            exprBuilder.append("container_label_PROJECT_ID='").append(projectId).append("',");
            exprBuilder.append("name=~'").append(projectName).append("-20.*'");
        }

        exprBuilder.append("}[1m]) * 100");
        exprBuilder.append(op).append(value);
        log.info("getContainerCpuAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }
    public String getContainerCpuResourceAlarmExpr(Integer projectId,String projectName,String op,double value,boolean isK8s){

        String appGitName = appMonitorService.getAppGitName(projectId, projectName);
        if(StringUtils.isNotBlank(appGitName)){
            projectName = appGitName;
        }

        String jobLabelValue = prometheusAlarmEnv.equals("production") ? "mione-online-china.*|mione-online-youpin.*" : "mione-staging-china.*|mione-staging-youpin.*";

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("sum(irate(container_cpu_usage_seconds_total{");
        exprBuilder.append("image!='',system='mione',");
        exprBuilder.append("job=~'").append(jobLabelValue).append("',");
        if(projectName.equals("mimonitor")){
            if(isK8s){
                exprBuilder.append("name =~'k8s.*',");
            }else{
                exprBuilder.append("container_label_PROJECT_ID!='',name !~'k8s.*',");
            }

        }else{

            if(isK8s){

                exprBuilder.append("container=~'").append(".*-").append(projectName).append("-.*")
                        .append("|").append(projectId).append("-0").append("")
                        .append("|").append(projectId).append("-0-.*").append("'");
            }else{

                exprBuilder.append("container_label_PROJECT_ID='").append(projectId).append("',");
                exprBuilder.append("name=~'").append(projectName).append("-20.*'");
            }

        }

        exprBuilder.append("}[1d])) without (cpu) * 100");
        exprBuilder.append(op).append(value);
        log.info("getContainerCpuResourceAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getContainerMemAlarmExpr(Integer projectId,String projectName,String op,double value,boolean isK8s){

        String appGitName = appMonitorService.getAppGitName(projectId, projectName);
        if(StringUtils.isNotBlank(appGitName)){
            projectName = appGitName;
        }

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("(sum(avg_over_time(container_memory_rss{");

        exprBuilder.append("image!='',system='mione',");

        if(isK8s){

            exprBuilder.append("container=~'").append(".*-").append(projectName).append("-.*")
                    .append("|").append(projectId).append("-0").append("")
                    .append("|").append(projectId).append("-0-.*").append("'");
        }else{

            exprBuilder.append("container_label_PROJECT_ID='").append(projectId).append("',");
            exprBuilder.append("name=~'").append(projectName).append("-20.*'");
        }



        exprBuilder.append("}[1m])) by (container_label_PROJECT_ID,ip,job,name,system,instance,id,pod,namespace,serverEnv) / ");
        exprBuilder.append("sum(avg_over_time(container_spec_memory_limit_bytes{");
        exprBuilder.append("image!='',system='mione',");

        if(isK8s){

            exprBuilder.append("container=~'").append(".*-").append(projectName).append("-.*")
                    .append("|").append(projectId).append("-0").append("")
                    .append("|").append(projectId).append("-0-.*").append("'");
        }else{

            exprBuilder.append("container_label_PROJECT_ID='").append(projectId).append("',");
            exprBuilder.append("name=~'").append(projectName).append("-20.*'");
        }
        exprBuilder.append("}[1m])) by (container_label_PROJECT_ID,ip,job,name,system,instance,id,pod,namespace,serverEnv)) * 100");
        exprBuilder.append(op).append(value);
        log.info("getContainerMemAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getContainerMemReourceAlarmExpr(Integer projectId,String projectName,String op,double value,boolean isK8s){

        String appGitName = appMonitorService.getAppGitName(projectId, projectName);
        if(StringUtils.isNotBlank(appGitName)){
            projectName = appGitName;
        }

        String jobLabelValue = prometheusAlarmEnv.equals("production") ? "mione-online-china.*|mione-online-youpin.*" : "mione-staging-china.*|mione-staging-youpin.*";

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("(sum(avg_over_time(container_memory_rss{");
        exprBuilder.append("image!='',system='mione',");
        exprBuilder.append("job=~'").append(jobLabelValue).append("',");

        if(projectName.equals("mimonitor")){
            if(isK8s){
                exprBuilder.append("name =~'k8s.*'");
            }else{
                exprBuilder.append("container_label_PROJECT_ID!='',name !~'k8s.*'");
            }

        }else{

            if(isK8s){

                exprBuilder.append("container=~'").append(".*-").append(projectName).append("-.*")
                        .append("|").append(projectId).append("-0").append("")
                        .append("|").append(projectId).append("-0-.*").append("'");
            }else{

                exprBuilder.append("container_label_PROJECT_ID='").append(projectId).append("',");
                exprBuilder.append("name=~'").append(projectName).append("-20.*'");
            }
        }

        exprBuilder.append("}[1d])) by (container_label_PROJECT_ID,ip,job,name,system,instance,id,serverEnv) / ");
        exprBuilder.append("sum(avg_over_time(container_spec_memory_limit_bytes{");
        exprBuilder.append("image!='',system='mione',");
        exprBuilder.append("job=~'").append(jobLabelValue).append("',");

        if(projectName.equals("mimonitor")){
            if(isK8s){
                exprBuilder.append("name =~'k8s.*'");
            }else{
                exprBuilder.append("container_label_PROJECT_ID!='',name !~'k8s.*'");
            }

        }else{

            if(isK8s){

                exprBuilder.append("container=~'").append(".*-").append(projectName).append("-.*")
                        .append("|").append(projectId).append("-0").append("")
                        .append("|").append(projectId).append("-0-.*").append("'");
            }else{

                exprBuilder.append("container_label_PROJECT_ID='").append(projectId).append("',");
                exprBuilder.append("name=~'").append(projectName).append("-20.*'");
            }
        }

        exprBuilder.append("}[1d])) by (container_label_PROJECT_ID,ip,job,name,system,instance,id,serverEnv)) * 100");
        exprBuilder.append(op).append(value);
        log.info("getContainerMemReourceAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }

    public String getK8sCpuAvgUsageAlarmExpr(Integer projectId,String projectName,String op,double value){


        String jobLabelValue = prometheusAlarmEnv.equals("production") ? "mione-online-china.*|mione-online-youpin.*" : "mione-staging-china.*|mione-staging-youpin.*";

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("sum(irate(container_cpu_usage_seconds_total{");
        exprBuilder.append("image!='',system='mione',");
        exprBuilder.append("job=~'").append(jobLabelValue).append("',");
        exprBuilder.append("container=~'").append(projectId).append("-0-.*").append("'");
        exprBuilder.append("}[1m])) without (cpu) * 100 ");
        exprBuilder.append("/");
        exprBuilder.append("(");
        exprBuilder.append("container_spec_cpu_quota{");
        exprBuilder.append("system='mione',");
        exprBuilder.append("job=~'").append(jobLabelValue).append("',");
        exprBuilder.append("container=~'").append(projectId).append("-0-.*").append("'");
        exprBuilder.append("}");
        exprBuilder.append("/");

        exprBuilder.append("container_spec_cpu_period{");
        exprBuilder.append("system='mione',");
        exprBuilder.append("job=~'").append(jobLabelValue).append("',");
        exprBuilder.append("container=~'").append(projectId).append("-0-.*").append("'");
        exprBuilder.append("}");
        exprBuilder.append(")");

        exprBuilder.append(op).append(value);
        log.info("getK8sCpuAvgUsageAlarmExpr param: projectId:{}, projectName:{}, op:{},value:{}, return:{}",projectId, projectName, op,value, exprBuilder.toString());
        return exprBuilder.toString();
    }


    public String getContainerCountAlarmExpr(Integer projectId,String projectName,String op,double value,boolean isK8s){

        String appGitName = appMonitorService.getAppGitName(projectId, projectName);
        if(StringUtils.isNotBlank(appGitName)){
            projectName = appGitName;
        }
        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("count(sum_over_time(container_spec_memory_limit_bytes{");
        exprBuilder.append("image!='',system='mione',");
        if(isK8s){

            exprBuilder.append("container=~'").append(".*-").append(projectName).append("-.*")
                    .append("|").append(projectId).append("-0").append("")
                    .append("|").append(projectId).append("-0-.*").append("'");
        }else{

            exprBuilder.append("container_label_PROJECT_ID='").append(projectId).append("',");
            exprBuilder.append("name=~'").append(projectName).append("-20.*'");
        }

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

    public String getAppCrashAlarmExpr(Integer projectId,String projectName){

        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("time() - container_last_seen{");
        exprBuilder.append("system='mione',");
        exprBuilder.append("application='").append(projectId).append("_").append(projectName).append("'");
        exprBuilder.append("}").append(" > 120");

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

            if(StringUtils.isNotBlank(metric.getService())){
                stringObjectMap.putIfAbsent("serviceList", new HashMap<>());
                HashMap serviceList = (HashMap<String,Set<String>>)stringObjectMap.get("serviceList");

                serviceList.putIfAbsent(metric.getService(), new HashSet<String>());
                HashSet<String> ips = (HashSet<String>)serviceList.get(metric.getService());

                ips.add(metric.getServerIp());
            }
        }

        result.put("allIps",allIps);
        result.put("envIpMapping",mapResult);

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

    private String getPresetMetricErrorAlarm(String sourceMetric,Integer projectId,String projectName,Map includeLabels,Map exceptLabels,String metricSuffix,String duration,String offset,String op,Float value){
        String s = prometheusService.completeMetricForAlarm(sourceMetric, includeLabels,exceptLabels, projectId,projectName, metricSuffix,  duration, null);

        StringBuilder expBuilder = new StringBuilder();
        expBuilder.append("sum(")
                .append("sum_over_time").append("(").append(s).append(")")
                .append(") by (application,system,serverIp,serviceName,methodName,sqlMethod,errorCode,service,serverEnv,functionModule,functionName)")
                .append(op).append(value);


        log.info("AlarmService.getPresetMetricErrorAlarm param" +
                        ":sourceMetric:{},projectId:{},projectName:{},includeLabels:{},exceptLabels:{},metricSuffix:{},duration:{},offset:{},op:{},value:{},return : {}"
                ,sourceMetric,projectId,projectName,includeLabels,exceptLabels,metricSuffix,duration,offset,op,value,expBuilder.toString());
        return expBuilder.toString();
    }

    private String getPresetMetricQpsAlarm(String sourceMetric,Integer projectId,String projectName,Map includeLabels,Map exceptLabels,String metricSuffix,String duration, String offset,String op,Float value){
        String s = prometheusService.completeMetricForAlarm(sourceMetric, includeLabels,exceptLabels, projectId,projectName, metricSuffix,  duration, null);
        StringBuilder expBuilder = new StringBuilder();
        expBuilder.append("sum(sum_over_time(").append(s).append(")/").append(evaluationDuration).append(") by (")
                .append("application,system,serverIp,serviceName,methodName,sqlMethod,errorCode,service,serverEnv,functionModule,functionName").append(")").append(op).append(value);
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
                .append("application,system,serverIp,serviceName,methodName,sqlMethod,errorCode,service,serverEnv,functionModule,functionName").append(")")
                .append(" / ")
                .append("sum(sum_over_time(").append(countSource).append(")) by (")
                .append("application,system,serverIp,serviceName,methodName,sqlMethod,errorCode,service,serverEnv,functionModule,functionName").append(") ")
                .append(op).append(value);
        log.info("AlarmService.getPresetMetricQpsAlarm expr={}", expBuilder.toString());
        return expBuilder.toString();
    }

    private String getJvmMemAlarmExpr(Integer projectId,String projectName,String type, String op,Float value){
        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("(sum(jvm_memory_used_bytes{");
        exprBuilder.append("application=").append("'").append(projectId).append("_").append(projectName.replaceAll("-","_")).append("'").append(",");
        exprBuilder.append("area=").append("'").append(type).append("'");
        exprBuilder.append("}) by (application,area,instance,serverEnv,serverIp,service,system)/ ");
        exprBuilder.append("sum(jvm_memory_max_bytes{");
        exprBuilder.append("application=").append("'").append(projectId).append("_").append(projectName.replaceAll("-","_")).append("'").append(",");
        exprBuilder.append("area=").append("'").append(type).append("'");
        exprBuilder.append("}) by (application,area,instance,serverEnv,serverIp,service,system)) * 100");
        exprBuilder.append(op).append(value);
        log.info("getJvmMemAlarmExpr param: projectId:{}, projectName:{}, type:{}, return:{}",projectId, projectName,type, exprBuilder.toString());
        return exprBuilder.toString();
    }

    private String getJvmThreadAlarmExpr(Integer projectId,String projectName, String op,Float value){
        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("max_over_time(jvm_threads_live_threads");
        exprBuilder.append("{");
        exprBuilder.append("application=").append("'").append(projectId).append("_").append(projectName.replaceAll("-","_")).append("'").append(",");
        exprBuilder.append("ip!=").append("''");
        exprBuilder.append("}[1m])");
        exprBuilder.append(op).append(value);
        log.info("getJvmThreadAlarmExpr param: projectId:{}, projectName:{}, return:{}",projectId, projectName, exprBuilder.toString());
        return exprBuilder.toString();
    }

    private String getJvmGcCostExpr(Integer projectId,String projectName, String op,Float value,boolean isFullGc){
        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("max_over_time(jvm_gc_pause_seconds_max");
        exprBuilder.append("{");
        if(isFullGc){
            exprBuilder.append("action=~'end of major GC|endofminorGC',");
        }
        exprBuilder.append("application=").append("'").append(projectId).append("_").append(projectName.replaceAll("-","_")).append("'").append(",");
        exprBuilder.append("ip!=").append("''");
        exprBuilder.append("}[1m])");
        exprBuilder.append(op).append(value);
        log.info("getJvmThreadAlarmExpr param: projectId:{}, projectName:{}, return:{}",projectId, projectName, exprBuilder.toString());
        return exprBuilder.toString();
    }

    private String getJvmGcCountExpr(Integer projectId,String projectName, String op,Float value,boolean isFullGc){
        StringBuilder exprBuilder = new StringBuilder();
        exprBuilder.append("delta(jvm_gc_pause_seconds_count{");
        if(isFullGc){
            exprBuilder.append("action=~'end of major GC|endofminorGC',");
        }
        exprBuilder.append("application=").append("'").append(projectId).append("_").append(projectName.replaceAll("-","_")).append("'");
        exprBuilder.append("}[1m])").append(op).append(value);
        log.info("getJvmGcCountExpr param: projectId:{}, projectName:{}, return:{}",projectId, projectName,exprBuilder.toString());
        return exprBuilder.toString();
    }


    public Result addRule(AppMonitor app, AppAlarmRule rule, String user, AlarmRuleData ruleData){



        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("alert", rule.getAlert());


        jsonObject.addProperty("cname", rule.getCname());


        jsonObject.addProperty("for", rule.getForTime());
        jsonObject.addProperty("For", rule.getForTime());


        StringBuilder title = new StringBuilder().append(app.getProjectName());
        AlarmPresetMetrics metrics = AlarmPresetMetrics.getByCode(rule.getAlert());
        if (metrics != null) {
            title.append("&").append(metrics.getMessage());
        } else {
            //check tesla metrics
            TeslaMetricGroup.TeslaMetrics metricByCode = TeslaMetricGroup.TeslaMetrics.getMetricByCode(rule.getAlert());
            if(metricByCode != null){
                title.append("&").append(metricByCode.getMessage());
            }else{
                title.append("&").append(rule.getAlert());
            }

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

        if("openSource".equals(alertManagerEnv)){
            jsonObject.addProperty("group", "example");

        }else{
            /**
             * rule-group
             */
            String alarmGroup = "group" + rule.getIamId();
            Result<JsonElement> result = searchAlarmGroup(alarmGroup, rule.getIamId(), user);

            if(result.getCode() == 404){
                Result<JsonElement> groupAddResult = addAlarmGroup(alarmGroup, rule.getIamId(), user);
                if(groupAddResult.getCode() !=0 || StringUtils.isBlank(groupAddResult.getData().getAsJsonObject().get("id").getAsString())){
                    log.error("AlarmService.addRule error! add group fail!");
                    return Result.fail(ErrorCode.unknownError);
                }

            }

            jsonObject.addProperty("group", alarmGroup);
        }



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
        if(rule.getAlert().endsWith("_intranet_tesla_availability") || rule.getAlert().endsWith("_staging_tesla_availability")){

            if(rule.getAlert().startsWith("mihome_")){
                //米家社区兼容
                labels.addProperty("detailRedirectUrl",teslaAlertIntranetGrafanaUrl);
                labels.addProperty("paramType","normal");
                JsonObject json = new JsonObject();
                json.addProperty("from","${alarmTime}-5min");
                json.addProperty("to","${alarmTime}+5min");
                json.addProperty("var-url","${url}");
                json.addProperty("var-group","${group}");
//                json.addProperty("var-Node","${ip}");
                jsonSummary.addProperty("paramMapping",json.toString());
            }else{
                log.info("rule.getAlert() ===== {},teslaAlertIntranetUrl:{}",rule.getAlert(),teslaAlertIntranetUrl);
                labels.addProperty("detailRedirectUrl",teslaAlertIntranetUrl);
                labels.addProperty("paramType","tesla");
            }

        }

        if(rule.getAlert().endsWith("_outnet_tesla_availability")){

            if(rule.getAlert().startsWith("mihome_")){
                //米家社区兼容
                labels.addProperty("detailRedirectUrl",teslaAlertOutnetGrafanaUrl);
                labels.addProperty("paramType","normal");
                JsonObject json = new JsonObject();
                json.addProperty("from","${alarmTime}-5min");
                json.addProperty("to","${alarmTime}+5min");
                json.addProperty("var-url","${url}");
                json.addProperty("var-group","${group}");
//                json.addProperty("var-Node","${ip}");
                jsonSummary.addProperty("paramMapping",json.toString());
            }else{
                log.info("rule.getAlert() ===== {},teslaAlertOutnetUrl:{}",rule.getAlert(),teslaAlertOutnetUrl);
                labels.addProperty("detailRedirectUrl",teslaAlertOutnetUrl);
                labels.addProperty("paramType","tesla");
            }

        }

        if (rule.getAlert().endsWith("_tesla_p99_time_cost")) {

            labels.addProperty("detailRedirectUrl", teslaAlertTimeCostnetUrl);
            labels.addProperty("paramType", "tesla");

        }

//        if(rule.getAlert().equals("container_cpu_resource_use_rate") || rule.getAlert().equals("container_mem_resource_use_rate") ){
//            log.info("rule.getAlert() ===== {},resourceUseRateUrl:{}",rule.getAlert(),resourceUseRateUrl);
//            labels.addProperty("detailRedirectUrl",resourceUseRateUrl);
//            labels.addProperty("paramType","normal");
//            JsonObject json = new JsonObject();
//            json.addProperty("from","${alarmTime}-5min");
//            json.addProperty("to","${alarmTime}+5min");
//            json.addProperty("var-name","${name}");
//            //添加到annotations属性
//            jsonSummary.addProperty("paramMapping",json.toString());
//        }



        labels.addProperty("exceptViewLables","detailRedirectUrl.paramType");

        if(StringUtils.isNotBlank(ruleData.getAlarmDetailUrl())){
            labels.addProperty("detailRedirectUrl",ruleData.getAlarmDetailUrl());
            labels.addProperty("paramType","customerPromql");
        }

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
        if (rule.getValue() != null) {
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
        ReqErrorMetrics errMetrics = ReqErrorMetrics.getErrorMetricsByMetrics(rule.getAlert());
        if (errMetrics != null) {
            //错误指标标记
            labels.addProperty("metrics_flag","1");
            labels.addProperty("metrics",errMetrics.getCode());
        }
        ReqSlowMetrics slowMetrics = ReqSlowMetrics.getSlowMetricsByMetric(rule.getAlert());
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
            jsonObject.addProperty("For", rule.getForTime());
        }

        /**
         * annotations
         */
        StringBuilder title = new StringBuilder().append(app.getProjectName());
        AlarmPresetMetrics metrics = AlarmPresetMetrics.getByCode(rule.getAlert());
        if (metrics != null) {
            title.append("&").append(metrics.getMessage());
        } else {
            //check tesla metrics
            TeslaMetricGroup.TeslaMetrics metricByCode = TeslaMetricGroup.TeslaMetrics.getMetricByCode(rule.getAlert());
            if(metricByCode != null){
                title.append("&").append(metricByCode.getMessage());
            }else{
                title.append("&").append(rule.getAlert());
            }

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
        if(rule.getAlert().endsWith("_intranet_tesla_availability") || rule.getAlert().endsWith("_staging_tesla_availability")){

            if(rule.getAlert().startsWith("mihome_")){
                //米家社区兼容
                labels.addProperty("detailRedirectUrl",teslaAlertIntranetGrafanaUrl);
                labels.addProperty("paramType","normal");
                JsonObject json = new JsonObject();
                json.addProperty("from","${alarmTime}-5min");
                json.addProperty("to","${alarmTime}+5min");
                json.addProperty("var-url","${url}");
                json.addProperty("var-group","${group}");
//                json.addProperty("var-Node","${ip}");
                jsonSummary.addProperty("paramMapping",json.toString());
            }else{
                log.info("rule.getAlert() ===== {},teslaAlertIntranetUrl:{}",rule.getAlert(),teslaAlertIntranetUrl);
                labels.addProperty("detailRedirectUrl",teslaAlertIntranetUrl);
                labels.addProperty("paramType","tesla");
            }

        }

        if(rule.getAlert().endsWith("_outnet_tesla_availability")){

            if(rule.getAlert().startsWith("mihome_")){
                //米家社区兼容
                labels.addProperty("detailRedirectUrl",teslaAlertOutnetGrafanaUrl);
                labels.addProperty("paramType","normal");
                JsonObject json = new JsonObject();
                json.addProperty("from","${alarmTime}-5min");
                json.addProperty("to","${alarmTime}+5min");
                json.addProperty("var-url","${url}");
                json.addProperty("var-group","${group}");
//                json.addProperty("var-Node","${ip}");
                jsonSummary.addProperty("paramMapping",json.toString());
            }else{
                log.info("rule.getAlert() ===== {},teslaAlertOutnetUrl:{}",rule.getAlert(),teslaAlertOutnetUrl);
                labels.addProperty("detailRedirectUrl",teslaAlertOutnetUrl);
                labels.addProperty("paramType","tesla");
            }

        }

        if(rule.getAlert().endsWith("_tesla_p99_time_cost")){
            log.info("rule.getAlert() ===== {},teslaAlertTimeCostnetUrl:{}",rule.getAlert(),teslaAlertTimeCostnetUrl);
            labels.addProperty("detailRedirectUrl",teslaAlertTimeCostnetUrl);
            labels.addProperty("paramType","tesla");
        }

        labels.addProperty("exceptViewLables","detailRedirectUrl.paramType");

        if(StringUtils.isNotBlank(ruleData.getAlarmDetailUrl())){
            labels.addProperty("detailRedirectUrl",ruleData.getAlarmDetailUrl());
            labels.addProperty("paramType","customerPromql");
        }

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
        if (rule.getValue() != null) {
            labels.addProperty("alert_value",rule.getValue().toString());
        }

        if (metrics != null) {
            labels.addProperty("calert",metrics.getMessage());
            labels.addProperty("group_key",metrics.getGroupKey().getCode());
        } else {
            labels.addProperty("calert",rule.getAlert());
        }

        ReqErrorMetrics errMetrics = ReqErrorMetrics.getErrorMetricsByMetrics(rule.getAlert());
        if (errMetrics != null) {
            //错误指标标记
            labels.addProperty("metrics_flag","1");
            labels.addProperty("metrics",errMetrics.getCode());
        }
        ReqSlowMetrics slowMetrics = ReqSlowMetrics.getSlowMetricsByMetric(rule.getAlert());
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

        if(!CollectionUtils.isEmpty(alertMembers)){
            JsonArray array = new Gson().fromJson(JSON.toJSONString(alertMembers), JsonArray.class);
            jsonObject.add("alert_member", array);
        }

        return alertServiceAdapt.editRule(rule.getAlarmId(),jsonObject,String.valueOf(rule.getIamId()),user);
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
