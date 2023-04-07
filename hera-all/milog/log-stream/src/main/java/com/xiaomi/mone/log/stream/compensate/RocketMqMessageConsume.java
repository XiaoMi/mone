package com.xiaomi.mone.log.stream.compensate;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.mone.es.EsProcessor;
import com.xiaomi.mone.log.common.Config;
import com.xiaomi.mone.log.parse.LogParser;
import com.xiaomi.mone.log.stream.plugin.es.EsPlugin;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.RPCHook;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

import static com.xiaomi.mone.log.common.Constant.GSON;
import static org.apache.rocketmq.common.consumer.ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/15 15:56
 */
@Slf4j
public class RocketMqMessageConsume implements MqMessageConsume {


    @Override
    public void consume(String ak, String sk, String serviceUrl, String topic) {
        log.info("【RocketMqMessageConsume】consumer mq service init");
        String mqGroup = Config.ins().get("rocketmq_group", "hear_log_stream");

        DefaultMQPushConsumer consumer = initDefaultMQPushConsumer(ak, sk, mqGroup, serviceUrl);
        try {
            consumer.subscribe(topic, "");
        } catch (MQClientException e) {
            log.error("【RocketMqMessageConsume】订阅RocketMq消费异常", e);
        }
        consumer.registerMessageListener((MessageListenerOrderly) (list, consumeOrderlyContext) -> {
            list.stream().forEach(ele -> {
                byte[] body = ele.getBody();
                String str = new String(body);
                log.info("RocketMqMessageConsume.consume:{}", str);
                MqMessageDTO mqMessageDTO = GSON.fromJson(str, MqMessageDTO.class);
                sendMessageReply(mqMessageDTO);
            });
            return ConsumeOrderlyStatus.SUCCESS;
        });
        try {
            consumer.start();
        } catch (MQClientException e) {
            log.error("【RocketMqMessageConsume】RocketMq客户端启动异常", e);
        }
    }

    public DefaultMQPushConsumer initDefaultMQPushConsumer(String ak, String sk, String consumerGroup, String address) {
        DefaultMQPushConsumer defaultMQPushConsumer;
        if (!ak.equals("") && !sk.equals("")) {
            SessionCredentials credentials = new SessionCredentials(ak, sk);
            RPCHook rpcHook = new AclClientRPCHook(credentials);
            defaultMQPushConsumer = new DefaultMQPushConsumer(consumerGroup, rpcHook, new AllocateMessageQueueAveragely());
        } else {
            defaultMQPushConsumer = new DefaultMQPushConsumer(consumerGroup);
        }
        defaultMQPushConsumer.setNamesrvAddr(address);
        defaultMQPushConsumer.setConsumeFromWhere(CONSUME_FROM_LAST_OFFSET);
        return defaultMQPushConsumer;
    }

    @Override
    public void consume() {
        String ak = Config.ins().get("rocketmq_ak", "");
        String sk = Config.ins().get("rocketmq_sk", "");
        String serviceUrl = Config.ins().get("rocketmq_service_url", "");
        String topic = Config.ins().get("rocketmq_producer_topic", "");
        this.consume(ak, sk, serviceUrl, topic);
        log.info("compensate consume  message succeed");
    }

    private void sendMessageReply(MqMessageDTO mqMessageDTO) {
        log.info("Compensate Message content: " + GSON.toJson(mqMessageDTO));
        //直接写入es no handle
        List<MqMessageDTO.CompensateMqDTO> compensateMqDTOS = mqMessageDTO.getCompensateMqDTOS();
        if (CollectionUtils.isNotEmpty(compensateMqDTOS)) {
            compensateMqDTOS.forEach(compensateMqDTO -> {
                String esIndex = compensateMqDTO.getEsIndex();
                String message = compensateMqDTO.getMsg();
                LinkedTreeMap hashMap = GSON.fromJson(message, new TypeToken<LinkedTreeMap<String, Object>>() {
                }.getType());
                Object timestampObject = hashMap.get(LogParser.esKeyMap_timestamp);
                try {
                    long timeStamp = Long.parseLong(String.valueOf(timestampObject));
                    if (String.valueOf(timeStamp).length() != LogParser.TIME_STAMP_MILLI_LENGTH) {
                        hashMap.put(LogParser.esKeyMap_timestamp, Instant.now().toEpochMilli());
                    }
                } catch (Exception e) {
                    hashMap.put(LogParser.esKeyMap_timestamp, Instant.now().toEpochMilli());
                }
                log.info("mq索引时间戳数据：{},当前时间戳：{}", timestampObject, Instant.now().toEpochMilli());
                EsProcessor esProcessor = EsPlugin.getEsProcessor(mqMessageDTO.getEsInfo(),
                        mqMessageDTO1 -> log.error("compensate msg store failed, data size:{}", mqMessageDTO1.getCompensateMqDTOS().size()));
                esProcessor.bulkInsert(esIndex, hashMap);
            });
        }
    }
}
