package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class HdfsDetailDto implements Serializable {

    String traceId;
    String other;
    String mqtag;
    String level;
    String appName;
    String tail;
    String className;
    String message;
    String logsource;
    String threadName;
    String logip;
    String mqtopic;
    String logstore;
    String timestamp;

}
