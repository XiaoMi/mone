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

package com.xiaomi.youpin.tesla.bug.mq.consumer;

import com.google.gson.Gson;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.tesla.bug.common.CopyUtil;
import com.xiaomi.youpin.tesla.bug.domain.Record;
import com.xiaomi.youpin.tesla.bug.domain.RecordDo;
import com.xiaomi.youpin.tesla.bug.exception.BugException;
import com.xiaomi.youpin.tesla.bug.service.RecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2020/9/5
 */
@Slf4j
@Service
public class MqConsumer {

    @Resource
    private DefaultMQPushConsumer consumer;


    @Resource
    private RecordService recordService;


    public void init() {
        log.info("MqConsumer init");
        try {
            consumer.subscribe("bug_record", "*");
        } catch (MQClientException e) {
            log.error(e.getMessage());
            throw new BugException(e.getMessage());
        }

        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            msgs.stream().forEach(msg -> {
                RecordDo rd = new Gson().fromJson(new String(msg.getBody()), RecordDo.class);
                log.info("receive record:{}", rd);
                Record record = CopyUtil.copy(RecordDo.class,Record.class,rd,new Record());
                recordService.record(record);
            });
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        try {
            consumer.start();
        } catch (MQClientException e) {
            log.error("fail to start consume, nameSerAddress");
        }
        log.info("Consumer Started.");
    }


}
