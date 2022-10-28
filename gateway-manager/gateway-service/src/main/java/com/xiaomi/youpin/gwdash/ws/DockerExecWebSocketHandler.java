//package com.xiaomi.youpin.gwdash.ws;
//
//import com.google.gson.Gson;
//import com.xiaomi.youpin.gwdash.bo.DockerContainerParam;
//import com.xiaomi.youpin.gwdash.service.DockerExecService;
//import com.xiaomi.youpin.gwdash.service.ProjectEnvService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.*;
//
//import java.util.Base64;
//
///**
// * @author tsingfu
// */
//@Component
//@Slf4j
//public class DockerExecWebSocketHandler implements WebSocketHandler {
//
//    @Autowired
//    private DockerExecService dockerExecService;
//
//    @Autowired
//    private ProjectEnvService projectEnvService;
//
//    public final static String InitCode = "9";
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
//        log.info("DockerExecWebSocketHandler.afterConnectionEstablished: {}", webSocketSession.getAttributes().get("username"));
//        webSocketSession.sendMessage(new TextMessage(InitCode));
//    }
//
//    @Override
//    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
//        log.info("DockerExecWebSocketHandler.handleMessage: {}, {}", webSocketSession.getAttributes().get("username"), webSocketMessage);
//        if (webSocketMessage.getPayload() instanceof String) {
//            String payload = (String) webSocketMessage.getPayload();
//            if (StringUtils.isNotEmpty(payload) && payload.startsWith(InitCode)) {
//                DockerContainerParam dockerContainerParam = (new Gson()).fromJson(payload.substring(1), DockerContainerParam.class);
//                if (null == dockerContainerParam
//                        || 0 == dockerContainerParam.getEnvId()
//                        || StringUtils.isEmpty(dockerContainerParam.getIp())) {
//                    webSocketSession.sendMessage(new TextMessage("1" + Base64.getEncoder().encodeToString("connect fail".getBytes())));
//                    webSocketSession.close();
//                    return;
//                }
//                String url = projectEnvService.getDirectContainerUrl(dockerContainerParam).getData();
//                if (StringUtils.isEmpty(url)) {
//                    webSocketSession.sendMessage(new TextMessage("1" + Base64.getEncoder().encodeToString("connect fail".getBytes())));
//                    webSocketSession.close();
//                    return;
//                }
//                dockerExecService.initConnection((String) webSocketSession.getAttributes().get("username"), webSocketSession, dockerContainerParam.getIp(), url);
//                return;
//            }
//        }
//        dockerExecService.toTyy((String) webSocketSession.getAttributes().get("username"), webSocketSession, webSocketMessage);
//    }
//
//    @Override
//    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
//        log.error("DockerExecWebSocketHandler.handleTransportError", throwable);
//        webSocketSession.close();
//        dockerExecService.close((String) webSocketSession.getAttributes().get("username"), webSocketSession);
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
//        log.info("DockerExecWebSocketHandler.afterConnectionClosed: {}", webSocketSession.getAttributes().get("username"));
//        webSocketSession.close(closeStatus);
//        dockerExecService.close((String) webSocketSession.getAttributes().get("username"), webSocketSession);
//    }
//
//    @Override
//    public boolean supportsPartialMessages() {
//        return false;
//    }
//}
