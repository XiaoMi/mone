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

import com.xiaomi.youpin.gwdash.service.FeiShuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


@Component
@Slf4j
public class MqHeartbeatHandler {

    @Value("${rocket.tag.rq.heartbeat}")
    private String heartbeatTag;

    @Value("${rocketmq.topic}")
    private String topic;

    public static String tag;

    @Value("${rocketmq.namesrv.addr}")
    private String namesrvAddr;

    @Autowired
    private FeiShuService feiShuService;

    @Autowired
    private DefaultMQProducer producer;

    private ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();

    private AtomicLong msgTime = new AtomicLong();


    @PostConstruct
    public void init() {
        tag = heartbeatTag;
        msgTime.set(System.currentTimeMillis());

        //send heartbeat msg
        pool.scheduleAtFixedRate(() -> {
            String heartbeatMsg = String.valueOf(System.currentTimeMillis());
            log.info("send " + heartbeatMsg);
            Message msg = new Message(topic, tag, heartbeatMsg.getBytes());
            try {
                producer.send(msg);
            } catch (Exception ex) {
                log.error("send gwdash heartbeat msg error: " + ex.getMessage(), ex);
                if (!namesrvAddr.contains("local")) {
                    feiShuService.sendMsg("", "RockMQ不能发送信息。 \n地址： " + namesrvAddr + "\n" + ex.toString());
                }
            }
        }, 0, 1, TimeUnit.MINUTES);

        //check heartbeat msg
        pool.scheduleAtFixedRate(() -> {
            log.info("schedule check gwdash beartbeat msg, current time is " + System.currentTimeMillis() + ", msgTime is " + msgTime.get());
            long now = System.currentTimeMillis();
            if (now - msgTime.get() > TimeUnit.SECONDS.toMillis(90)) {
                if (!namesrvAddr.contains("local")) {
                    feiShuService.sendMsg("", "RockMQ不能接受信息。 \n地址： " + namesrvAddr);
                }
            }
        }, 2, 1, TimeUnit.MINUTES);
    }


    /**
     * 存储健康监测结果
     *
     * @param message
     */
    public void consumeMessage(MessageExt message) {
        log.info("HealthCheckHandler#consumeMessage: {} {}", message.getMsgId(), new String(message.getBody()));
        try {
            String msg = new String(message.getBody());
            msgTime.set(Long.valueOf(msg.trim()));
        } catch (Throwable ex) {
            log.warn("health check error:" + ex.getMessage(), ex);
        }
    }

}
