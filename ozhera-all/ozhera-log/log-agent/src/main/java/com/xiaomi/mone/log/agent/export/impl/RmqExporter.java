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
package com.xiaomi.mone.log.agent.export.impl;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.mone.log.agent.common.trace.TraceUtil;
import com.xiaomi.mone.log.agent.export.MsgExporter;
import com.xiaomi.mone.log.api.enums.LogTypeEnum;
import com.xiaomi.mone.log.api.model.msg.LineMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Rocketmq 消息发送实现类
 *
 * @author shanwb
 * @date 2021-07-19
 */
@Slf4j
public class RmqExporter implements MsgExporter {

    private DefaultMQProducer mqProducer;

    private String rmqTopic;

    private Integer batchSize;

    private Gson gson = new Gson();

    private final static String OPENTELEMETRY_TYPE = String.valueOf(
            LogTypeEnum.OPENTELEMETRY.getType());

    public RmqExporter(DefaultMQProducer mqProducer) {
        this.mqProducer = mqProducer;
    }

    @Override
    public void export(LineMessage message) {
        this.export(Lists.newArrayList(message));
    }

    @Override
    public void export(List<LineMessage> messageList) {
        if (messageList.isEmpty()) {
            return;
        }
        List<Message> messages = messageList.stream()
                .map(m -> {
                    Message message;
                    if (OPENTELEMETRY_TYPE.equals(m.getProperties(LineMessage.KEY_MESSAGE_TYPE))) {
                        byte[] bytes = TraceUtil.toBytes(m.getMsgBody());
                        if (bytes != null) {
                            message = new Message();
                            message.setBody(bytes);
                        } else {
                            return null;
                        }
                    } else {
                        message = new Message();
                        message.setTags(m.getProperties(LineMessage.KEY_MQ_TOPIC_TAG));
                        message.setBody(gson.toJson(m).getBytes(StandardCharsets.UTF_8));
                    }
                    message.setTopic(this.rmqTopic);
                    return message;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        try {
            mqProducer.send(messages);
        } catch (MQClientException e) {
            log.error("rocketmq export MQClientException:{}", e);
        } catch (RemotingException e) {
            log.error("rocketmq export RemotingException:{}", e);
        } catch (MQBrokerException e) {
            log.error("rocketmq export MQBrokerException:{}", e);
        } catch (InterruptedException e) {
            log.error("rocketmq export InterruptedException:{}", e);
        }
    }

    public String getRmqTopic() {
        return rmqTopic;
    }

    public void setRmqTopic(String rmqTopic) {
        this.rmqTopic = rmqTopic;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public int batchExportSize() {
        if (null == batchSize || batchSize < 0) {
            return BATCH_EXPORT_SIZE;
        }

        return batchSize;
    }


    @Override
    public void close() {
        //mqProducer多topic公用，不能shutdown();
    }

}
