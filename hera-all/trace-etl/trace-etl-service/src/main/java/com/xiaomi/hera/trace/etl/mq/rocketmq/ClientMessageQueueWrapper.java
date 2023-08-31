package com.xiaomi.hera.trace.etl.mq.rocketmq;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.concurrent.BlockingQueue;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/11/6 5:24 下午
 */
public class ClientMessageQueueWrapper {

    public MessageQueue rocketMQMessageQueue;

    public BlockingQueue<MessageExt> clientMessageQueue;

    public ClientMessageQueueWrapper(MessageQueue rocketMQMessageQueue, BlockingQueue<MessageExt> clientMessageQueue){
        this.rocketMQMessageQueue = rocketMQMessageQueue;
        this.clientMessageQueue = clientMessageQueue;
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
}
