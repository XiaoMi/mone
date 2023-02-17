package com.xiaomi.mone.log.manager.model.bo.alert;

import lombok.Data;

import java.util.List;

@Data
public class AlertParam {
    private long alertId;
    private String name;
    private String type;
    private Long milogAppId;
    private Long tailId;
    private String appId;
    private String appName;
    private String logPath;
    private String contacts;
    private String feishuGroups;
    private String flinkJobName;
    private String department;
    private String mqType;


    private int countLimit;
    private String filterRegex;
    private int windowSize;
    private int windowOffset;
    private String operation;

    private String consumerAccessKey;
    private String consumerSecretKey;
    private String consumerServer;
    private String consumerTopic;
    private String consumerGroup;
    private String consumerTag;

    private String content;

    private List<AlertRuleParam> rules;

}
