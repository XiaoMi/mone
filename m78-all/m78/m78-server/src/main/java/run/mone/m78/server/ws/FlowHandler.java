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
package run.mone.m78.server.ws;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xiaomi.hera.trace.annotation.Trace;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import run.mone.m78.api.bo.flow.FlowOperateParam;
import run.mone.m78.api.bo.flow.FlowStatusStreamParam;
import run.mone.m78.api.bo.flow.FlowTestParam;
import run.mone.m78.api.bo.flow.FlowTestRes;
import run.mone.m78.api.bo.flow.SyncFlowStatus;
import run.mone.m78.common.Constant;
import run.mone.m78.common.WebsocketMessageType;
import run.mone.m78.server.SessionType;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.service.flow.FlowService;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class FlowHandler extends TextWebSocketHandler {

    private int maxConnectionSize;
    private FlowService flowService;

    private static final List<String> FLOW_STREAM_STATUS_URIS = Arrays.asList("/ws/sockjs/flow/status/stream", "/ws/flow/stream/access");

    public FlowHandler(int maxConnectionSize, FlowService flowService) {
        this.maxConnectionSize = maxConnectionSize;
        this.flowService = flowService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 连接建立后的操作
        AuthUserVo vo = (AuthUserVo) session.getAttributes().get("TPC_USER");
        // vo is null close connection
        if (vo == null) {
            log.error("ws connection user is null");
            session.close();
            return;
        }
        // max session
        if (FlowRecordSessionHolder.INSTANCE.getSessionSize() >= maxConnectionSize) {
            log.error("flow ws connection max session is exceed the threshold value : " + maxConnectionSize);
            session.close();
            return;
        }
        String account = vo.getAccount();
        log.info("webhook get account is : " + account);
        session.getAttributes().put(Constant.USER_KEY, account);
    }

    @Trace
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        String uri = session.getUri().getPath();
        try {
            if (uriMatch(FLOW_STREAM_STATUS_URIS, uri)) {
                String user = session.getAttributes().get(Constant.USER_KEY).toString();
                log.info("flow stream user:{}, message: {}", user, message.getPayload());
                try {
                    String operateCmd = getValueFromJson(message.getPayload(), Constant.OPERATE_CMD);
                    if ("testFlow".equals(operateCmd)){
                        FlowTestParam flowTestParam = parseParam(message.getPayload(), FlowTestParam.class);
                        flowTestParam.setUserName(user);
                        //调用test运行
                        Result<FlowTestRes> testRes = flowService.testFlow(flowTestParam);
                        if (testRes.getCode() == 0 && testRes.getData() != null) {
                            FlowRecordSessionHolder.INSTANCE.setRecordIdSession(testRes.getData().getFlowRecordId(), session, SessionType.FLOW);
                            FlowRecordSessionHolder.INSTANCE.sendMsgToRecordId(testRes.getData().getFlowRecordId(),
                                    GsonUtils.gson.toJson(testRes.getData()), WebsocketMessageType.FLOW_EXECUTE_STATUS);
                        } else {
                            log.error("testFlow failed:{}", testRes);
                            session.sendMessage(new TextMessage(
                                    GsonUtils.addMessageType(GsonUtils.gson.toJson(testRes), WebsocketMessageType.FLOW_EXECUTE_FAILURE)));
                        }
                    }

                    if ("getStatus".equals(operateCmd) || StringUtils.isBlank(operateCmd)){
                        FlowStatusStreamParam param = parseParam(message.getPayload(), FlowStatusStreamParam.class);
                        String flowRecordId = param.getFlowRecordId();
                        SyncFlowStatus syncFlowStatusWs = flowService.getSyncFlowStatusWs(user, flowRecordId);
                        String sessionKey = flowRecordId + "_" + user;
                        flowService.addSessionKeyToStatusMap(flowRecordId, sessionKey);
                        log.info("flow handle get sessionKey:{},SyncFlowStatus : {}", sessionKey, syncFlowStatusWs);
                        FlowRecordSessionHolder.INSTANCE.setRecordIdSession(sessionKey, session, SessionType.FLOW);
                        if(syncFlowStatusWs != null) {
                            if(-1 != syncFlowStatusWs.getEndFlowStatus()) {
                                FlowRecordSessionHolder.INSTANCE.sendMsgToRecordId(sessionKey, GsonUtils.gson.toJson(syncFlowStatusWs), WebsocketMessageType.FLOW_EXECUTE_STATUS);
                            }
                        }else{
                            FlowRecordSessionHolder.INSTANCE.sendMsgToRecordId(sessionKey, GsonUtils.gson.toJson(flowService.buildEndStatus()), WebsocketMessageType.FLOW_EXECUTE_STATUS);
                        }
                    }
                    if ("operateFlow".equals(operateCmd)){
                        FlowOperateParam flowOperateParam = parseParam(message.getPayload(), FlowOperateParam.class);
                        flowOperateParam.setUserName(user);
                        if (!flowService.operateFlow(flowOperateParam).getData()){
                            FlowRecordSessionHolder.INSTANCE.setRecordIdSession(flowOperateParam.getFlowRecordId()+"", session, SessionType.FLOW);
                            FlowRecordSessionHolder.INSTANCE.sendMsgToRecordId(
                                    flowOperateParam.getFlowRecordId()+"", GsonUtils.gson.toJson(flowService.buildEndStatus()), WebsocketMessageType.FLOW_EXECUTE_STATUS);
                        }
                    }
                } catch (Exception e) {
                    log.error("flow stream exception", e);
                }
            }
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }


    private String getValueFromJson(String json, String key) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        return jsonObject.has(key) ? jsonObject.get(key).getAsString() : null;
    }


    private boolean uriMatch(List<String> allowUris, String uri) {
        for (String allowUri : allowUris) {
            if (uri.contains(allowUri)) {
                return true;
            }
        }
        return false;
    }

    private <T> T parseParam(String payload, Class<T> clazz) {
        return GsonUtils.gson.fromJson(payload, clazz);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        exception.printStackTrace();
        log.error("ws transport error : ", exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        // 连接关闭后的操作
        FlowRecordSessionHolder.INSTANCE.clearSession(session.getId());
    }

}
