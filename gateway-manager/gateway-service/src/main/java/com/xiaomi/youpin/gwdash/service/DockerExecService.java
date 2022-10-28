//package com.xiaomi.youpin.gwdash.service;
//
//
//import com.alibaba.nacos.api.config.annotation.NacosValue;
//import com.google.common.collect.Maps;
//import com.xiaomi.youpin.gwdash.common.HttpResult;
//import com.xiaomi.youpin.gwdash.common.HttpUtils;
//import com.xiaomi.youpin.gwdash.ws.DockerExecWebSocketClientHandler;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.concurrent.ListenableFutureCallback;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.client.WebSocketClient;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//
//import java.io.IOException;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @author tsingfu
// */
//@Service
//@Slf4j
//public class DockerExecService {
//
//    @Autowired
//    private ProjectEnvService projectEnvService;
//
//    @NacosValue("${gotty.token:}")
//    private String gottyToken;
//
//    public static Map<String, Map<WebSocketSession, WebSocketSession>> map = new ConcurrentHashMap<>();
//
//    public void initConnection(String userName, WebSocketSession session, String ip, String url) {
//        Map<WebSocketSession, WebSocketSession> defaultGottyMap = new ConcurrentHashMap<WebSocketSession, WebSocketSession>();
//        Map<WebSocketSession, WebSocketSession> gottyMap = map.putIfAbsent(userName, defaultGottyMap);
//        if (null == gottyMap) {
//            gottyMap = defaultGottyMap;
//        }
//        WebSocketClient client = new StandardWebSocketClient();
//        DockerExecWebSocketClientHandler dockerExecWebSocketHandler = new DockerExecWebSocketClientHandler(session, gottyMap, gottyToken);
//        client.doHandshake(dockerExecWebSocketHandler, url.replaceFirst("http", "ws") + "/ws")
//                .addCallback(new ListenableFutureCallback<WebSocketSession>() {
//
//                    @Override
//                    public void onFailure(Throwable throwable) {
//                        log.error("DockerExecService onFailure", throwable);
//                        try {
//                            session.sendMessage(new TextMessage("connect fail"));
//                            session.close();
//                        } catch (IOException e) {}
//                    }
//
//                    @Override
//                    public void onSuccess(WebSocketSession session) {
//                    }
//
//                });
//    }
//
//    public boolean close(String userName, WebSocketSession session) {
//        Map<WebSocketSession, WebSocketSession> gottyMap = map.get(userName);
//        if (null != gottyMap) {
//            if (null != gottyMap.get(session)) {
//                try {
//                    gottyMap.get(session).close();
//                } catch (IOException e) {
//                    log.error("DockerExecService.close", e);
//                }
//            }
//            gottyMap.remove(session);
//        }
//        return true;
//    }
//
//    public void toTyy(String userName, WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) {
//        Map<WebSocketSession, WebSocketSession> res = map.get(userName);
//        WebSocketSession ttyWebSocketSession = res.get(webSocketSession);
//        if (null == ttyWebSocketSession) {
//            return;
//        }
//        try {
//            ttyWebSocketSession.sendMessage(webSocketMessage);
//        } catch (IOException e) {
//            log.error("toTyy", e);
//        }
//    }
//}
