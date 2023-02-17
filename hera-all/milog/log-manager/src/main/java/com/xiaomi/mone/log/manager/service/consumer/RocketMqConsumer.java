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
