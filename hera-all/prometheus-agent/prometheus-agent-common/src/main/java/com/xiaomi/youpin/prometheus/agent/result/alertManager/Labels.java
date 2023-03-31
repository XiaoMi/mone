package com.xiaomi.youpin.prometheus.agent.result.alertManager;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Labels {
    private String alert_key;
    private String alert_op;
    private String alert_value;
    private String alertname;
    private String app_iam_id;
    private String application;
    private String calert;
    private String exceptViewLables;
    private String group_key;
    private String methodName;
    private String metrics;
    private String metrics_flag;
    private String project_id;
    private String project_name;
    private String send_interval;
    private String serverEnv;
    private String serverIp;
    private String serviceName;
    private String system;
    private String container;
    private String image;
    private String instance;
    private String ip;
    private String job;
    private String name;
    private String namespace;
    private String pod;
    private String restartCounts;

}
