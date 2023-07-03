package com.xiaomi.hera.trace.etl.mq.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/11/6 5:24 下午
 */
@Slf4j
public class ClientMessageQueueWrapper {

    private MessageQueue rocketMQMessageQueue;

    private BlockingQueue<MessageExt> clientMessageQueue;

    private RocketMqProducer producer;

    private ExecutorService executor;

    private boolean running = true;

    public ClientMessageQueueWrapper(MessageQueue rocketMQMessageQueue, BlockingQueue<MessageExt> clientMessageQueue, RocketMqProducer producer) {
        this.rocketMQMessageQueue = rocketMQMessageQueue;
        this.clientMessageQueue = clientMessageQueue;
        this.producer = producer;
        startExport();
        log.info(rocketMQMessageQueue.getBrokerName() + " - "+rocketMQMessageQueue.getQueueId()+" start");
    }

    public BlockingQueue<MessageExt> getClientMessageQueue() {
        return clientMessageQueue;
    }

    public void setClientMessageQueue(BlockingQueue<MessageExt> clientMessageQueue) {
        this.clientMessageQueue = clientMessageQueue;
    }

    public MessageQueue getRocketMQMessageQueue() {
        return rocketMQMessageQueue;
    }

    public void setRocketMQMessageQueue(MessageQueue rocketMQMessageQueue) {
        this.rocketMQMessageQueue = rocketMQMessageQueue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if(o instanceof ClientMessageQueueWrapper) {
            ClientMessageQueueWrapper that = (ClientMessageQueueWrapper) o;
            return Objects.equals(rocketMQMessageQueue, that.rocketMQMessageQueue);
        }else if (o instanceof MessageQueue){
            MessageQueue queue = (MessageQueue) o;
            return Objects.equals(rocketMQMessageQueue, queue);
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return rocketMQMessageQueue.hashCode();
    }

    private void startExport() {
        // Initializes the export task, periodically and quantitatively fetches messages from the client queue and sends them to mq
        executor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(false);
            thread.setName("client-queue-exporter-" + rocketMQMessageQueue.getBrokerName() + "-" + rocketMQMessageQueue.getQueueId());
            return thread;
        });
        executor.submit(new ClientQueueExporter());
    }

    public void stopExport() {
        // Wait for all messages to be successfully sent
        while (clientMessageQueue.size() > 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                log.error("sleep error : ", e);
            }
        }
        running = false;
        // Destroy thread pool
        executor.shutdown();
        log.info(rocketMQMessageQueue.getBrokerName()+" - "+rocketMQMessageQueue.getQueueId()+" stopped");
    }

    private class ClientQueueExporter implements Runnable {

        private static final int CLIENT_QUEUE_BATCH_SEND_SIZE = 1000;

        private static final int CLIENT_QUEUE_SEND_GAP = 1000;

        private long lastSendTime = System.currentTimeMillis();

        public ClientQueueExporter() {
        }

        @Override
        public void run() {
            while (running) {
                try {
                    int clientQueueSize = clientMessageQueue.size();
                    if (clientQueueSize > 0) {
                        if (clientQueueSize >= CLIENT_QUEUE_BATCH_SEND_SIZE || System.currentTimeMillis() - lastSendTime >= CLIENT_QUEUE_SEND_GAP) {
                            List<MessageExt> list = new ArrayList<>();
                            clientMessageQueue.drainTo(list);
                            producer.send(list, rocketMQMessageQueue);
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

}
