package com.xiaomi.mone.monitor.bo;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gaoxihui
 * @date 2021/12/29 4:53 下午
 */
public enum TeslaMetricGroup {

    china_staging_tesla("tesla","网关","staging",392,17465,"tesla",TeslaMetrics.getEnumList("china","staging")),

    ;

    private String projectKey;
    private String projectCName;
    private String evn;
    private Integer projectId;
    private Integer iamTreeId;
    private String name;
    private List<MetricsRule> rules;

    TeslaMetricGroup(String projectKey, String projectCName, String evn, Integer projectId, Integer iamTreeId, String name, List<MetricsRule> rules) {
        this.projectKey = projectKey;
        this.projectCName = projectCName;
        this.evn = evn;
        this.projectId = projectId;
        this.iamTreeId = iamTreeId;
        this.name = name;
        this.rules = rules;
    }


    public static List<TeslaMetricRule> getEnumList(String env){

        return Arrays.stream(TeslaMetricGroup.values())
                .filter(t -> t.getEvn().equals(env))
                .map(t -> new TeslaMetricRule(t.getProjectKey(),t.getProjectCName(), t.getProjectId(),t.getIamTreeId(),t.getName(),t.getRules()))
                .collect(Collectors.toList());
    }

    public String getProjectKey() {
        return projectKey;
    }

    public String getProjectCName() {
        return projectCName;
    }

    public String getEvn() {
        return evn;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public Integer getIamTreeId() {
        return iamTreeId;
    }

    public String getName() {
        return name;
    }

    public List<MetricsRule> getRules() {
        return rules;
    }

    public enum TeslaMetrics{

        availability("open","staging","open_tesla_availability","开源网关url请求可用率",new TeslaMetricInfo("open_tesla_errCode_total","open_tesla_TotalCounter_total","tesla-open-intranet"),MetricsUnit.UNIT_PERCENT,SendAlertGroupKey.TESLA_URl, AlarmStrategyType.TESLA),
        ;

        private String projectKey;
        private String env;
        private String code;
        private String message;
        private TeslaMetricInfo metric;
        private MetricsUnit unit;
        private SendAlertGroupKey groupKey;
        private AlarmStrategyType strategyType;

        TeslaMetrics(String projectKey, String env, String code, String message, TeslaMetricInfo metric, MetricsUnit unit, SendAlertGroupKey groupKey, AlarmStrategyType strategyType) {
            this.projectKey = projectKey;
            this.env = env;
            this.code = code;
            this.message = message;
            this.metric = metric;
            this.unit = unit;
            this.groupKey = groupKey;
            this.strategyType = strategyType;
        }

        public static List<MetricsRule> getEnumList(String projectKey, String env){

            return Arrays.stream(TeslaMetrics.values())
                    .filter(t -> t.projectKey.equals(projectKey) && t.env.equals(env))
                    .map(t -> new MetricsRule(t.getCode(),t.getMessage(), t.getUnit().getCode(), t.strategyType.getCode(),"",false))
                    .collect(Collectors.toList());
        }

        public static TeslaMetricInfo getMetricInfoByCode(String code){

            if(StringUtils.isBlank(code)){
                return null;
            }

            TeslaMetrics[] values = TeslaMetrics.values();
            for(TeslaMetrics value : values){
                if(value.getCode().equals(code)){
                    return value.getMetric();
                }
            }

            return null;
        }

        public static TeslaMetrics getMetricByCode(String code){

            if(StringUtils.isBlank(code)){
                return null;
            }

            TeslaMetrics[] values = TeslaMetrics.values();
            for(TeslaMetrics value : values){
                if(value.getCode().equals(code)){
                    return value;
                }
            }

            return null;
        }

        public String getProjectKey() {
            return projectKey;
        }

        public String getEnv() {
            return env;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
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

        public TeslaMetricInfo getMetric() {
            return metric;
        }
    }

}
