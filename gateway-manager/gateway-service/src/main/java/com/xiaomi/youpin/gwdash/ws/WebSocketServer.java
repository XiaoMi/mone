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
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.websocket.*;
//import javax.websocket.server.PathParam;
//import javax.websocket.server.ServerEndpoint;
//import java.io.IOException;
//
//
///**
// * @author tsingfu
// */
//// @ServerEndpoint(value = "/ws/anyone/test")
//// @Component
//@Slf4j
//public class WebSocketServer {
//
//    @OnOpen
//    public void onOpen(Session session, @PathParam("uuid") String uuid) {
//        log.info("WebSocketServer.onOpen: {}", session);
//    }
//
//    @OnClose
//    @OnClose
//    @OnClose
//    public void onCloese (Session session) throws IOException {
//        log.info("WebSocketServer.onCloese");
//        session.close();
//    }
//
//    @OnMessage
//    public void onMessage (String message, Session session) throws IOException {
//        log.info("WebSocketServer.onMessage: {}, {}", message, session);
//        session.getBasicRemote().sendText(message);
//    }
//
//    @OnError
//    public void onError(Session session, Throwable error) {
//        log.error("WebSocketServer.onError: {}, {}", session, error);
//    }
//}
