package com.xiaomi.hera.trace.etl.config;

import com.xiaomi.hera.trace.etl.mq.rocketmq.ClientMessageQueue;
import com.xiaomi.hera.trace.etl.mq.rocketmq.RocketMqProducer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description RocketMQ producer config、start
 * @Author dingtao
 * @Date 2022/10/24 2:58 下午
 */
@Configuration
public class RocketMqProducerConfig {

    @Value("${mq.rocketmq.nameseraddr}")
    private String nameSrvAddr;

    @Value("${mq.rocketmq.es.topic}")
    private String esTopic;

    @Value("${mq.rocketmq.producer.group}")
    private String group;

    @Bean
    public ClientMessageQueue getClientMessageQueue() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(group);
        producer.setNamesrvAddr(nameSrvAddr);
        producer.start();
        RocketMqProducer rocketMqProducer = new RocketMqProducer(producer, esTopic);
        return new ClientMessageQueue(rocketMqProducer);
    }
}
