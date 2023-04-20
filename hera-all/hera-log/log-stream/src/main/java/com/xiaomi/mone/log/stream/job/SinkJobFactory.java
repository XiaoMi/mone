//package com.xiaomi.mone.log.stream.job;
//
//import com.google.gson.Gson;
//import com.xiaomi.mone.es.EsProcessor;
//import com.xiaomi.mone.log.api.enums.MiddlewareEnum;
//import com.xiaomi.mone.log.common.Constant;
//import com.xiaomi.mone.log.parse.LogParser;
//import com.xiaomi.mone.log.parse.LogParserFactory;
//import com.xiaomi.mone.log.stream.common.SinkJobEnum;
//import com.xiaomi.mone.log.stream.job.extension.MessageSender;
//import com.xiaomi.mone.log.stream.job.extension.SinkJob;
//import com.xiaomi.mone.log.stream.job.extension.impl.EsMessageSender;
//import com.xiaomi.mone.log.stream.job.extension.impl.RocketMqSinkJob;
//import com.xiaomi.mone.log.stream.plugin.es.EsPlugin;
//import com.xiaomi.mone.log.stream.plugin.mq.rocketmq.RocketmqConfig;
//import com.xiaomi.mone.log.stream.plugin.mq.rocketmq.RocketmqPlugin;
//import com.xiaomi.mone.log.stream.sink.SinkChain;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
//
///**
// * @author wtt
// * @version 1.0
// * @description
// * @date 2022/8/22 16:03
// */
//@Slf4j
//public class SinkJobFactory {
//    private static Gson gson = Constant.GSON;
//
//    /**
//     * 生成 sinkJob
//     *
//     * @param sinkJobConfig
//     * @return
//     */
//    private static SinkJob instanceSinkJob(SinkJobConfig sinkJobConfig, MessageSender messageSender,
//                                           SinkJobEnum jobType) {
//        SinkJob sinkJob = null;
//        SinkChain sinkChain = sinkJobConfig.getSinkChain();
//        LogParser logParser = LogParserFactory.getLogParser(
//                sinkJobConfig.getParseType(), sinkJobConfig.getKeyList(), sinkJobConfig.getValueList(),
//                sinkJobConfig.getParseScript(), sinkJobConfig.getTopic(), sinkJobConfig.getTail(),
//                sinkJobConfig.getTag(), sinkJobConfig.getLogStoreName());
//
//        LogDataTransfer dataTransfer = new LogDataTransfer(sinkChain, logParser, messageSender);
//        // 不合理,违反了设计原则，但是为了拿到topicName暂时先织入了
//        dataTransfer.setSinkJobConfig(sinkJobConfig);
//        dataTransfer.setJobType(jobType);
//
//        if (MiddlewareEnum.ROCKETMQ.getName().equals(sinkJobConfig.getMqType())) {
//            RocketmqConfig rocketmqConfig = RocketmqPlugin.buildRocketmqConfig(sinkJobConfig.getAk(), sinkJobConfig.getSk(), sinkJobConfig.getClusterInfo(),
//                    sinkJobConfig.getTopic(), sinkJobConfig.getTag(), jobType);
//            DefaultMQPushConsumer rocketMqConsumer = RocketmqPlugin.getRocketMqConsumer(rocketmqConfig);
//            sinkJob = new RocketMqSinkJob(rocketmqConfig, rocketMqConsumer, dataTransfer);
//        }
////        if (MiddlewareEnum.TALOS.getName().equals(sinkJobConfig.getMqType())) {
////
////            TalosConfig talosConfig = TalosMqPlugin.buildTalosConsumer(
////                    sinkJobConfig.getAk(), sinkJobConfig.getSk(), sinkJobConfig.getClusterInfo(),
////                    sinkJobConfig.getTopic(), sinkJobConfig.getTag(), jobType);
////
////            sinkJob = new TalosSinkJob(talosConfig, dataTransfer);
////
////        }
//        return sinkJob;
//    }
////package com.xiaomi.mone.log.stream.job;
////
////import com.google.gson.Gson;
////import com.xiaomi.mone.es.EsProcessor;
////import com.xiaomi.mone.log.api.enums.MiddlewareEnum;
////import com.xiaomi.mone.log.common.Constant;
////import com.xiaomi.mone.log.parse.LogParser;
////import com.xiaomi.mone.log.parse.LogParserFactory;
////import com.xiaomi.mone.log.stream.common.SinkJobEnum;
////import com.xiaomi.mone.log.stream.job.extension.MessageSender;
////import com.xiaomi.mone.log.stream.job.extension.SinkJob;
////import com.xiaomi.mone.log.stream.job.extension.impl.EsMessageSender;
////import com.xiaomi.mone.log.stream.job.extension.impl.RocketMqSinkJob;
////import com.xiaomi.mone.log.stream.plugin.es.EsPlugin;
////import com.xiaomi.mone.log.stream.plugin.mq.rocketmq.RocketmqConfig;
////import com.xiaomi.mone.log.stream.plugin.mq.rocketmq.RocketmqPlugin;
////import com.xiaomi.mone.log.stream.sink.SinkChain;
////import lombok.extern.slf4j.Slf4j;
////import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
////
/////**
//// * @author wtt
//// * @version 1.0
//// * @description
//// * @date 2022/8/22 16:03
//// */
////@Slf4j
////public class SinkJobFactory {
////    private static Gson gson = Constant.GSON;
////
////    /**
////     * 生成 sinkJob
////     *
////     * @param sinkJobConfig
////     * @return
////     */
////    private static SinkJob instanceSinkJob(SinkJobConfig sinkJobConfig, MessageSender messageSender,
////                                           SinkJobEnum jobType) {
////        SinkJob sinkJob = null;
////        SinkChain sinkChain = sinkJobConfig.getSinkChain();
////        LogParser logParser = LogParserFactory.getLogParser(
////                sinkJobConfig.getParseType(), sinkJobConfig.getKeyList(), sinkJobConfig.getValueList(),
////                sinkJobConfig.getParseScript(), sinkJobConfig.getTopic(), sinkJobConfig.getTail(),
////                sinkJobConfig.getTag(), sinkJobConfig.getLogStoreName());
////
////        LogDataTransfer dataTransfer = new LogDataTransfer(sinkChain, logParser, messageSender);
////        // 不合理,违反了设计原则，但是为了拿到topicName暂时先织入了
////        dataTransfer.setSinkJobConfig(sinkJobConfig);
////        dataTransfer.setJobType(jobType);
////
////        if (MiddlewareEnum.ROCKETMQ.getName().equals(sinkJobConfig.getMqType())) {
////            RocketmqConfig rocketmqConfig = RocketmqPlugin.buildRocketmqConfig(sinkJobConfig.getAk(), sinkJobConfig.getSk(), sinkJobConfig.getClusterInfo(),
////                    sinkJobConfig.getTopic(), sinkJobConfig.getTag(), jobType);
////            DefaultMQPushConsumer rocketMqConsumer = RocketmqPlugin.getRocketMqConsumer(rocketmqConfig);
////            sinkJob = new RocketMqSinkJob(rocketmqConfig, rocketMqConsumer, dataTransfer);
////        }
//////        if (MiddlewareEnum.TALOS.getName().equals(sinkJobConfig.getMqType())) {
//////
//////            TalosConfig talosConfig = TalosMqPlugin.buildTalosConsumer(
//////                    sinkJobConfig.getAk(), sinkJobConfig.getSk(), sinkJobConfig.getClusterInfo(),
//////                    sinkJobConfig.getTopic(), sinkJobConfig.getTag(), jobType);
//////
//////            sinkJob = new TalosSinkJob(talosConfig, dataTransfer);
//////
//////        }
////        return sinkJob;
////    }
////
////    /**
////     * 正常启动写入的es的任务
////     *
////     * @param sinkJobConfig
////     * @return
////     */
////    public static SinkJob instanceSinkJobEs(SinkJobConfig sinkJobConfig) {
////        EsProcessor esProcessor = EsPlugin.getEsProcessor(sinkJobConfig.getEsInfo());
////        MessageSender esSender = new EsMessageSender(esProcessor, sinkJobConfig.getIndex());
////        return instanceSinkJob(sinkJobConfig, esSender, SinkJobEnum.NORMAL_JOB);
////    }
////
////    /**
////     * 备份的任务
////     *
////     * @param sinkJobConfig
////     * @return
////     */
//////    public static SinkJob instanceSinkJobBackUp(SinkJobConfig sinkJobConfig) {
//////        MessageSender lokiSender = new LokiSender(sinkJobConfig);
//////        return instanceSinkJob(sinkJobConfig, lokiSender, SinkJobEnum.BACKUP_JOB);
//////    }
////
////}
//    /**
//     * 正常启动写入的es的任务
//     *
//     * @param sinkJobConfig
//     * @return
//     */
//    public static SinkJob instanceSinkJobEs(SinkJobConfig sinkJobConfig) {
//        EsProcessor esProcessor = EsPlugin.getEsProcessor(sinkJobConfig.getEsInfo());
//        MessageSender esSender = new EsMessageSender(esProcessor, sinkJobConfig.getIndex());
//        return instanceSinkJob(sinkJobConfig, esSender, SinkJobEnum.NORMAL_JOB);
//    }
//
//    /**
//     * 备份的任务
//     *
//     * @param sinkJobConfig
//     * @return
//     */
////    public static SinkJob instanceSinkJobBackUp(SinkJobConfig sinkJobConfig) {
////        MessageSender lokiSender = new LokiSender(sinkJobConfig);
////        return instanceSinkJob(sinkJobConfig, lokiSender, SinkJobEnum.BACKUP_JOB);
////    }
//
//}
