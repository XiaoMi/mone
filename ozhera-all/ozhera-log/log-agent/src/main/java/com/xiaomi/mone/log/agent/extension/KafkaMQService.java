package com.xiaomi.mone.log.agent.extension;

import com.google.common.base.Preconditions;
import com.xiaomi.mone.log.agent.export.MsgExporter;
import com.xiaomi.mone.log.agent.output.Output;
import com.xiaomi.mone.log.agent.service.OutPutService;
import com.xiaomi.mone.log.api.model.meta.LogPattern;
import com.xiaomi.mone.log.api.model.meta.MQConfig;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static com.xiaomi.mone.log.common.Constant.DEFAULT_CONSUMER_GROUP;

@Service(name = "KafkaMQService")
@Slf4j
public class KafkaMQService implements OutPutService {

    private ConcurrentHashMap<String, Producer> producerMap;

    public void init() {
        producerMap = new ConcurrentHashMap<>(128);
    }

    @Override
    public boolean compare(Output oldOutPut, Output newOutPut) {

        return false;
    }

    @Override
    public void preCheckOutput(Output output) {
        KafkaOutput rmqOutput = (KafkaOutput) output;
        Preconditions.checkArgument(null != rmqOutput.getClusterInfo(), "rmqOutput.getClusterInfo can not be null");
        Preconditions.checkArgument(null != rmqOutput.getTopic(), "rmqOutput.getTopic can not be null");
    }

    @Override
    public MsgExporter exporterTrans(Output output) throws Exception {
        KafkaOutput kafkaOutput = (KafkaOutput) output;
        String nameSrvAddr = kafkaOutput.getClusterInfo();
        Producer mqProducer = producerMap.get(nameSrvAddr);
        if (null == mqProducer) {
            mqProducer = initMqProducer(kafkaOutput);
            producerMap.put(String.valueOf(nameSrvAddr), mqProducer);
        }

        KafkaExporter rmqExporter = new KafkaExporter(mqProducer);
        rmqExporter.setRmqTopic(kafkaOutput.getTopic());
        rmqExporter.setBatchSize(kafkaOutput.getBatchExportSize());

        return rmqExporter;
    }

    private Producer initMqProducer(KafkaOutput output) {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, output.getClusterInfo());
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        if (StringUtils.isNotEmpty(output.getAk()) && StringUtils.isNotEmpty(output.getSk())) {
            properties.setProperty("security.protocol", "SASL_SSL");
            properties.setProperty("sasl.mechanism", "PLAIN");
            properties.setProperty("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + output.getAk() + "\" password=\"" + output.getSk() + "\";");
        }
        Producer<String, String> producer = new KafkaProducer<>(properties);
        return producer;
    }

    @Override
    public void removeMQ(Output output) {
        KafkaOutput kafkaOutput = (KafkaOutput) output;
        if (null != producerMap.get(kafkaOutput.getClusterInfo())) {
            producerMap.get(kafkaOutput.getClusterInfo()).close();
        }
    }

    @Override
    public Output configOutPut(LogPattern logPattern) {

        MQConfig mqConfig = logPattern.getMQConfig();
        KafkaOutput output = new KafkaOutput();
        output.setOutputType(KafkaOutput.OUTPUT_KAFKAMQ);
        output.setClusterInfo(mqConfig.getClusterInfo());
        output.setProducerGroup(mqConfig.getProducerGroup());
        output.setAk(mqConfig.getAk());
        output.setSk(mqConfig.getSk());
        output.setTopic(mqConfig.getTopic());
        output.setPartitionCnt(mqConfig.getPartitionCnt());
        output.setTag(mqConfig.getTag());
        output.setProducerGroup(DEFAULT_CONSUMER_GROUP + (null == logPattern.getPatternCode() ? "" : logPattern.getPatternCode()));
        return output;
    }
}
