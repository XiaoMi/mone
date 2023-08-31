package com.xiaomi.hera.trace.etl.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.hera.trace.etl.mq.rocketmq.ClientMessageQueue;
import com.xiaomi.hera.trace.etl.mq.rocketmq.RocketMqProducer;
import com.xiaomi.hera.trace.etl.util.ThriftUtil;
import com.xiaomi.hera.tspandata.TSpanData;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.thrift.TDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author dingtao
 * @Description
 * @date 2021/9/29 2:47 下午
 */
@Service
@Slf4j
public class ConsumerService {

    @Value("${mq.rocketmq.consumer.group}")
    private String group;

    @NacosValue("${mq.rocketmq.nameseraddr}")
    private String nameSerAddr;

    @Value("${mq.rocketmq.server.topic}")
    private String topicName;

    @Autowired
    private MetricsParseService metricsExporterService;

    @Autowired
    private ClientMessageQueue clientMessageQueue;

    @Autowired
    private DataCacheService cacheService;

    @Autowired
    private EnterManager enterManager;

    @PostConstruct
    public void takeMessage() throws MQClientException {
        // Before initializing rocketmq consumer,
        // initialize the local message queue to
        // ensure that the local message queue is available when messages come in
        clientMessageQueue.initFetchQueueTask();
        // initializing rocketmq consumer
        log.info("init consumer start ...");
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(group);
        consumer.setNamesrvAddr(nameSerAddr);
        consumer.subscribe(topicName, "*");
        consumer.registerMessageListener(new TraceEtlMessageListener());
        consumer.start();
        log.info("init consumer end ...");
    }

    private class TraceEtlMessageListener implements MessageListenerOrderly {

        private AtomicInteger i = new AtomicInteger();

        private AtomicLong time = new AtomicLong(System.currentTimeMillis());


        @Override
        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, final ConsumeOrderlyContext context) {
            if (list == null || list.isEmpty()) {
                return ConsumeOrderlyStatus.SUCCESS;
            }
            enterManager.enter();
            enterManager.getProcessNum().incrementAndGet();
            try {
                for (MessageExt message : list) {
                    String traceId = "";
                    try {
                        TSpanData tSpanData = new TSpanData();
                        new TDeserializer(ThriftUtil.PROTOCOL_FACTORY).deserialize(tSpanData, message.getBody());
                        traceId = tSpanData.getTraceId();
                        metricsExporterService.parse(tSpanData);
                    } catch (Throwable t) {
                        log.error("consumer message error", t);
                    }
                    clientMessageQueue.enqueue(traceId, message);
                }

                long now = System.currentTimeMillis();

                i.addAndGet(list.size());
                if ((now - time.get() >= TimeUnit.SECONDS.toMillis(15))) {
                    i.set(0);
                    time.set(now);
                    cacheService.cacheData();
                }

                enterManager.processEnter();
                return ConsumeOrderlyStatus.SUCCESS;
            } finally {
                enterManager.getProcessNum().decrementAndGet();
            }
        }
    }
}
