package com.xiaomi.youpin.docean.plugin.rocketmq;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.remoting.RPCHook;

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
        config.setAk(c.get("rocketmq_ak", ""));
        config.setSk(c.get("rocketmq_sk", ""));

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
        DefaultMQProducer producer = null;
        if (!config.getAk().equals("") && !config.getSk().equals("")) {
            SessionCredentials credentials = new SessionCredentials(config.getAk(), config.getSk());
            RPCHook rpcHook = new AclClientRPCHook(credentials);
            producer = new DefaultMQProducer(config.getProducerGroup(), rpcHook);
        } else {
            producer = new DefaultMQProducer(config.getProducerGroup());
        }
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
        if (!config.getAk().equals("") && !config.getSk().equals("")) {
            SessionCredentials credentials = new SessionCredentials(config.getAk(), config.getSk());
            RPCHook rpcHook = new AclClientRPCHook(credentials);
            defaultMQPushConsumer = new DefaultMQPushConsumer(config.getConsumerGroup(), rpcHook, new AllocateMessageQueueAveragely());

        } else {
            defaultMQPushConsumer = new DefaultMQPushConsumer(config.getConsumerGroup());
        }

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
