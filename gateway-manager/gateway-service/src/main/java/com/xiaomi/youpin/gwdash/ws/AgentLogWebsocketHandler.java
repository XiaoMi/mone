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
//import com.xiaomi.youpin.gwdash.service.WebSSHService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.*;
//
///**
//
// * @Description: WebSSH的WebSocket处理器
// * @Author: zhangjunyi
// * @Date: 2020/4/16
// */
//@Component
//@Slf4j
//public class AgentLogWebsocketHandler implements WebSocketHandler {
//
//    @Autowired
//    private WebSSHService webSSHService;
//
//    /**
//     * @Description: 用户连接上WebSocket的回调
//     * @Param: [webSocketSession]
//     * @return: void
//     * @Author: Object
//     * @Date: 2020/3/8
//     */
//    @Override
//    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
//        log.info("ws connect:{}", webSocketSession.getAttributes().get("username"));
//        webSSHService.initConnection(webSocketSession);
//    }
//
//
//    /**
//     * @Description: 收到消息的回调
//     * @Param: [webSocketSession, webSocketMessage]
//     * @return: void
//     * @Author: zhangjunyi
//     * @Date: 2020/3/8
//     */
//    @Override
//    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) {
//        if (webSocketMessage instanceof TextMessage) {
//            webSSHService.recvAgentLogHanlde(((TextMessage) webSocketMessage).getPayload(), webSocketSession);
//        } else if (webSocketMessage instanceof BinaryMessage) {
//
//        } else if (webSocketMessage instanceof PongMessage) {
//
//        } else {
//            log.error("Unexpected WebSocket message type: " + webSocketMessage);
//        }
//    }
//
//    /**
//     * @Description: 出现错误的回调
//     * @Param: [webSocketSession, throwable]
//     * @return: void
//     * @Author: Object
//     * @Date: 2020/3/8
//     */
//    @Override
//    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
//        log.error("error:{}", throwable.getMessage());
//    }
//
//
//    /**
//     * @Description: 连接关闭的回调
//     * @Param: [webSocketSession, closeStatus]
//     * @return: void
//     * @Author: zhangjunyi
//     * @Date: 2020/3/8
//     */
//    @Override
//    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
//        log.info("user:{} close  agentSystemLog", webSocketSession.getAttributes().get("username"));
//        webSSHService.close(webSocketSession);
//        webSocketSession.close(closeStatus);
//    }
//
//
//    @Override
//    public boolean supportsPartialMessages() {
//        return false;
//    }
//}