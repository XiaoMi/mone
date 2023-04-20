package com.xiaomi.mone.log.stream.plugin.hdfs;

import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TalosProducerHdfs {

//    private static final String accessKey = Config.ins().get("talos.mq.ak","");
//    private static final String accessSecret = Config.ins().get("talos.mq.sk","");
//    private static final String topicName = Config.ins().get("topic.name","");
//    private TalosProducerConfig producerConfig;
//    private Credential credential;
//    CopyOnWriteArrayList<Message>  messageList = new CopyOnWriteArrayList<>();
//    private final int toPutMsgNumber = 1000;
//    private static TalosProducer talosProducer;
//
//
//    public TalosProducerHdfs() {
//        Properties pros = new Properties();
//        pros.setProperty("galaxy.talos.service.endpoint", Config.ins().get("127.0.0.1",""));
//        pros.setProperty("galaxy.talos.producer.max.buffered.message.bytes","104857600");
//        pros.setProperty("galaxy.talos.producer.max.buffered.milli.secs","200");
//        pros.setProperty("galaxy.talos.producer.max.retry.times","4");
//        producerConfig = new TalosProducerConfig(pros);
//        credential = new Credential();
//        credential.setSecretKeyId(accessKey).setSecretKey(accessSecret).setType(UserType.DEV_XIAOMI);
//    }
//
//    /**
//     *  序列化 && send message
//     */
//    public void send(Map<String, Object> map)  {
//        if (messageList.size() <= toPutMsgNumber){
//            thriftSerialize(map);
//            map = null;
//        }else {
//            start(messageList);
//            messageList.clear();
//        }
//    }
//
//    /**
//     * thrift序列化
//     */
//    private void thriftSerialize(Map<String, Object> map){
//
//        TSerializer serializer = new TSerializer(new TCompactProtocol.Factory());
////        MilogTalosSinkHdfs milogTalosSinkHdfs = new MilogTalosSinkHdfs();
//        MilogHdfs milogHdfs = new MilogHdfs();
//        milogHdfs.setTraceId(String.valueOf(map.get("traceId")));
//        milogHdfs.setOther(String.valueOf(map.get("other")));
//        milogHdfs.setMqtag(String.valueOf(map.get("mqtag")));
//        milogHdfs.setLevel(String.valueOf(map.get("level")));
//        milogHdfs.setAppName(String.valueOf(map.get("appName")));
//        milogHdfs.setTail(String.valueOf(map.get("tail")));
//        milogHdfs.setClassName(String.valueOf(map.get("className")));
//        milogHdfs.setMessage(String.valueOf(map.get("message")));
//        milogHdfs.setLogsource(String.valueOf(map.get("logsource")));
//        milogHdfs.setThreadName(String.valueOf(map.get("threadName")));
//        milogHdfs.setLogip(String.valueOf(map.get("logip")));
//        milogHdfs.setMqtopic(String.valueOf(map.get("mqtopic")));
//        milogHdfs.setLogstore(String.valueOf(map.get("logstore")));
//        milogHdfs.setTimestamp(String.valueOf(map.get("timestamp")));
//        try {
//            Message message  = new Message(ByteBuffer.wrap(serializer.serialize((milogHdfs))));
//            messageList.add(message);
//        } catch (TException e) {
//            log.info("thrift Serialize Exception!！",e);
//        }
//    }
//
//    /**
//     *talos 发送
//     */
//    public void start(List<Message> messageList)  {
//
//        try {
//            if (talosProducer == null){
//                talosProducer = new TalosProducer(producerConfig, credential,topicName, new SimpleTopicAbnormalCallback(),new MyMessageCallback());
//            }
//            if (null != messageList){
//                talosProducer.addUserMessage(messageList);
//            }
//            log.debug("talosProducer start after !!!");
//        } catch (Exception e) {
//            log.error("talos start Exception",e);
//        }
//    }
//
//    /**
//     * 回调函数
//     */
//    private static class MyMessageCallback implements UserMessageCallback {
//        @Override
//        public void onSuccess(UserMessageResult userMessageResult) {
////            long count = successPutNumber.addAndGet(userMessageResult.getMessageList().size());
////            for (Message message : userMessageResult.getMessageList()) {
////                LOG.info("success to put message: " + new String(message.getMessage()));
////            }
////            LOG.info("success to put message: " + count + " so far.");
//        }
//        @Override
//        public void onError(UserMessageResult userMessageResult) {
//              log.error("failed to put message");
////            try {
////                for (Message message : userMessageResult.getMessageList()) {
////                    log.info("failed to put message: " + message + " we will retry to put it.");
////                }
////                talosProducer.addUserMessage(userMessageResult.getMessageList());
////            } catch (ProducerNotActiveException e) {
////                log.error("failed to put message",e);
////            }
//        }
//    }
}
