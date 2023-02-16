package com.xiaomi.mone.log.manager.model.cache;

import lombok.Data;

@Data
public class LogCellectProcessCache {

    private String tailId;

    private String tailName;

    private String path;

    private String ip;

    private String pattern;

    private Long appId;


    private String appName;

    // 日志文件行号
    private Long fileRowNumber;

    private Long pointer;

    private Long fileMaxPointer;

    // 收集进度
    private String collectPercentage;

    // 收集时间
    private Long collectTime;
}
