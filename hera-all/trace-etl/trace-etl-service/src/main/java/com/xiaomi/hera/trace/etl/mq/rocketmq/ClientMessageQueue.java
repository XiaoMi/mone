package com.xiaomi.hera.trace.etl.mq.rocketmq;

import com.xiaomi.hera.trace.etl.common.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/11/6 4:27 下午
 */
@Slf4j
public class ClientMessageQueue {
    public Map<Integer, ClientMessageQueueWrapper> clientMessageQueues = new ConcurrentHashMap<>();

    private volatile int rocketMQQueueSize;

    private RocketMqProducer producer;

    private static final int CLIENT_QUEUE_SIZE = 2000;

    private static final int CLIENT_QUEUE_BATCH_SEND_SIZE = 1000;

    private static final int CLIENT_QUEUE_SEND_GAP = 1000;

    private final static int FETCH_ROCKETMQ_QUEUE_GAP = 30;

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
        for (MessageQueue messageQueue : queueList) {
            int queueId = messageQueue.getQueueId();
            ClientMessageQueueWrapper clientQueue = clientMessageQueues.get(queueId);
            if (clientQueue == null) {
                ClientMessageQueueWrapper clientMessageQueueWrapper = new ClientMessageQueueWrapper(messageQueue, new ArrayBlockingQueue<>(CLIENT_QUEUE_SIZE));
                clientMessageQueues.put(queueId, clientMessageQueueWrapper);
                initClientQueueExporter(queueId);
            }
        }
        setRocketMQQueueSize(queueList.size());
    }

    private void initClientQueueExporter(int queueId) {
        final int currQueueId = queueId;
        // 初始化导出任务，定时定量从client queue中取出消息，发送到mq
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(1), r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(false);
            thread.setName("client-queue-exporter-" + currQueueId);
            return thread;
        });
        threadPoolExecutor.submit(new ClientQueueExporter(currQueueId));
    }

    private class ClientQueueExporter implements Runnable {

        private int queueId;

        private long lastSendTime = System.currentTimeMillis();

        public ClientQueueExporter(int queueId) {
            this.queueId = queueId;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    ClientMessageQueueWrapper clientMessageQueueWrapper = clientMessageQueues.get(queueId);
                    BlockingQueue<MessageExt> clientMessageQueue = clientMessageQueueWrapper.getClientMessageQueue();
                    int clientQueueSize = clientMessageQueue.size();
                    if (clientQueueSize > 0) {
                        if (clientQueueSize >= CLIENT_QUEUE_BATCH_SEND_SIZE || System.currentTimeMillis() - lastSendTime >= CLIENT_QUEUE_SEND_GAP) {
                            List<MessageExt> list = new ArrayList<>();
                            clientMessageQueue.drainTo(list);
                            producer.send(list, clientMessageQueueWrapper.getRocketMQMessageQueue());
                        }
                    }
                } catch (Throwable t) {
                    log.error("client queue exporter error : ", t);
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    log.error("sleep error : ", e);
                }
            }
        }
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
                    log.info("fetch message queue size : "+messageQueues.size());
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
