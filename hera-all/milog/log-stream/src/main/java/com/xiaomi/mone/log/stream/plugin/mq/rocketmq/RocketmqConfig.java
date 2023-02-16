package com.xiaomi.mone.log.stream.plugin.mq.rocketmq;

import com.xiaomi.mone.log.stream.plugin.mq.MQConfig;
import lombok.Data;

/**
 * RocketMq的一些配置
 */
@Data
public class RocketmqConfig extends MQConfig {

    private String consumerGroup;

    private String consumerFromWhere;

    private String namesrvAddr;

    private String ak;

    private String sk;
}
