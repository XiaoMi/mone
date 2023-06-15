package com.xiaomi.mone.test;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.mone.app.AppBootstrap;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.RPCHook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author wtt
 * @version 1.0
 * @description rocketmq 收发消息测试
 * @date 2023/6/14 16:36
 */
@Slf4j
@SpringBootTest(classes = AppBootstrap.class)
public class RocketMqTest {

    @Autowired
    private DefaultMQProducer defaultMQProducer;

    @NacosValue(value = "${rocket.mq.srvAddr}", autoRefreshed = true)
    private String namesrvAddr;

    @NacosValue(value = "${rocketmq.ak}", autoRefreshed = true)
    private String ak;

    @NacosValue(value = "${rocketmq.sk}", autoRefreshed = true)
    private String sk;

    private DefaultMQPushConsumer defaultMQPushConsumer;

    private String topic = "wtt-test-topic";

    @Test
    public void testSendMessageToMq() {

        String message = "test";
        List<Message> messageList = IntStream.range(0, 10)
                .boxed()
                .map(value -> new Message(topic, String.format("%s-%s", message, value).getBytes()))
                .collect(Collectors.toList());
        try {
            defaultMQProducer.send(messageList);
            log.info("send message success");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("send message error ", e);
        }
    }

    @Test
    public void testConsumeMessageToMq() throws MQClientException, IOException {
        String consumerTag = "*";
        String consumerGroup = "wtt-test-group";

        if (StringUtils.isNotEmpty(ak)
                && StringUtils.isNotEmpty(sk)) {
            SessionCredentials credentials = new SessionCredentials(ak, sk);
            RPCHook rpcHook = new AclClientRPCHook(credentials);
            defaultMQPushConsumer = new DefaultMQPushConsumer(consumerGroup, rpcHook, new AllocateMessageQueueAveragely());
        } else {
            defaultMQPushConsumer = new DefaultMQPushConsumer(consumerGroup);
        }
        defaultMQPushConsumer.setNamesrvAddr(namesrvAddr);
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);

        defaultMQPushConsumer.subscribe(topic, consumerTag);
        defaultMQPushConsumer.registerMessageListener((MessageListenerOrderly) (list, consumeOrderlyContext) -> {
            try {
                list.stream().forEach(message -> {
                    try {
                        log.info("consumer message : {}", new String(message.getBody()));
                    } catch (Throwable ex) {
                        log.error("consumer message handle error", ex);
                    }
                });
            } catch (Exception e) {
                log.info("consumer message error", e);
            }
            return ConsumeOrderlyStatus.SUCCESS;
        });
        defaultMQPushConsumer.start();
        System.in.read();
    }
}
