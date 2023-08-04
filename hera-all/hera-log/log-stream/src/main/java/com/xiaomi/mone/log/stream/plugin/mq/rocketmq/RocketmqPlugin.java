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
package com.xiaomi.mone.log.stream.plugin.mq.rocketmq;

import com.xiaomi.mone.log.common.Config;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.stream.common.SinkJobEnum;
import com.xiaomi.mone.log.stream.plugin.mq.MQPlugin;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.remoting.RPCHook;

import java.util.Optional;

import static org.apache.rocketmq.common.consumer.ConsumeFromWhere.*;

@Slf4j
@Service
public class RocketmqPlugin implements MQPlugin {

    public static RocketmqConfig buildRocketmqConfig(String ak, String sk, String clusterInfo,
                                                     String topic, String tag, SinkJobEnum jobType) {
        RocketmqConfig config = new RocketmqConfig();
        Config ins = Config.ins();
        config.setNamesrvAddr(StringUtils.isNotEmpty(clusterInfo) ? clusterInfo : ins.get("rocketmq_namesrv_addr", ""));
        config.setAk(StringUtils.isNotEmpty(ak) ? ak : ins.get("rocketmq_ak", ""));
        config.setSk(StringUtils.isNotEmpty(sk) ? sk : ins.get("rocketmq_sk", ""));
        config.setConsumerFromWhere(ins.get("rockermq_consumer_offset", ""));
        config.setConsumerGroup(Constant.DEFAULT_CONSUMER_GROUP + tag);
        if (SinkJobEnum.BACKUP_JOB == jobType) {
            config.setConsumerGroup(Constant.DEFAULT_CONSUMER_GROUP + tag + "_" + BACKUP_PREFIX);
        }
        config.setTopicName(topic);
        config.setTag(tag);
        log.info("[RmqSinkJob.initJob] print consumer config:{}", config);
        return config;
    }

    public static DefaultMQPushConsumer getRocketMqConsumer(RocketmqConfig rocketmqConfig) {
        return RocketmqPlugin.initDefaultMQPushConsumer(rocketmqConfig);
    }

    public static DefaultMQPushConsumer initDefaultMQPushConsumer(RocketmqConfig config) {
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer(config.getConsumerGroup());
        if (!config.getAk().equals("") && !config.getSk().equals("")) {
            SessionCredentials credentials = new SessionCredentials(config.getAk(), config.getSk());
            RPCHook rpcHook = new AclClientRPCHook(credentials);
            defaultMQPushConsumer = new DefaultMQPushConsumer(config.getConsumerGroup(), rpcHook, new AllocateMessageQueueAveragely());
        } else {
            defaultMQPushConsumer = new DefaultMQPushConsumer(config.getConsumerGroup());
        }
        String server = config.getNamesrvAddr();
        defaultMQPushConsumer.setNamesrvAddr(config.getNamesrvAddr());

        defaultMQPushConsumer.setConsumeFromWhere(getConsumeFromWhere(config.getConsumerFromWhere()));
        return defaultMQPushConsumer;
    }

    private static ConsumeFromWhere getConsumeFromWhere(String offset) {
        if (Optional.ofNullable(offset).isPresent()) {
            switch (offset) {
                case "last_offset":
                    return CONSUME_FROM_LAST_OFFSET;
                case "first_offset":
                    return CONSUME_FROM_FIRST_OFFSET;
                case "timestamp":
                    return CONSUME_FROM_TIMESTAMP;
                default:
                    return CONSUME_FROM_LAST_OFFSET;

            }
        }

        return CONSUME_FROM_LAST_OFFSET;
    }
}