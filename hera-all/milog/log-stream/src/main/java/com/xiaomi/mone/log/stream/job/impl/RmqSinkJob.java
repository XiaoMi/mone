package com.xiaomi.mone.log.stream.job.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class RmqSinkJob {

//    private static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//
//    private DefaultMQPushConsumer consumer;
//
//    private TalosConsumer talosConsumer;
//
//    private TalosConfig talosConfig;
//
//    private EsService esService;
//
//    private EsProcessor esProcessor;
//
//    private String mqType;
//
//    public String topic;
//
//    public String tag;
//
//    private String index;
//
//    public String keyList;
//
//    public String valueList;
//
//    public String parseScript;
//
//    public String logStoreName;
//
//    public String tail;
//
//    public Long logSpaceId;
//
//    public Long logStoreId;
//
//    public Long logTailId;
//
//    private LogParser logParser;
//
//    private final static String CONSUMERGROUPPREXFIX = "GROUP_";
//
//    public TalosProducerHdfs talosProducerHdfs = new TalosProducerHdfs();
//
//    public LokiClient lokiClient = new HttpLokiClient();
//
//    private Gson gson = new Gson();
//
//    public static Map<String, TalosConsumer> consumerMap = new HashedMap();
//
//    @Setter
//    public SinkChain sinkChain;
//
//    private final AtomicLong sendMsgNumber = new AtomicLong(0);
//
//    private RateLimiter rateLimiter = RateLimiter.create(1000000);
//
//    public static SinkJob initJob(String mqType, String ak, String sk, String clusterInfo, String topic, String tag,
//                                  String index, String keyList, String valueList, String parseScript,
//                                  String logStoreName, SinkChain sinkChain, String tail, EsInfo esInfo, Integer parseType,
//                                  Long logTailId, Long logStoreId, Long logSpaceId) {
//        RmqSinkJob job = new RmqSinkJob(topic, tag, index, keyList, valueList, parseScript, logStoreName, tail,
//                logTailId, logStoreId, logSpaceId);
//        job.setEsProcessor(EsPlugin.getEsProcessor(esInfo));
//        if (MiddlewareEnum.ROCKETMQ.getName().equals(mqType)) {
////            job.setConsumer(RocketmqPlugin.getRocketMqConsumer(ak, sk, clusterInfo, tag));
//        }
//        if (MiddlewareEnum.TALOS.getName().equals(mqType)) {
////            job.setTalosConfig(TalosMqPlugin.buildTalosConsumer(ak, sk, clusterInfo, topic, tag));
//        }
//        job.setMqType(mqType);
//        job.setEsProcessor(EsPlugin.getEsProcessor(esInfo));
//        if (sinkChain.getTeslaSink().getEsProcessor() == null && StringUtils.isEmpty(Config.ins().get("localIp", ""))) {
//            String esAddress = Config.ins().get("esAddress", "");
//            String user = Config.ins().get("user", "xiaomi");
//            String pwd = Config.ins().get("pwd", "xiaomi");
//            EsInfo esInfoTesla = new EsInfo(10011L, esAddress, user, pwd);
//            sinkChain.getTeslaSink().setEsProcessor(EsPlugin.getEsProcessor(esInfoTesla));
//        }
//        job.setSinkChain(sinkChain);
//        //设置解析器
//        job.setLogParser(LogParserFactory.getLogParser(parseType, keyList, valueList, parseScript, topic, tail, tag, logStoreName));
//        return job;
//    }
//
//    @Override
//    public boolean start() throws Exception {
//        if (MiddlewareEnum.ROCKETMQ.getName().equals(this.mqType)) {
//            return rocketMqStart();
//        }
//        if (MiddlewareEnum.TALOS.getName().equals(this.mqType)) {
//            return talosMqStart();
//        }
//        return false;
//    }
//
//    private boolean rocketMqStart() {
//        try {
//            consumer.subscribe(topic, tag);
//            log.info("[RmqSinkJob.start] job subscribed topic [topic:{},tag:{}]", topic, tag);
//        } catch (MQClientException e) {
//            log.error(String.format("[RmqSinkJob.start] logStream rockerMq start error,topic:%s,tag:%s", topic, tag), e);
//            throw new StreamException("[RmqSinkJob.start] job subscribed topic error,topic: " + topic + " tag: " + tag + " err: ", e);
//        }
//
//        consumer.registerMessageListener((MessageListenerOrderly) (list, consumeOrderlyContext) -> {
//            String time = getTime();
//            for (MessageExt messageExt : list) {
//                Map<String, Object> m = null;
//                String msg = new String(messageExt.getBody());
//                this.handleMessage(MiddlewareEnum.ROCKETMQ.getName(), msg, time);
//            }
//            return ConsumeOrderlyStatus.SUCCESS;
//        });
//        try {
//            consumer.start();
//            return true;
//        } catch (MQClientException e) {
//            log.error(String.format("rocket mq start error,topic:%s,tag:%s", topic, tag), e);
//        }
//        return false;
//    }
//
//    public void handleMessage(String type, String msg, String time) {
//        Map<String, Object> m = null;
//        try {
//            LineMessage lineMessage = Constant.GSON.fromJson(msg, LineMessage.class);
//            String ip = lineMessage.getProperties(LineMessage.KEY_IP);
//            Long lineNumber = lineMessage.getLineNumber();
//            m = logParser.parse(lineMessage.getMsgBody(), ip, lineNumber, Instant.now().toEpochMilli(), lineMessage.getFileName());
//            String esIndex = index + "-" + time;
//            if (null != m && !sinkChain.execute(m)) {
//                long num = sendMsgNumber.incrementAndGet();
//                rateLimiter.acquire();
//                checkInsertTimeStamp(m);
//                if (!m.containsKey(esKeyMap_MESSAGE) || null == m.get(esKeyMap_MESSAGE)) {
//                    log.info("before insert es,origin data:{},parsed data:{}", msg, m);
//                }
//                esProcessor.bulkInsert(esIndex, m);
//                if (lokiClient.getConfig().enabled) {
//                    lokiClient.send(m, lokiClient.buildFixedTags(logTailId, logStoreId, logSpaceId), logSpaceId.toString());
//                }
//                if (num % COUNT_NUM == 0 || num == 1) {
//                    log.info("send msg:{}", m);
//                }
//            }
//        } catch (Exception e) {
//            log.error(String.format("[RmqSinkJob consumer error] mqType:%s,index:%s,data:%s,tail:%s,topic:%s,tag:%s", mqType, index + "-" + time, m, tail, topic, tag), e);
//            log.error("parse and send error", e);
//        }
//    }
//
//    public void checkInsertTimeStamp(Map<String, Object> mapData) {
//        mapData.putIfAbsent(esKeyMap_timestamp, Instant.now().toEpochMilli());
//        Object timeStamp = mapData.get(esKeyMap_timestamp);
//        if (timeStamp.toString().length() != TIME_STAMP_MILLI_LENGTH) {
//            mapData.put(esKeyMap_timestamp, Instant.now().toEpochMilli());
//        }
//    }
//
//    private boolean talosMqStart() {
//        try {
//            String consumerMapKey = "";
//            // TODO add loki consumer group
//            if (!consumerMap.containsKey(consumerMapKey)) {
//                if (!talosConfig.isCommonTag()) {
//                    consumerMapKey = String.format("%s_%s_%s", talosConfig.getAk(), talosConfig.getClusterInfo(), talosConfig.getTopicName());
//                    talosConsumer = new TalosConsumer(
//                            talosConfig.getConsumerGroup(), talosConfig.getConsumerConfig(),
//                            talosConfig.getCredential(), talosConfig.getTopicName(),
//                            new TalosMessageProcessorFactory(this),
//                            talosConfig.getClientPrefix(), new SimpleTopicAbnormalCallback());
//                }
//                if (talosConfig.isCommonTag()) {
//                    consumerMapKey = String.format("%s_%s_%s_%s", talosConfig.getAk(), talosConfig.getClusterInfo(), talosConfig.getTopicName(), talosConfig.getTag());
//                    talosConsumer = new TalosConsumer(
//                            talosConfig.getConsumerGroup(), talosConfig.getConsumerConfig(),
//                            talosConfig.getCredential(), talosConfig.getTopicName(),
//                            talosConfig.getTag(), new TalosMessageProcessorFactory(this),
//                            talosConfig.getClientPrefix(), new SimpleTopicAbnormalCallback());
//                }
//
//                log.info("[TalosSinkJob.start] job started consumer [topic:{},tag:{}]", topic, tag);
//                consumerMap.put(consumerMapKey, talosConsumer);
//            }
//            return true;
//        } catch (TException e) {
//            log.error(String.format("talos start error talosConfig:%s", talosConfig), e);
//        }
//        return false;
//    }
//
//    public static class TalosMessageProcessorFactory implements MessageProcessorFactory {
//
//        public RmqSinkJob rmqSinkJob;
//
//        public TalosMessageProcessorFactory(RmqSinkJob rmqSinkJob) {
//            this.rmqSinkJob = rmqSinkJob;
//        }
//
//        @Override
//        public MessageProcessor createProcessor() {
//            return new TalosMessageProcessor(null);
//        }
//    }
//
//
//    @Override
//    public void shutdown() throws StreamException {
//        log.info("current consumer will stop：logstoreName:{},tail:{},topic:{}", logStoreName, tail, topic);
//        if (MiddlewareEnum.ROCKETMQ.getName().equals(this.mqType) && null != consumer) {
//            consumer.shutdown();
//            log.info("[RmqSinkJob.rocketmq shutdown] job consumer shutdown, topic:{},tag:{}", topic, tag);
//        }
//        if (MiddlewareEnum.TALOS.getName().equals(this.mqType) && null != talosConsumer) {
//            String consumerMapKey = String.format("%s_%s_%s", talosConfig.getAk(), talosConfig.getClusterInfo(), talosConfig.getTopicName());
//            consumerMap.remove(consumerMapKey);
//            talosConsumer.shutDown();
//            log.info("[RmqSinkJob.talos shutdown] job consumer shutdown, topic:{}", topic);
//
//            if (lokiClient.getConfig().enabled) {
//                try {
//                    lokiClient.close();
//                } catch (Exception e) {
//                    log.error("[loki] close loki plugin client error: ", e);
//                }
//            }
//        }
//    }
//
//
//    public RmqSinkJob() {
//    }
//
//    public RmqSinkJob(String topic, String tag, String index, String keyList, String valueList, String parseType,
//                      String logstoreName, String tail, Long logTailId, Long logStoreId, Long logSpaceId) {
//        this.topic = topic;
//        this.tag = tag;
//        this.index = index;
//        this.keyList = keyList;
//        this.valueList = valueList;
//        this.parseScript = parseType;
//        this.logStoreName = logstoreName;
//        this.tail = tail;
//        this.logTailId = logTailId;
//        this.logStoreId = logStoreId;
//        this.logSpaceId = logSpaceId;
//    }

}
