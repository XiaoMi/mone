package com.xiaomi.mone.monitor.service.rocketmq;

import com.alibaba.fastjson.JSON;
import com.xiaomi.mone.monitor.service.model.AppMonitorModel;
import com.xiaomi.mone.monitor.service.rocketmq.model.HeraAppMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author gaoxihui
 * @date 2022/3/9 4:19 下午
 */
@Slf4j
@Service
public class RocketMqProducer {

    @Value("${rocketmq.topic.hera.app}")
    private String heraAppTopic;

    @Value("${rocketmq.tag.hera.app}")
    private String heraAppTag;

    @Value("${rocketmq.group.hera.app}")
    private String consumerGroup;

    @Autowired
    @Qualifier("defaultMQProducer")
    private DefaultMQProducer producer;

    public void pushHeraAppMsg(HeraAppMessage heraAppMessage) {

        Message msg = new Message(heraAppTopic, heraAppTag, JSON.toJSONString(heraAppMessage).getBytes());
        try {
            producer.send(msg);
            log.info("pushHeraAppMsg send rocketmq message : {}", heraAppMessage.toString());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("pushHeraAppMsg error: " + e.getMessage(), e);
        }
    }
}
