package com.xiaomi.mone.log.stream.plugin.mq.talos;

import lombok.Data;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/28 17:45
 */
@Data
public class TalosConfig {
    private String ak;
    private String sk;
    private String clusterInfo;
    private String consumerGroup;
    private String clientPrefix;
    //    private TalosConsumerConfig consumerConfig;
    private String topicName;
    private String tag;
    private boolean isCommonTag;
}
