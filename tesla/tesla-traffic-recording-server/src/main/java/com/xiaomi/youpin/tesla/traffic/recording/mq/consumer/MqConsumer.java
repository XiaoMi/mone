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

package com.xiaomi.youpin.tesla.traffic.recording.mq.consumer;

import com.google.gson.Gson;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic.Traffic;
import com.xiaomi.youpin.tesla.traffic.recording.common.TrafficException;
import com.xiaomi.youpin.tesla.traffic.recording.daoobj.TrafficDao;
import com.xiaomi.youpin.tesla.traffic.recording.service.TrafficService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;

import javax.annotation.Resource;

/**
 * @author 
 */
@Slf4j
@Service
public class MqConsumer {

    @Resource
    private DefaultMQPushConsumer consumer;


    @Resource
    private TrafficService trafficService;

    @Value("$recording_traffic_topic")
    private String topic;


    public void init() {
        log.info("MqConsumer init");
        try {
            consumer.subscribe(topic, "*");
        } catch (MQClientException e) {
            log.error(e.getMessage());
            throw new TrafficException(e.getMessage());
        }

        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            msgs.stream().forEach(msg -> {
                try {
                    Traffic traffic = new Gson().fromJson(new String(msg.getBody()), Traffic.class);
                    log.debug("MqConsumer, receive traffic:{}", traffic);
                    TrafficDao trafficDao = TrafficService.adapterToTrafficDao(traffic);
                    trafficService.addTraffic(trafficDao);
                } catch (Exception e) {
                    log.error("MqConsumer consume failed, ", e);
                }
            });
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        try {
            consumer.start();
        } catch (MQClientException e) {
            log.error("MqConsumer, fail to start consume, nameSerAddress");
        }
        log.info("MqConsumer, Consumer Started.");
    }

}
