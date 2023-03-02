package com.xiaomi.mone.log.stream.compensate;

import com.google.common.collect.Lists;
import com.xiaomi.mone.log.common.Config;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.stream.exception.StreamException;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/15 15:56
 */
@Slf4j
public class RocketMqMessageProduct implements MqMessageProduct {

    @Override
    public void product(String ak, String sk, String serviceUrl, String topic, List<String> msgs) {
        String producerGroup = Config.ins().get("rocketmq_group", "hear_log_stream");
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup, true);
        producer.setNamesrvAddr(serviceUrl);
        try {
            producer.start();
        } catch (MQClientException e) {
            log.error("RocketMqMessageProduct.initMqProducer error, RocketmqConfig: {},nameSrvAddr:{}", producerGroup, serviceUrl, e);
            throw new StreamException("initMqProducer exception", e);
        }
        List<Message> messageList = msgs.stream().map(msg -> {
            Message message = new Message();
            message.setTopic(topic);
            message.setBody(msg.getBytes(StandardCharsets.UTF_8));
            return message;
        }).collect(Collectors.toList());

        try {
            producer.send(messageList);
        } catch (Exception e) {
            log.error("RocketMqMessageProduct send message error, RocketmqConfig: " +
                    "{},nameSrvAddr:{}", producerGroup, serviceUrl, e);
        }
    }

    @Override
    public void product(MqMessageDTO msg) {
        String ak = Config.ins().get("rocketmq_ak", "");
        String sk = Config.ins().get("rocketmq_sk", "");
        String serviceUrl = Config.ins().get("rocketmq_service_url", "");
        String topic = Config.ins().get("rocketmq_producer_topic", "");
        this.product(ak, sk, serviceUrl, topic, Lists.newArrayList(Constant.GSON.toJson(msg)));
        log.info("compensate send message succeed");

    }
}
