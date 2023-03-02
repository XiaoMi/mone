package com.xiaomi.mone.log.manager.service.consumer;

import com.google.gson.Gson;
import com.xiaomi.mone.app.model.vo.HeraEnvIpVo;
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

/**
 * @author wtt
 * @version 1.0
 * @description mq消费 ip变更
 * @date 2021/7/14 20:15
 */
@Slf4j
@Service
public class MioneRocketMqConsumer extends RocketMqConsumer {

    @Value("$rocketmq_consumer_topic")
    private String consumeTopic;

    @Resource
    private DefaultMQPushConsumer consumer;

    @Resource
    private LogTailServiceImpl logTailService;

    public void init() {
        log.info("consumer mq service init");
        try {
            consumer.subscribe(consumeTopic, "");
        } catch (MQClientException e) {
            log.error("订阅ip变更Mq消费异常", e);
        }
        consumer.registerMessageListener((MessageListenerOrderly) (list, consumeOrderlyContext) -> {
            list.stream().forEach(ele -> {
                ipChangeConsumeMessage(ele);
            });
            return ConsumeOrderlyStatus.SUCCESS;
        });

        try {
            consumer.start();
        } catch (MQClientException e) {
            log.error("订阅创建项目时的RocketMq客户端启动异常", e);
        }
    }

    private void ipChangeConsumeMessage(MessageExt message) {
        try {
            byte[] body = message.getBody();
            HeraEnvIpVo heraEnvIpVo = new Gson().fromJson(new String(body), HeraEnvIpVo.class);
            log.info("【动态扩缩容】RocketMq消费的消息数据转化为对象: {}", heraEnvIpVo.toString());
            logTailService.machineIpChange(heraEnvIpVo);
            log.info("【动态扩缩容】RocketMq消费的消息消费结束");
        } catch (Throwable ex) {
            log.error("【动态扩缩容】RocketMq消费的消息消费异常:" + ex.getMessage(), ex);
        }
    }

}
