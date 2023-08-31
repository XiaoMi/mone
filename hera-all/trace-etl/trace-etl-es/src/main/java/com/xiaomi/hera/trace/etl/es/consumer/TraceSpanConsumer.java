package com.xiaomi.hera.trace.etl.es.consumer;

import com.xiaomi.hera.trace.etl.es.util.pool.ConsumerPool;
import com.xiaomi.hera.trace.etl.util.ThriftUtil;
import com.xiaomi.hera.tspandata.TSpanData;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.thrift.TDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author dingtao
 * @Date 2021/11/5 10:05 上午
 */
@Component
public class TraceSpanConsumer {

    private static final Logger log = LoggerFactory.getLogger(TraceSpanConsumer.class);

    @Value("${mq.rocketmq.group}")
    private String group;

    @Value("${mq.rocketmq.nameseraddr}")
    private String nameSerAddr;

    @Value("${mq.rocketmq.es.topic}")
    private String topicName;

    @Autowired
    private ConsumerService consumerService;

    @PostConstruct
    public void takeMessage() throws MQClientException {
        // 初始化rocketmq consumer
        log.info("init consumer start ...");
        DefaultMQPushConsumer consumer=new DefaultMQPushConsumer(group);
        consumer.setNamesrvAddr(nameSerAddr);
        consumer.subscribe(topicName,"*");
        consumer.registerMessageListener(new TraceEtlMessageListener());
        consumer.start();
        log.info("init consumer end ...");
    }

    private class TraceEtlMessageListener implements MessageListenerConcurrently {

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
            if(list == null || list.isEmpty()){
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            try {
                for(MessageExt message : list) {
                    ConsumerPool.CONSUMER_POOL.submit(new ConsumerRunner(message.getBody()));
                    await();
                }
            } catch (Throwable t) {
                log.error("consumer message error", t);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }

    private void await() {
        while (true) {
            try {
                if (ConsumerPool.CONSUMER_QUEUE.remainingCapacity() > ConsumerPool.CONSUMER_QUEUE_THRESHOLD) {
                    return;
                }
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (Throwable t) {
                log.error("await error : ", t);
            }
        }
    }

    private class ConsumerRunner implements Runnable {
        private byte[] message;

        public ConsumerRunner(byte[] message) {
            this.message = message;
        }

        @Override
        public void run() {
            try {
                TSpanData tSpanData = new TSpanData();
                new TDeserializer(ThriftUtil.PROTOCOL_FACTORY).deserialize(tSpanData, message);
                consumerService.consumer(tSpanData);
            } catch (Throwable t) {
                log.error("consumer error : ", t);
            }
        }
    }
}
