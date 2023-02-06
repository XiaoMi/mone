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

import com.google.gson.Gson;
import com.xiaomi.youpin.gwdash.service.*;
import com.xiaomi.youpin.mischedule.api.service.bo.CompileEnum;
import com.xiaomi.youpin.mischedule.api.service.bo.PowerOnResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author tsingfu
 */
@Service
@Slf4j
public class RocketMQConsumer {

    @Autowired
    private LogService logService;

    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private MErrorService errorService;

    @Autowired
    private CodeCheckerHandler codeCheckerHandler;

    @Autowired
    private JavaDocHandler javaDocHandler;

    @Autowired
    private HealthCheckHandler healthCheckHandler;

    @Autowired
    private NukeHandler nukeHandler;

    @Autowired
    private CompileHandler compileHandler;

    @Autowired
    private DockerfileHandler dockerfileHandler;

    @Autowired
    private MqHeartbeatHandler mqHeartbeatHandler;

    @Value("${rocketmq.topic}")
    private String gwdashTopic;

    @Autowired
    private Dao dao;

    @Autowired
    private MachineManagementServiceImp machineManagementServiceImp;

    @Value("${rocketmq.namesrv.addr}")
    private String namesrvAddr;

    @Value("${rocketmq.group}")
    private String consumerGroup;


    @Autowired
    private ProjectEnvService projectEnvService;


    private String powerOnTag = "PowerOn";

    private DefaultMQPushConsumer defaultMQPushConsumer;

    @PostConstruct
    public void init() throws MQClientException {
        defaultMQPushConsumer = new DefaultMQPushConsumer(consumerGroup);
        defaultMQPushConsumer.setNamesrvAddr(namesrvAddr);
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        /**
         * 调度系统结果信息：
         * java doc, 代码监测，容器健康监测
         */
        defaultMQPushConsumer.subscribe("schedule_server_topic",
                JavaDocHandler.tag + " || "
                        + CodeCheckerHandler.tag + " || "
                        + HealthCheckHandler.tag + " || "
                        + DockerfileHandler.tag + " || "
                        //开机的任务tag
                        + powerOnTag + " || "
                        + NukeHandler.tag);
        /**
         * java构建日志结果信息
         */
        defaultMQPushConsumer.subscribe(CompileEnum.CompileLog.getName(),
                CompileHandler.tag);
        /**
         * java构建状态结果信息
         */
        defaultMQPushConsumer.subscribe(CompileEnum.CompileStatus.getName(),
                CompileHandler.tag);

        defaultMQPushConsumer.subscribe(gwdashTopic, MqHeartbeatHandler.tag);

        defaultMQPushConsumer.registerMessageListener((MessageListenerOrderly) (list, consumeOrderlyContext) -> {
            try {
                list.stream().forEach(it -> {
                    String topic = it.getTopic();
                    log.info("RocketMQConsumer#content: {}, {} {}", it.getMsgId(), topic, it.getTags());
                    if (topic.equals(CompileEnum.CompileLog.getName())
                            || topic.equals(CompileEnum.CompileStatus.getName())) {
                        compileHandler.consumeMessage(it);
                    } else if ("schedule_server_topic".equals(topic)) {
                        if (it.getTags().equals(HealthCheckHandler.tag)) {
                             healthCheckHandler.consumeMessage(it);
                        } else if (it.getTags().equals(NukeHandler.tag)) {
                            log.info("nuke message:{}",it);
                            // nukeHandler.consumeMessage(it);
                        } else if (it.getTags().equals(CodeCheckerHandler.tag)) {
                             codeCheckerHandler.consumeMessage(it);
                        } else if (it.getTags().equals(JavaDocHandler.tag)) {
                             javaDocHandler.consumeMessage(it);
                        } else if (it.getTags().equals(DockerfileHandler.tag)) {
                            dockerfileHandler.consumeMessage(it);
                        } else if (it.getTags().equals(powerOnTag)) {
                            PowerOnResult result =new Gson().fromJson(new String(it.getBody()),PowerOnResult.class);
                            projectEnvService.powerOn(result);
                        }
                    } else if (gwdashTopic.equals(topic)) {
                        mqHeartbeatHandler.consumeMessage(it);
                    }
                });
            } catch (Exception e) {
                log.info("ProjectCompileResponseService#uildResService(): {}", e.getMessage());
            }

            return ConsumeOrderlyStatus.SUCCESS;
        });

        defaultMQPushConsumer.start();
    }
}
