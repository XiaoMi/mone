package com.xiaomi.mone.log.manager.model.bo.alert;

import lombok.Data;

import java.util.List;

@Data
public class AlertBo {

    private long alertId;
    private String name;
    private String type;
    private String appId;
    private Long milogAppId;
    private String appName;
    private String logPath;
    private String flinkJobName;
    private String contacts;
    private String feishuGroups;
    private int status;
    private String creator;
    private long ctime;
    private String department;
    private Long tailId;

    private List<AlertRuleBo> alertRules;
}
