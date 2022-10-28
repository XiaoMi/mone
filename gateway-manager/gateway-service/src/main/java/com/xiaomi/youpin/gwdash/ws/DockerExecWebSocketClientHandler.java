//package com.xiaomi.youpin.gwdash.ws;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.socket.*;
//
//import java.util.Map;
//
///**
// * @author tsingfu
// */
//@Slf4j
//public class DockerExecWebSocketClientHandler implements WebSocketHandler {
//
//    private WebSocketSession webSocketSessionClient;
//    private Map<WebSocketSession, WebSocketSession> gottyMap;
//    private String gottyToken;
//
//    public DockerExecWebSocketClientHandler(WebSocketSession webSocketSession, Map<WebSocketSession, WebSocketSession> gottyMap, String gottyToken) {
//        this.webSocketSessionClient = webSocketSession;
//        this.gottyMap = gottyMap;
//        this.gottyToken = gottyToken;
//    }
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
//        log.error("DockerExecWebSocketClientHandler.afterConnectionEstablished");
//        gottyMap.put(this.webSocketSessionClient, webSocketSession);
//        webSocketSession.sendMessage(new TextMessage("{ \"Arguments\": \"\", \"AuthToken\": \"" + gottyToken + "\" }"));
//    }
//
//    @Override
//    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
//        log.error("DockerExecWebSocketClientHandler.handleMessage： {}", webSocketMessage);
//        webSocketSessionClient.sendMessage(webSocketMessage);
//    }
//
//    @Override
//    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
//        log.error("DockerExecWebSocketClientHandler.handleTransportError", throwable);
//        webSocketSession.close();
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
//        log.error("DockerExecWebSocketClientHandler.afterConnectionClosed");
//        webSocketSession.close(closeStatus);
//        webSocketSessionClient.close();
//    }
//
//    @Override
//    public boolean supportsPartialMessages() {
//        return false;
//    }
//}
