package com.xiaomi.mone.log.stream.plugin.mq.talos;

import lombok.extern.slf4j.Slf4j;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/28 17:22
 */
@Slf4j
public class TalosMessageProcessor {

//    private final AtomicLong successGetNumber = new AtomicLong(0);
//
//    private final LogDataTransfer dataTransfer;
//    private final String topicName;
//    private final String tag;
//    private int partitionId;
//
//    public TalosMessageProcessor(LogDataTransfer dataTransfer) {
//        this.dataTransfer = dataTransfer;
//        this.topicName = dataTransfer.getSinkJobConfig().getTopic();
//        this.tag = dataTransfer.getSinkJobConfig().getTag();
//    }
//
//    @Override
//    public void init(TopicAndPartition topicAndPartition, long messageOffset) {
//        this.partitionId = topicAndPartition.partitionId;
//        log.info("talos consumer init,topicName:{},topicAndPartitionId;{},tag:{},messageOffset:{}",
//                topicAndPartition.topicName, partitionId, tag, messageOffset);
//    }
//
//    @Override
//    public void process(List<MessageAndOffset> messages, MessageCheckpointer messageCheckpointer) {
//        try {
//            String time = DateUtils.getTime();
//            // add your process logic for 'messages'
//            for (MessageAndOffset messageAndOffset : messages) {
//                String msg = new String(messageAndOffset.getMessage().getMessage(), "UTF-8");
//                log.debug("Message content: " + new String(messageAndOffset.getMessage().getMessage()));
////                dataTransfer.handleMessage(MiddlewareEnum.TALOS.getName(), msg, time);
//            }
//            long count = successGetNumber.addAndGet(messages.size());
//            if (count % COUNT_NUM == 0) {
//                log.info("Consuming data,topic,Consuming total data so far: {},topic:{},tag:{}", count, topicName, tag);
//            }
//        } catch (Throwable throwable) {
//            log.error("talos consume,process error, ", new Exception(throwable));
//        }
//    }
//
//    @Override
//    public void shutdown(MessageCheckpointer messageCheckpointer) {
//        log.info("talos consumer shutdown, is checkpoint:{},topic:{},,topicAndPartitionId;{},tag:{}",
//                messageCheckpointer.checkpoint(), topicName, partitionId, tag);
//    }
}
