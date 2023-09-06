/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
 * @description MQ consumption IP changes
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
            log.error("Subscription IP address changed Mq consumption abnormality", e);
        }
        consumer.registerMessageListener((MessageListenerOrderly) (list, consumeOrderlyContext) -> {
            list.stream().forEach(ele -> {
                ipChangeConsumeMessage(ele);
            });
            return ConsumeOrderlyStatus.SUCCESS;
        });

        try {
            consumer.start();
        } catch (Exception e) {
            log.error("The Rocket Mq client starts unexpectedly when the subscription creates a project", e);
        }
    }

    private void ipChangeConsumeMessage(MessageExt message) {
        try {
            byte[] body = message.getBody();
            HeraEnvIpVo heraEnvIpVo = new Gson().fromJson(new String(body), HeraEnvIpVo.class);
            log.info("【Dynamic expansion】The message data consumed by Rocket Mq is converted into objects: {}", heraEnvIpVo.toString());
            logTailService.machineIpChange(heraEnvIpVo);
            log.info("【Dynamic Expansion】The news consumption of Rocket Mq consumption ends");
        } catch (Throwable ex) {
            log.error("【Dynamic Scaling】Rocket Mq consumption of messages is abnormal:" + ex.getMessage(), ex);
        }
    }

}
