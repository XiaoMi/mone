package com.xiaomi.mone.log.stream;

import com.xiaomi.mone.log.stream.plugin.mq.talos.TalosConfig;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/29 9:55
 */
public class StreamTest {

    @Test
    public void test() {
        String str = "";
        System.out.println(str.length());
    }


    @Test
    public void testTalos() throws  IOException {
        TalosConfig config = new TalosConfig();
        config.setConsumerGroup("talosConsumerGroup");
        config.setClientPrefix("logSystem-");
        Properties pros = new Properties();
        pros.setProperty("galaxy.talos.service.endpoint", "http://127.0.0.1");
//        TalosConsumerConfig consumerConfig = new TalosConsumerConfig(pros);
//        config.setConsumerConfig(consumerConfig);
        config.setTopicName("");
//        new TalosConsumer(config.getConsumerGroup(), config.getConsumerConfig(),
//                config.getCredential(), config.getTopicName(), new RmqSinkJob.TalosMessageProcessorFactory(new RmqSinkJob()),
//                config.getClientPrefix(), new SimpleTopicAbnormalCallback());
        System.in.read();
    }
}
