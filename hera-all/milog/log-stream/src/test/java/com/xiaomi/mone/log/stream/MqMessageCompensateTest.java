package com.xiaomi.mone.log.stream;

import com.google.common.collect.Lists;
import com.xiaomi.mone.log.stream.job.extension.impl.RocketCompensateMsgConsume;
import com.xiaomi.mone.log.stream.job.extension.impl.RocketMqMessageProduct;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/15 16:13
 */
@Slf4j
public class MqMessageCompensateTest {

    @Test
    public void testRocketProduce() {
        String url = "127.0.0.1:9876";
        String topic = "";
        RocketMqMessageProduct rocketMqMessageProduct = new RocketMqMessageProduct();
        rocketMqMessageProduct.product("", "", url, topic, Lists.newArrayList("test"));
    }

    @Test
    public void testRocketConsume() throws IOException {
        String url = "127.0.0.1:9876";
        String topic = "";
        RocketCompensateMsgConsume rocketCompensateMsgConsume = new RocketCompensateMsgConsume();
        rocketCompensateMsgConsume.consume("", "", url, topic);
        System.in.read();
    }
}
