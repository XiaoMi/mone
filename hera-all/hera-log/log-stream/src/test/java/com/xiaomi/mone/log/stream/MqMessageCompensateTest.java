/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
