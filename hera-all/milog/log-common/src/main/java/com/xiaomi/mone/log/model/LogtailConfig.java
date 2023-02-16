package com.xiaomi.mone.log.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 对应logtail 配置，一个logtail 对应一个rmq topic
 */
@Data
@EqualsAndHashCode
public class LogtailConfig {
    private Long logtailId;

    private String ak;
    private String sk;
    private String clusterInfo;
    private String consumerGroup;
    private String topic;
    private String tag;
    private String type;
    private Integer appType;

    private Integer parseType;
    private String tail;
    /**
     * 日志分隔符
     */
    private String parseScript;
    private String valueList;

}
