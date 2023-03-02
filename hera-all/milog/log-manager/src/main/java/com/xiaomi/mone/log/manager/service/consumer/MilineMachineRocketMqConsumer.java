package com.xiaomi.mone.log.manager.service.consumer;

import com.google.gson.Gson;
import com.xiaomi.mone.log.manager.model.dto.DockerScaleBo;
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
public class MilineMachineRocketMqConsumer extends RocketMqConsumer {

    @Value("$miline_rocketmq_consumer_topic")
    private String consumeTopic;

    @Value("$miline_rocketmq_consumer_tag")
    private String consumeTag;

    @Value("$miline_rocketmq_consumer_group")
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
        log.info("【miline machine change】consumer mq service init");
        String projectTag = getSplitTagString(consumeTag);
        DefaultMQPushConsumer consumer = initDefaultMQPushConsumer(ak, sk, consumerGroup, address);
        try {
            consumer.subscribe(consumeTopic, projectTag);
        } catch (MQClientException e) {
            log.error("【miline machine change】订阅RocketMq消费异常", e);
        }
        consumer.registerMessageListener((MessageListenerOrderly) (list, consumeOrderlyContext) -> {
            list.stream().forEach(ele -> {
                // 创建项目发送的mq消息
                milieMachineChange(ele, milineMachineChangeDTO -> {
                    logTailService.dockerScaleDynamic(milineMachineChangeDTO);
                });
            });
            return ConsumeOrderlyStatus.SUCCESS;
        });
        try {
            consumer.start();
        } catch (MQClientException e) {
            log.error("【miline machine change】RocketMq客户端启动异常", e);
        }
    }

    private void milieMachineChange(MessageExt message, Consumer<DockerScaleBo> changeDTOConsumer) {
        try {
            byte[] body = message.getBody();
            DockerScaleBo machineChangeDTO = new Gson().fromJson(new String(body), DockerScaleBo.class);
            log.info("【miline machine change】RocketMq消费的消息数据转化为对象: {}", machineChangeDTO.toString());
            changeDTOConsumer.accept(machineChangeDTO);
            log.info("【miline machine change】RocketMq消费的消息消费结束");
        } catch (Throwable ex) {
            log.error(String.format("【miline machine change】RocketMq消费的消息消费异常:%s", message), ex);
        }
    }

}
