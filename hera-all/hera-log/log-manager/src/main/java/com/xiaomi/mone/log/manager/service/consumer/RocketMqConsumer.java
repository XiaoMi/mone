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

import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.remoting.RPCHook;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.rocketmq.common.consumer.ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/3/4 14:36
 */
public class RocketMqConsumer {
    /**
     * init Mq consumer
     * @param ak
     * @param sk
     * @param consumerGroup
     * @param address
     * @return
     */
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

    /**
     * cult multi tags
     * @param consumeTag
     * @return
     */
    public String getSplitTagString(String consumeTag) {
        List<String> projectTags = Arrays.stream(consumeTag.split(",")).collect(Collectors.toList());
        return projectTags.stream().collect(Collectors.joining("||"));
    }

}
