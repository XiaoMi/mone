package com.xiaomi.mone.log.api.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AgentLogProcessDTO implements Serializable {
    private String path;
    private Long fileRowNumber;
    private Long pointer;
    private Long fileMaxPointer;
    private String appName;
    private String collectPercentage;
    private Long collectTime;
//    private String envName;
}
