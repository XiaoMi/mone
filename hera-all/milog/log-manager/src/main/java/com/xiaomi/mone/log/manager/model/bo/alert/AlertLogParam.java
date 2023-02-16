package com.xiaomi.mone.log.manager.model.bo.alert;

import lombok.Data;

@Data
public class AlertLogParam {

    private String accessKey;
    private String secretKey;
    private String topicName;
    private String mqServer;
    private String logBody;
    private int numOfLogs;
}
