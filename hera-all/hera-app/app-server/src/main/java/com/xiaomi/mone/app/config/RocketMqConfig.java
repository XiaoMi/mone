package com.xiaomi.mone.app.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.mone.app.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/11 19:42
 */
@Configuration
@Slf4j
public class RocketMqConfig {

    @Value("${rocket.mq.producer.group}")
    private String producerGroup;

    @NacosValue(value = "${rocket.mq.srvAddr}", autoRefreshed = true)
    private String nameSrvAddr;

    @Bean
    public DefaultMQProducer getMqProducer() {
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup, true);
        producer.setNamesrvAddr(nameSrvAddr);
        try {
            producer.start();
            return producer;
        } catch (MQClientException e) {
            log.error("ChannelBootstrap.initMqProducer error, RocketmqConfig: {},nameSrvAddr:{}", producerGroup, nameSrvAddr, e);
            throw new AppException("initMqProducer exception", e);
        }
    }
}
