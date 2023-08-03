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
package com.xiaomi.mone.log.stream.job.extension.impl;

import com.xiaomi.mone.log.api.enums.MiddlewareEnum;
import com.xiaomi.mone.log.stream.exception.StreamException;
import com.xiaomi.mone.log.stream.job.LogDataTransfer;
import com.xiaomi.mone.log.stream.job.extension.SinkJob;
import com.xiaomi.mone.log.stream.plugin.mq.rocketmq.RocketmqConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.Map;

import static com.xiaomi.mone.log.utils.DateUtils.getTime;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/8/22 15:11
 */
@Slf4j
public class RocketMqSinkJob implements SinkJob {

    private final RocketmqConfig rocketmqConfig;

    private final DefaultMQPushConsumer consumer;

    private final LogDataTransfer handleMessage;

    public RocketMqSinkJob(RocketmqConfig rocketmqConfig, DefaultMQPushConsumer consumer, LogDataTransfer handleMessage) {
        this.rocketmqConfig = rocketmqConfig;
        this.consumer = consumer;
        this.handleMessage = handleMessage;
    }

    @Override
    public boolean start() throws Exception {
        String topicName = rocketmqConfig.getTopicName();
        String tag = rocketmqConfig.getTag();
        try {
            consumer.subscribe(topicName, tag);
            log.info("[RmqSinkJob.start] job subscribed topic [topic:{},tag:{}]", topicName, tag);
            consumer.registerMessageListener((MessageListenerOrderly) (list, consumeOrderlyContext) -> {
                String time = getTime();
                for (MessageExt messageExt : list) {
                    Map<String, Object> m = null;
                    String msg = new String(messageExt.getBody());
                    handleMessage.handleMessage(MiddlewareEnum.ROCKETMQ.getName(), msg, time);
                }
                return ConsumeOrderlyStatus.SUCCESS;
            });
            consumer.start();
            return true;
        } catch (MQClientException e) {
            log.error(String.format("[RmqSinkJob.start] logStream rockerMq start error,topic:%s,tag:%s", topicName, tag), e);
            throw new StreamException("[RmqSinkJob.start] job subscribed topic error,topic: " + topicName + " tag: " + tag + " err: ", e);
        }
    }

    @Override
    public void shutdown() throws Exception {
        consumer.shutdown();
        log.info("[RmqSinkJob.rocketmq shutdown] job consumer shutdown, topic:{},tag:{}", rocketmqConfig.getTopicName(), rocketmqConfig.getTag());
    }
}
