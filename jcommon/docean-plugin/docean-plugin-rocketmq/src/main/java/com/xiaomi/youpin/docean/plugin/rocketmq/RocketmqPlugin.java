/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.docean.plugin.rocketmq;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;

import java.util.Optional;
import java.util.Set;

import static org.apache.rocketmq.common.consumer.ConsumeFromWhere.*;

/**
 * @author 丁嬷嬷
 */
@DOceanPlugin
@Slf4j
public class RocketmqPlugin implements IPlugin {

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("init RocketmqPlugin");
        RocketmqConfig config = new RocketmqConfig();
        Config c = ioc.getBean(Config.class);
        config.setNamesrvAddr(c.get("rocketmq_namesrv_addr", ""));
        config.setProducerGroup(c.get("rocketmq_producer_group", ""));
        config.setConsumerGroup(c.get("rocketmq_consumer_group", ""));
        config.setConsumerFromWhere(c.get("rockermq_consumer_offset", ""));

        if (Boolean.TRUE.toString().equals(c.get("rocketmq_producer_on", ""))) {
            DefaultMQProducer producer = initDefaultMQProducer(config);
            ioc.putBean(producer);
        }

        if (Boolean.TRUE.toString().equals(c.get("rocketmq_consumer_on", ""))) {
            DefaultMQPushConsumer consumer = initDefaultMQPushConsumer(config);
            ioc.putBean(consumer);
        }
    }

    private DefaultMQProducer initDefaultMQProducer(RocketmqConfig config) {
        DefaultMQProducer producer = new DefaultMQProducer(config.getProducerGroup());
        producer.setNamesrvAddr(config.getNamesrvAddr());
        try {
            producer.start();
            return producer;
        } catch (MQClientException e) {
            log.error("RocketmqPlugin.initDefaultMQProducer error, RocketmqConfig: {}", config, e);
        }

        return producer;
    }

    private DefaultMQPushConsumer initDefaultMQPushConsumer(RocketmqConfig config) {
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer(config.getConsumerGroup());
        defaultMQPushConsumer.setNamesrvAddr(config.getNamesrvAddr());
        defaultMQPushConsumer.setConsumeFromWhere(getConsumeFromWhere(config.getConsumerFromWhere()));

        return defaultMQPushConsumer;
    }

    private ConsumeFromWhere getConsumeFromWhere(String offset) {
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
