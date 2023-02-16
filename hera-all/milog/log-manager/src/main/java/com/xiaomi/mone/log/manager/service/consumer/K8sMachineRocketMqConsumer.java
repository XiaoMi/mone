package com.xiaomi.mone.log.manager.service.consumer;

import com.google.gson.Gson;
import com.xiaomi.mone.log.manager.model.dto.K8sMachineChangeDTO;
import com.xiaomi.mone.log.manager.service.impl.LogTailServiceImpl;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import javax.annotation.Resource;
import java.util.function.Consumer;

/**
 * @author wtt
 * @version 1.0
 * @description mq消费
 * @date 2021/7/14 20:15
 */
@Slf4j
@Service
@Deprecated
public class K8sMachineRocketMqConsumer extends RocketMqConsumer {

    @Value("$k8s_rocketmq_consumer_topic")
    private String consumeTopic;

    @Value("$k8s_rocketmq_consumer_tag")
    private String consumeTag;

    @Value("$k8s_rocketmq_consumer_group")
    private String consumerGroup;

    @Value("$rocketmq_ak")
    private String ak;

    @Value("$rocketmq_sk")
    private String sk;

    @Value("$rocketmq_namesrv_addr")
    private String address;

    @Resource
    private LogTailServiceImpl logTailService;

    public void init() {
        log.info("【k8s machine change】consumer mq service init");
        String projectTag = getSplitTagString(consumeTag);
        DefaultMQPushConsumer consumer = initDefaultMQPushConsumer(ak, sk, consumerGroup, address);
        try {
            consumer.subscribe(consumeTopic, projectTag);
        } catch (MQClientException e) {
            log.error("【k8s machine change】订阅RocketMq消费异常", e);
        }
        consumer.registerMessageListener((MessageListenerOrderly) (list, consumeOrderlyContext) -> {
            list.stream().forEach(ele -> {
                // 创建项目发送的mq消息
                k8sMachineChange(ele, k8sMachineChangeDTO -> {
                    logTailService.handleK8sTopicTail(k8sMachineChangeDTO);
                });
            });
            return ConsumeOrderlyStatus.SUCCESS;
        });
        try {
            consumer.start();
        } catch (MQClientException e) {
            log.error("【k8s machine change】RocketMq客户端启动异常", e);
        }
    }

    private void k8sMachineChange(MessageExt message, Consumer<K8sMachineChangeDTO> changeDTOConsumer) {
        try {
            byte[] body = message.getBody();
            K8sMachineChangeDTO machineChangeDTO = new Gson().fromJson(new String(body), K8sMachineChangeDTO.class);
            log.info("【k8s machine change】RocketMq消费的消息数据转化为对象: {}", machineChangeDTO.toString());
            changeDTOConsumer.accept(machineChangeDTO);
            log.info("【k8s machine change】RocketMq消费的消息消费结束");
        } catch (Throwable ex) {
            log.error(String.format("【k8s machine change】RocketMq消费的消息消费异常:%s", message), ex);
        }
    }

}
