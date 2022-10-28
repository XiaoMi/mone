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
//package com.xiaomi.youpin.gwdash.ws;
//
//import com.google.gson.Gson;
//import com.xiaomi.youpin.gwdash.bo.DataMessage;
//import com.xiaomi.youpin.gwdash.bo.ProjectCiCdBo;
//import com.xiaomi.youpin.gwdash.bo.VulcanusData;
//import com.xiaomi.youpin.gwdash.common.DeployTypeEnum;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectPipeline;
//import com.xiaomi.youpin.gwdash.rocketmq.CodeCheckerHandler;
//import com.xiaomi.youpin.gwdash.rocketmq.CompileHandler;
//import com.xiaomi.youpin.gwdash.rocketmq.DockerfileHandler;
//import com.xiaomi.youpin.gwdash.rocketmq.WebStaticResourceHandler;
//import com.xiaomi.youpin.gwdash.service.*;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.io.IOException;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//import java.util.concurrent.CopyOnWriteArraySet;
//import java.util.concurrent.locks.ReentrantLock;
//
///**
// * @author tsingfu
// * @modify zhangzhiyong1
// */
//@Slf4j
//public class CiCdWebSocketHandler extends TextWebSocketHandler {
//
//    public static final String CI_CD_SUBSCRIBE = "ci-cd-subscribe";
//
//    public static final String CODE_CHECK_STAGE = "projectCodeCheckRecord";
//
//    public static final String COMPILE_STAGE = "projectCompileRecord";
//
//    public static final String DEPLOY_STAGE = "deployInfo";
//
//
//    private PipelineService pipelineService;
//
//    public CiCdWebSocketHandler(PipelineService pipelineService) {
//        this.pipelineService = pipelineService;
//    }
//
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        log.info("ciCdWebSocketHandler: {}, {}", session, message);
//        Gson gson = new Gson();
//        DataMessage dataMessage = gson.fromJson(message.getPayload(), DataMessage.class);
//        String type = dataMessage.getMsgType();
//        ProjectCiCdBo projectCiCdBo =
//                gson.fromJson(dataMessage.getData(), ProjectCiCdBo.class);
//        long projectId = projectCiCdBo.getProjectId();
//        long envId = projectCiCdBo.getEnvId();
//        ProjectPipeline projectPipeline =
//                pipelineService.getProjectPipeline(projectId, envId).getData();
//        if (null != projectPipeline) {
//            long id = projectPipeline.getId();
//            if (CI_CD_SUBSCRIBE.equals(type)) {
//                addWebSocketSession("p" + id, session);
//            }
//            int deployType = projectPipeline.getDeploySetting().getDeployType();
//            long codeCheckId = projectPipeline.getCodeCheckId();
//            if (0 != codeCheckId) {
//                String log = pipelineService.getCodeCheckLog(codeCheckId).getData();
//                sendLog(codeCheckId, CodeCheckerHandler.CODE_CHECK_LOGS, CODE_CHECK_STAGE, log, session);
//            }
//            long compilationId = projectPipeline.getCompilationId();
//            if (0 != compilationId) {
//                if (deployType == DeployTypeEnum.DOCKERFILE.getId()
//                    || deployType == DeployTypeEnum.WEB.getId()) {
//                    String log = pipelineService.getDockerBuildLog(compilationId).getData();
//                    sendLog(compilationId, DockerfileHandler.DOCKER_BUILD_LOGS, COMPILE_STAGE, log, session);
//                } else {
//                    String log = pipelineService.getCompileLog(compilationId).getData();
//                    sendLog(compilationId,  CompileHandler.COMPILE_LOGS, COMPILE_STAGE, log, session);
//                }
//            }
//            if (deployType == DeployTypeEnum.WEB.getId()) {
//                String log = pipelineService.getWebCompileLog(id).getData();
//                sendLog(compilationId, WebStaticResourceHandler.WEB_COMPILE_LOGS, COMPILE_STAGE, log, session);
//                log = pipelineService.getWebDeployLog(id).getData();
//                sendLog(compilationId, WebStaticResourceHandler.WEB_DEPLOY_LOGS, DEPLOY_STAGE, log, session);
//            }
//        }
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
//        webSocketSession.close(closeStatus);
//        removeWebSocketSession(webSocketSession);
//    }
//
//    private void sendLog (long id, String type, String stage, String log, WebSocketSession session) throws IOException {
//        if (log.equals("")) {
//            return;
//        }
//        Gson gson = new Gson();
//        DataMessage msgLog = new DataMessage();
//        msgLog.setData(log);
//        msgLog.setMsgType(type);
//        msgLog.setStage(stage);
//        msgLog.setData(gson.toJson(VulcanusData.builder().id(id).message(log).build()));
//        session.sendMessage(new TextMessage(gson.toJson(msgLog)));
//    }
//
//    public static final ConcurrentMap<String, CopyOnWriteArraySet<WebSocketSession>> subscriber = new ConcurrentHashMap<>();
//
//    public static void pushMsg(String pId, String message) {
//        CopyOnWriteArraySet<WebSocketSession> sessions = subscriber.get(pId);
//        if (null != sessions) {
//            sessions.forEach(it -> {
//                synchronized (it) {
//                    try {
//                        log.info("push msg :{}", message);
//                        it.sendMessage(new TextMessage(message));
//                    } catch (Throwable e) {
//                        log.error("WsSubscriber#pushMsg:" + e.getMessage(), e);
//                    }
//                }
//            });
//        }
//    }
//
//    public static void addWebSocketSession(String pId, WebSocketSession webSocketSession) {
//        CopyOnWriteArraySet set = subscriber.get(pId);
//        if (null == set) {
//            set = new CopyOnWriteArraySet<>();
//            set.add(webSocketSession);
//            subscriber.put(pId, set);
//        } else {
//            set.add(webSocketSession);
//        }
//    }
//
//    public static void removeWebSocketSession(WebSocketSession webSocketSession) {
//        Set<Map.Entry<String, CopyOnWriteArraySet<WebSocketSession>>> set = subscriber.entrySet();
//        set.stream().forEach(it -> {
//            it.getValue().remove(webSocketSession);
//        });
//        set = subscriber.entrySet();
//        set.stream().forEach(it -> {
//            it.getValue().remove(webSocketSession);
//        });
//    }
//}
