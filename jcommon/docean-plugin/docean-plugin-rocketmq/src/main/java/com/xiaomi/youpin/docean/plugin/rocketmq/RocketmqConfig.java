package com.xiaomi.youpin.docean.plugin.rocketmq;

import lombok.Data;

/**
 * @author 丁春秋
 */
@Data
public class RocketmqConfig {

    private String producerGroup;

    private String consumerGroup;

    private String consumerFromWhere;

    private String namesrvAddr;

    private String ak;

    private String sk;

}
