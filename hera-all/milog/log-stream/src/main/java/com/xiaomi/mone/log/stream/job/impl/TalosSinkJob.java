package com.xiaomi.mone.log.stream.job.impl;

import com.xiaomi.mone.log.stream.job.LogDataTransfer;
import com.xiaomi.mone.log.stream.job.SinkJob;
import com.xiaomi.mone.log.stream.plugin.mq.talos.TalosConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/8/22 15:10
 */
@Slf4j
public class TalosSinkJob implements SinkJob {

    private final TalosConfig talosConfig;

//    private TalosConsumer talosConsumer;

    private final LogDataTransfer dataTransfer;

    public TalosSinkJob(TalosConfig talosConfig, LogDataTransfer dataTransfer) {
        this.talosConfig = talosConfig;
        this.dataTransfer = dataTransfer;
    }

    @Override
    public boolean start() throws Exception {
//        try {
//            if (!talosConfig.isCommonTag()) {
//                talosConsumer = new TalosConsumer(
//                        talosConfig.getConsumerGroup(), talosConfig.getConsumerConfig(),
//                        talosConfig.getCredential(), talosConfig.getTopicName(),
//                        new TalosMessageProcessorFactory(dataTransfer),
//                        talosConfig.getClientPrefix(), new SimpleTopicAbnormalCallback());
//                log.info("[TalosSinkJob.start] job started consumer [topic:{}]", talosConfig.getTopicName());
//                return true;
//            }
//            talosConsumer = new TalosConsumer(
//                    talosConfig.getConsumerGroup(), talosConfig.getConsumerConfig(),
//                    talosConfig.getCredential(), talosConfig.getTopicName(),
//                    talosConfig.getTag(), new TalosMessageProcessorFactory(dataTransfer),
//                    talosConfig.getClientPrefix(), new SimpleTopicAbnormalCallback());
//
//            log.info("[TalosSinkJob.start] job started consumer [topic:{},tag:{}]", talosConfig.getTopicName(), talosConfig.getTag());
//            return true;
//        } catch (TException e) {
//            log.error(String.format("talos start error talosConfig:%s", talosConfig), e);
//        }
        return false;
    }

    @Override
    public void shutdown() throws Exception {
//        talosConsumer.shutDown();
        log.info("[RmqSinkJob.talos shutdown] job consumer shutdown, topic:{},tag:{}", talosConfig.getTopicName(), talosConfig.getTag());
    }

//    public static class TalosMessageProcessorFactory implements MessageProcessorFactory {
//
//        public LogDataTransfer dataTransfer;
//
//        public TalosMessageProcessorFactory(LogDataTransfer dataTransfer) {
//            this.dataTransfer = dataTransfer;
//        }
//
//        @Override
//        public MessageProcessor createProcessor() {
//            return new TalosMessageProcessor(dataTransfer);
//        }
//    }
}
