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

package com.xiaomi.youpin.mischedule.notify;

import com.xiaomi.data.push.schedule.task.notify.Notify;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqNotify implements Notify {

    @Autowired
    private DefaultMQProducer producer;

    private String topic = "schedule_server_topic";

    @Override
    public void notify(String type, String s) {
        Message msg = new Message(topic, type, s.getBytes());
        try {
            producer.send(msg);
        } catch (Exception e) {
            log.warn("mq send msg error:{}", e.getMessage());
        }
    }
}
