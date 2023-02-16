package com.xiaomi.mone.monitor.bo;

import lombok.Data;

import java.util.List;

/**
 * @author gaoxihui
 * @date 2021/12/30 3:23 下午
 */
@Data
public class TeslaMetricRule {

    private String projectKey;
    private String projectCName;
    private Integer projectId;
    private Integer iamTreeId;
    private String projectName;
    private List<MetricsRule> rules;

    public TeslaMetricRule(String projectKey, String projectCName, List<MetricsRule> rules) {
        this.projectKey = projectKey;
        this.projectCName = projectCName;
        this.rules = rules;
    }

    public TeslaMetricRule(String projectKey, String projectCName, Integer projectId, Integer iamTreeId, String projectName, List<MetricsRule> rules) {
        this.projectKey = projectKey;
        this.projectCName = projectCName;
        this.projectId = projectId;
        this.iamTreeId = iamTreeId;
        this.projectName = projectName;
        this.rules = rules;
    }
}
