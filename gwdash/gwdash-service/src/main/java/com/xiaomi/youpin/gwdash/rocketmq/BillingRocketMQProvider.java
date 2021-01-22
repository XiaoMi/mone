/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.gwdash.rocketmq;

import jdk.nashorn.internal.ir.annotations.Reference;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author goodjava@qq.com
 */
@Service
@Slf4j
public class BillingRocketMQProvider {


    @Autowired
    private DefaultMQProducer producer;

    private String topic = "local_billing_operation_topic";


    public void send(String message) {
        Message msg = new Message(topic, "", message.getBytes());
        try {
            producer.send(msg);
        } catch (Throwable e) {
            log.error("BillingRocketMQProvider send:{} error:{}", message, e.getMessage());
        }

    }


}
