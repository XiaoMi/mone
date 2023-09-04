package com.xiaomi.hera.trace.etl.mq.rocketmq;

import com.xiaomi.hera.trace.etl.mq.Producer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/10/19 10:55 上午
 */
@Slf4j
public class RocketMqProducer implements Producer<MessageExt> {

    private DefaultMQProducer producer;
    private String topic;

    public RocketMqProducer(DefaultMQProducer producer, String topic) {
        this.producer = producer;
        this.topic = topic;
    }

    @Override
    public void send(MessageExt message) {
        this.send(Collections.singletonList(message));
    }

    @Override
    public void send(List<MessageExt> messages) {
        List<Message> list = new ArrayList<>();
        for (MessageExt message : messages) {
            Message msg = new Message();
            msg.setBody(message.getBody());
            msg.setTopic(topic);
            list.add(msg);
        }
        try {
            producer.send(list);
        } catch (Throwable t) {
            log.error("rocketmq producer send error", t);
        }
    }

    public void send(List<MessageExt> messages, MessageQueue messageQueue){
        List<Message> list = new ArrayList<>();
        for (MessageExt message : messages) {
            Message msg = new Message();
            msg.setBody(message.getBody());
            msg.setTopic(topic);
            list.add(msg);
        }
        try {
            producer.send(list, messageQueue);
        } catch (Throwable t) {
            log.error("rocketmq producer send error", t);
        }
    }

    public List<MessageQueue> fetchMessageQueue(){
        try {
            return this.producer.fetchPublishMessageQueues(topic);
        } catch (MQClientException e) {
            log.error("fetch queue task error : ", e);
        }
        return new ArrayList<>();
    }
}
