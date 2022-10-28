//package com.xiaomi.youpin.gwdash.rocketmq;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.rocketmq.client.producer.DefaultMQProducer;
//import org.apache.rocketmq.common.message.Message;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class MqNotify {
//
//    @Autowired
//    @Qualifier("defaultMQProducer")
//    private DefaultMQProducer defaultMQProducer;
//
//    @Value("${rocketmq.topic}")
//    private String topic;
//
//    public void notify(String type, String s) {
//        Message msg = new Message(topic, type, s.getBytes());
//        try {
//            defaultMQProducer.send(msg);
//        } catch (Exception e) {
//            log.warn("mq send msg error:{}", e.getMessage());
//        }
//    }
//
//    public void notify(String type, String s, int shardingKey) {
//        Message msg = new Message(topic, type, s.getBytes());
//        try {
//            defaultMQProducer.send(msg);
//        } catch (Exception e) {
//            log.warn("mq send msg error:{}", e.getMessage());
//        }
//    }
//}
//
