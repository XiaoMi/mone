package com.xiaomi.mone.log.agent.extension;

import com.google.common.collect.Lists;
import com.xiaomi.mone.log.agent.export.MsgExporter;
import com.xiaomi.mone.log.api.model.msg.LineMessage;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.List;


public class KafkaExporter implements MsgExporter {

    private Producer mqProducer;

    private String rmqTopic;

    private Integer batchSize;

    public KafkaExporter(Producer mqProducer) {
        this.mqProducer = mqProducer;
    }

    @Override
    public void close() {

    }

    @Override
    public void export(LineMessage message) {
        this.export(Lists.newArrayList(message));
    }

    @Override
    public void export(List<LineMessage> messageList) {
        if (messageList.isEmpty()) {
            return;
        }

        for (LineMessage lineMessage : messageList) {
            ProducerRecord<String, String> record = new ProducerRecord<>(rmqTopic, "message", gson.toJson(lineMessage));
            mqProducer.send(record);
        }

    }

    public String getRmqTopic() {
        return rmqTopic;
    }

    public void setRmqTopic(String rmqTopic) {
        this.rmqTopic = rmqTopic;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }
}
