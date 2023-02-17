package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

@Data
public class AgentLogProcessDTO {
    private String path;
    private Long fileRowNumber;
    private Long pointer;
    private Long fileMaxPointer;
    private String appName;
    private String collectPercentage;
    private Long collectTime;
//    private String envName;
}
