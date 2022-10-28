///*
// *  Copyright 2020 Xiaomi
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.xiaomi.youpin.gwdash.rocketmq;
//
//import com.google.gson.Gson;
//import com.xiaomi.youpin.gwdash.service.*;
//import com.xiaomi.youpin.mischedule.api.service.bo.CompileEnum;
//import com.xiaomi.youpin.mischedule.api.service.bo.PowerOnResult;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.common.utils.StringUtils;
//import org.apache.rocketmq.acl.common.AclClientRPCHook;
//import org.apache.rocketmq.acl.common.SessionCredentials;
//import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
//import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
//import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
//import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
//import org.apache.rocketmq.client.exception.MQClientException;
//import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
//import org.apache.rocketmq.remoting.RPCHook;
//import org.nutz.dao.Dao;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//
///**
// * @author tsingfu
// */
//@Service
//@Slf4j
//public class RocketMQConsumer {
//
//    @Autowired
//    private LogService logService;
//
//    @Autowired
//    private PipelineService pipelineService;
//
//    @Autowired
//    private MErrorService errorService;
//
//    @Autowired
//    private CodeCheckerHandler codeCheckerHandler;
//
//    @Autowired
//    private JavaDocHandler javaDocHandler;
//
//    @Autowired
//    private SonarQubeHandler sonarQubeHandler;
//
////    @Autowired
////    private HealthCheckHandler healthCheckHandler;
//
//    @Autowired
//    private CompileHandler compileHandler;
//
//    @Autowired
//    private DockerfileHandler dockerfileHandler;
//
//    @Autowired
//    private K8sDeployHandler k8sDeployHandler;
//
//    @Autowired
//    private WebStaticResourceHandler webStaticResourceHandler;
//
//    @Autowired
//    private Dao dao;
//
//    @Autowired
//    private MachineManagementServiceImp machineManagementServiceImp;
//
//    @Value("${rocketmq.namesrv.addr}")
//    private String namesrvAddr;
//
//    @Value("${rocketmq.group}")
//    private String consumerGroup;
//
//    @Value("${rocketmq.consumer.topic}")
//    private String consumerTopic;
//
//    @Value("${rocketmq.ak}")
//    private String ak;
//
//    @Value("${rocketmq.sk}")
//    private String sk;
//
//    @Autowired
//    private ProjectEnvService projectEnvService;
//
//    private String powerOnTag = "PowerOn";
//
//    private DefaultMQPushConsumer defaultMQPushConsumer;
//
//    @PostConstruct
//    public void init() throws MQClientException {
//        if (StringUtils.isNotEmpty(ak)
//            && StringUtils.isNotEmpty(sk)) {
//            SessionCredentials credentials = new SessionCredentials(ak, sk);
//            RPCHook rpcHook = new AclClientRPCHook(credentials);
//            defaultMQPushConsumer = new DefaultMQPushConsumer(consumerGroup, rpcHook, new AllocateMessageQueueAveragely());
//        } else {
//            defaultMQPushConsumer = new DefaultMQPushConsumer(consumerGroup);
//        }
//        defaultMQPushConsumer.setNamesrvAddr(namesrvAddr);
//        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
//
//        /**
//         * 调度系统结果信息：
//         * java doc, 代码监测，容器健康监测，构建编译
//         */
//        defaultMQPushConsumer.subscribe(consumerTopic,
//            JavaDocHandler.tag + " || "
//                + SonarQubeHandler.tag + " || "
//                + CodeCheckerHandler.tag + " || "
//                // + HealthCheckHandler.tag + " || "
//                + DockerfileHandler.tag + " || "
//                //开机的任务tag
//                + powerOnTag + " || "
//                + NukeHandler.tag + " || "
//                + WebStaticResourceHandler.compileTag + " || "
//                + WebStaticResourceHandler.deployTag + " || "
//                    + K8sDeployHandler.k8sDeployTag + " || "
//                + CompileEnum.CompileLog.getName() + CompileHandler.tag + " || "
//                + CompileEnum.CompileStatus.getName() + CompileHandler.tag);
//
//
//        defaultMQPushConsumer.registerMessageListener((MessageListenerOrderly) (list, consumeOrderlyContext) -> {
//            try {
//                list.stream().forEach(it -> {
//                    String topic = it.getTopic();
//                    log.info("RocketMQConsumer#content: {}, {} {}", it.getMsgId(), topic, it.getTags());
//                    if (it.getTags().equals(HealthCheckHandler.tag)) {
//                        if (false) {
//                            //TODO
//                            // healthCheckHandler.consumeMessage(it);
//                        }
//                    } else if (it.getTags().equals(NukeHandler.tag)) {
//                        log.info("nuke message:{}", it);
//                    } else if (it.getTags().equals(CodeCheckerHandler.tag)) {
//                        codeCheckerHandler.consumeMessage(it);
//                    } else if (it.getTags().equals(JavaDocHandler.tag)) {
//                        javaDocHandler.consumeMessage(it);
//                    } else if (it.getTags().equals(SonarQubeHandler.tag)) {
//                        sonarQubeHandler.consumeMessage(it);
//                    } else if (it.getTags().equals(DockerfileHandler.tag)) {
//                        dockerfileHandler.consumeMessage(it);
//                    } else if (it.getTags().equals(powerOnTag)) {
//                        PowerOnResult result = new Gson().fromJson(new String(it.getBody()), PowerOnResult.class);
//                        projectEnvService.powerOn(result);
//                    } else if (it.getTags().equals(CompileEnum.CompileLog.getName() + CompileHandler.tag)) {
//                        compileHandler.handleCompileLog(it);
//                    } else if (it.getTags().equals(CompileEnum.CompileStatus.getName() + CompileHandler.tag)) {
//                        compileHandler.handleCompileStatus(it);
//                    } else if (it.getTags().equals(WebStaticResourceHandler.deployTag)) {
//                        webStaticResourceHandler.consumeDeployMessage(it);
//                    } else if (it.getTags().equals(WebStaticResourceHandler.compileTag)) {
//                        webStaticResourceHandler.consumeCompileMessage(it);
//                    } else if (it.getTags().equals(K8sDeployHandler.k8sDeployTag)) {
//                        k8sDeployHandler.consumeDeployMessage(it);
//                    }
//                });
//            } catch (Exception e) {
//                log.info("ProjectCompileResponseService#uildResService(): {}", e.getMessage());
//            }
//
//            return ConsumeOrderlyStatus.SUCCESS;
//        });
//
//        defaultMQPushConsumer.start();
//    }
//}
