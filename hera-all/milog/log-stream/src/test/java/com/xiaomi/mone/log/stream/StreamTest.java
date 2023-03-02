package com.xiaomi.mone.log.stream;

import com.xiaomi.infra.galaxy.rpc.thrift.Credential;
import com.xiaomi.infra.galaxy.rpc.thrift.UserType;
import com.xiaomi.mone.log.stream.plugin.mq.talos.TalosConfig;
import libthrift091.TException;
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
    public void testTalos() throws TException, IOException {
        TalosConfig config = new TalosConfig();
        config.setConsumerGroup("talosConsumerGroup");
        config.setClientPrefix("logSystem-");
        Properties pros = new Properties();
        pros.setProperty("galaxy.talos.service.endpoint", "http://127.0.0.1");
//        TalosConsumerConfig consumerConfig = new TalosConsumerConfig(pros);
//        config.setConsumerConfig(consumerConfig);
        Credential credential = new Credential();
        credential.setSecretKeyId("")
                .setSecretKey("")
                .setType(UserType.DEV_XIAOMI);
        config.setCredential(credential);
        config.setTopicName("");
//        new TalosConsumer(config.getConsumerGroup(), config.getConsumerConfig(),
//                config.getCredential(), config.getTopicName(), new RmqSinkJob.TalosMessageProcessorFactory(new RmqSinkJob()),
//                config.getClientPrefix(), new SimpleTopicAbnormalCallback());
        System.in.read();
    }
}
