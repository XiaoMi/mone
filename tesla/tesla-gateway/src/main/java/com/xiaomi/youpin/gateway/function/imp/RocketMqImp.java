package com.xiaomi.youpin.gateway.function.imp;

import com.xiaomi.youpin.gateway.rocketmq.RocketMq;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RocketMqImp implements RocketMq {

    @Autowired
    private DefaultMQProducer defaultMQProducer;

    @Override
    public String send(String topic, String data) {
        try {
            defaultMQProducer.send(new Message(
                    topic,
                    "",
                    data.getBytes()
            ));
            return "ok";
        } catch (Exception e) {
            log.error("RocketMqImp.send error...", e);
            return "error";
        }
    }
}
