package com.xiaomi.mone.log.manager.model.bo.alert;

import lombok.Data;

@Data
public class AlertLogBo {

    private long alertId;
    private String app;
    private String appName;
    private String ip;
    private String logPath;
    private String alertContent;
    private boolean activeAlarm;
    private long startTime;
    private long endTime;
    private long alertDuration;
    private int alertCount;
    private String alertLevel;
    private int windowSize;
    private String traceLink;

}
