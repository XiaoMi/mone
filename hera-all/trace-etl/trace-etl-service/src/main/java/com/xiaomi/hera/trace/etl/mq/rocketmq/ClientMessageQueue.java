package com.xiaomi.hera.trace.etl.mq.rocketmq;

import com.xiaomi.hera.trace.etl.common.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/11/6 4:27 下午
 */
@Slf4j
public class ClientMessageQueue {
    public List<ClientMessageQueueWrapper> clientMessageQueues = new CopyOnWriteArrayList<>();

    private volatile int rocketMQQueueSize;

    private RocketMqProducer producer;

    private static final int CLIENT_QUEUE_SIZE = 2000;


    private final static int FETCH_ROCKETMQ_QUEUE_GAP = 10;

    public ClientMessageQueue(RocketMqProducer producer) {
        this.producer = producer;
    }

    public void setRocketMQQueueSize(int size) {
        rocketMQQueueSize = size;
    }

    public void checkClientQueue(List<MessageQueue> queueList) {
        if (queueList == null || queueList.size() == 0) {
            return;
        }
        // 本地message queue 缺少的，进行添加
        queueList.stream()
                .filter(i -> !clientMessageQueues.stream().anyMatch(j -> j.getRocketMQMessageQueue().equals(i)))
                .forEach(queue -> clientMessageQueues.add(new ClientMessageQueueWrapper(queue, new ArrayBlockingQueue<>(CLIENT_QUEUE_SIZE), producer)));

        setRocketMQQueueSize(queueList.size());

        // 本地message queue 多出来的，进行销毁
        List<ClientMessageQueueWrapper> collect = clientMessageQueues.stream()
                .filter(i -> !queueList.contains(i.getRocketMQMessageQueue()))
                .collect(Collectors.toList());

        clientMessageQueues.stream()
                .filter(i -> !queueList.contains(i.getRocketMQMessageQueue()))
                .forEach(clientMessageQueues::remove);

        collect.forEach(ClientMessageQueueWrapper::stopExport);
    }

    public void enqueue(String traceId, MessageExt message) {
        try {
            // hash by traceId
            int i = HashUtil.consistentHash(traceId, rocketMQQueueSize);
            ClientMessageQueueWrapper clientMessageQueueWrapper = clientMessageQueues.get(i);
            clientMessageQueueWrapper.getClientMessageQueue().put(message);
        } catch (Throwable t) {
            log.error("client queue enqueue error : ", t);
        }
    }

    public void initFetchQueueTask() {
        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(
                () -> {
                    List<MessageQueue> messageQueues = producer.fetchMessageQueue();
                    log.info("fetch message queue size : " + messageQueues.size());
                    if (messageQueues == null || messageQueues.size() == 0) {
                        return;
                    }
                    checkClientQueue(messageQueues);
                },
                0,
                FETCH_ROCKETMQ_QUEUE_GAP,
                TimeUnit.SECONDS);
    }
}
