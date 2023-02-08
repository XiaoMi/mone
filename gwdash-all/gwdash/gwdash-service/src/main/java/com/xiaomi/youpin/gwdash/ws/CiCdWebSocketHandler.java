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

package com.xiaomi.youpin.gwdash.ws;

import com.google.gson.Gson;
import com.xiaomi.youpin.gwdash.bo.DataMessage;
import com.xiaomi.youpin.gwdash.bo.ProjectCompilationBo;
import com.xiaomi.youpin.gwdash.bo.VulcanusData;
import com.xiaomi.youpin.gwdash.dao.model.ProjectPipeline;
import com.xiaomi.youpin.gwdash.rocketmq.CodeCheckerHandler;
import com.xiaomi.youpin.gwdash.rocketmq.CompileHandler;
import com.xiaomi.youpin.gwdash.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

/**
 * @author tsingfu
 */
@Slf4j
public class CiCdWebSocketHandler extends TextWebSocketHandler {

    public static final String CODE_CHECK_SUBSCRIBE = "code-check-subscribe";

    public static final String COMPILE_SUBSCRIBE = "compile-subscribe";

    public static final String DOCKER_BUILD_SUBSCRIBE = "docker-build-subscribe";

    public static final String DEPLOY_SUBSCRIBE = "deploy-subscribe";

    private PipelineService pipelineService;

    public CiCdWebSocketHandler(PipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("ciCdWebSocketHandler: {}, {}", session, message);
        Gson gson = new Gson();
        DataMessage dataMessage = gson.fromJson(message.getPayload(), DataMessage.class);
        String type = dataMessage.getMsgType();
        ProjectCompilationBo projectCompilationBo =
                gson.fromJson(dataMessage.getData(), ProjectCompilationBo.class);
        ProjectPipeline projectPipeline =
                pipelineService.getProjectPipeline(projectCompilationBo.getProjectId(), projectCompilationBo.getEnvId()).getData();
        if (null != projectPipeline) {
            if (CODE_CHECK_SUBSCRIBE.equals(type)) {
                long codeCheckId = projectPipeline.getCodeCheckId();
                String log = pipelineService.getCodeCheckLog(codeCheckId).getData();
                sendLog(codeCheckId, CodeCheckerHandler.CODE_CHECK_LOGS, log, session);
                CodeCheckerService.addWebSocketSession(codeCheckId, session);
            } else if (COMPILE_SUBSCRIBE.equals(type)) {
                long compilationId = projectPipeline.getCompilationId();
                String log = pipelineService.getCompileLog(compilationId).getData();
                sendLog(compilationId, CompileHandler.COMPILE_LOGS, log, session);
                ProjectCompilationService.addWebSocketSession(compilationId, session);
            } else if (DOCKER_BUILD_SUBSCRIBE.equals(type)) {
                long compilationId = projectPipeline.getCompilationId();
                String log = pipelineService.getDockerBuildLog(compilationId).getData();
                sendLog(compilationId, CompileHandler.DOCKER_BUILD_LOGS, log, session);
                DockerfileService.addWebSocketSession(compilationId, session);
            } else if (DEPLOY_SUBSCRIBE.equals(type)) {
                long pipelineId = projectPipeline.getId();
                DataHubServiceImp.addWebSocketSession(pipelineId, session);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        webSocketSession.close(closeStatus);
        ProjectCompilationService.removeWebSocketSession(webSocketSession);
        DataHubServiceImp.removeWebSocketSession(webSocketSession);
        CodeCheckerService.removeWebSocketSession(webSocketSession);
        DockerfileService.removeWebSocketSession(webSocketSession);
    }

    private void sendLog (long id, String type, String log, WebSocketSession session) throws IOException {
        if (log.equals("")) {
            return;
        }
        Gson gson = new Gson();
        DataMessage msgLog = new DataMessage();
        msgLog.setData(log);
        msgLog.setMsgType(type);
        msgLog.setData(gson.toJson(VulcanusData.builder().id(id).message(log).build()));
        session.sendMessage(new TextMessage(gson.toJson(msgLog)));
    }
}
