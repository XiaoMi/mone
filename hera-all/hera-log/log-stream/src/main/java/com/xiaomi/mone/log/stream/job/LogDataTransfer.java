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
package com.xiaomi.mone.log.stream.job;

import com.google.common.util.concurrent.RateLimiter;
import com.xiaomi.mone.log.api.model.msg.LineMessage;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.parse.LogParser;
import com.xiaomi.mone.log.stream.common.LogStreamConstants;
import com.xiaomi.mone.log.stream.common.SinkJobEnum;
import com.xiaomi.mone.log.stream.job.extension.MessageSender;
import com.xiaomi.mone.log.stream.job.extension.MqMessagePostProcessing;
import com.xiaomi.mone.log.stream.sink.SinkChain;
import com.xiaomi.youpin.docean.Ioc;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.xiaomi.mone.log.common.Constant.COUNT_NUM;
import static com.xiaomi.mone.log.parse.LogParser.TIME_STAMP_MILLI_LENGTH;
import static com.xiaomi.mone.log.parse.LogParser.esKeyMap_timestamp;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/8/22 15:51
 */

@Slf4j
public class LogDataTransfer {

    private final SinkChain sinkChain;
    private final LogParser logParser;
    private final MessageSender messageSender;
    @Getter
    @Setter
    private SinkJobConfig sinkJobConfig;
    @Setter
    private SinkJobEnum jobType;

    private final AtomicLong sendMsgNumber = new AtomicLong(0);

    private RateLimiter rateLimiter = RateLimiter.create(180000000);

    private MqMessagePostProcessing messagePostProcessing;

    public LogDataTransfer(SinkChain sinkChain, LogParser logParser,
                           MessageSender messageSender, SinkJobConfig sinkJobConfig) {
        this.sinkChain = sinkChain;
        this.logParser = logParser;
        this.messageSender = messageSender;
        this.sinkJobConfig = sinkJobConfig;
        String mqPostProcessingBean = sinkJobConfig.getMqType() + LogStreamConstants.postProcessingProviderBeanSuffix;
        this.messagePostProcessing = Ioc.ins().getBean(mqPostProcessingBean);
    }


    public void handleMessage(String type, String msg, String time) {
        Map<String, Object> dataMap;
        try {
            LineMessage lineMessage = Constant.GSON.fromJson(msg, LineMessage.class);
            String ip = lineMessage.getProperties(LineMessage.KEY_IP);
            Long lineNumber = lineMessage.getLineNumber();
            dataMap = logParser.parse(lineMessage.getMsgBody(), ip, lineNumber, lineMessage.getTimestamp(), lineMessage.getFileName());
            if (SinkJobEnum.NORMAL_JOB == jobType) {
                if (null != dataMap && !sinkChain.execute(dataMap)) {
                    sendMessage(dataMap);
                }
            } else {
                sendMessage(dataMap);
            }
            if (sendMsgNumber.get() % COUNT_NUM == 0 || sendMsgNumber.get() == 1) {
                log.info(jobType.name() + " send msg:{}", dataMap);
            }
            messagePostProcessing.postProcessing(sinkJobConfig, msg);
        } catch (Exception e) {
            log.error(jobType.name() + " parse and send error", e);
        }
    }

    private void sendMessage(Map<String, Object> m) throws Exception {
        sendMsgNumber.incrementAndGet();
        rateLimiter.acquire();
        checkInsertTimeStamp(m);
        messageSender.send(m);
    }

    public void checkInsertTimeStamp(Map<String, Object> mapData) {
        mapData.putIfAbsent(esKeyMap_timestamp, Instant.now().toEpochMilli());
        Object timeStamp = mapData.get(esKeyMap_timestamp);
        if (timeStamp.toString().length() != TIME_STAMP_MILLI_LENGTH) {
            mapData.put(esKeyMap_timestamp, Instant.now().toEpochMilli());
        }
    }
}
