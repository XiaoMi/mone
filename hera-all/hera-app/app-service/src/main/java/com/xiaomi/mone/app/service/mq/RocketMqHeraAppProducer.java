package com.xiaomi.mone.app.service.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.mone.app.api.message.HeraAppInfoModifyMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gaoxihui
 * @date 2023/4/26 3:12 下午
 */
@Slf4j
//@Service("rocketMqHeraAppProducer")
public class RocketMqHeraAppProducer {

    @NacosValue("${rocket.mq.hera.app.modify.topic}")
    private String topic;

    @NacosValue("${rocket.mq.hera.app.modify.tag}")
    private String tag;

    @Autowired
    private DefaultMQProducer producer;

    public void pushHeraAppMsg(HeraAppInfoModifyMessage heraAppMessage) {

        Message msg = new Message(topic, tag, JSON.toJSONString(heraAppMessage).getBytes());
        try {
            producer.send(msg);
            log.info("pushHeraAppMsg send rocketmq message : {}", heraAppMessage.toString());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("pushHeraAppMsg error: " + e.getMessage(), e);
        }
    }
}
