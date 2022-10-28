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
//package com.xiaomi.youpin.gwdash.service.impl;
//
//import com.google.gson.Gson;
//import com.xiaomi.youpin.gwdash.bo.BroadCastData;
//import com.xiaomi.youpin.gwdash.bo.BroadcastConnectInfo;
//import com.xiaomi.youpin.gwdash.common.MSafe;
//import com.xiaomi.youpin.gwdash.service.BroadcastService;
//import com.xiaomi.youpin.gwdash.service.UserService;
//import com.xiaomi.youpin.hermes.bo.response.Account;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.compress.utils.Lists;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//
//import java.io.IOException;
//import java.lang.reflect.Type;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.stream.Collectors;
//
///**
// * @author zhangjunyi
// * created on 2020/5/14 2:37 下午
// * @modify zhangzhiyong
// * modify on 2020-05-29
// */
//@Slf4j
//@Service
//public class BroadcastServiceImpl implements BroadcastService {
//
//    private static Map<String, BroadcastConnectInfo> userConnectionMap = new ConcurrentHashMap<>();
//
//    @Autowired
//    private UserService userService;
//
//
//    /**
//     * 同时在线用户数量在20左右,就直接遍历了,如果以后同时在线用户大于1000,这里需要优化
//     *
//     * @param userName
//     * @param message
//     */
//    @Override
//    public void sendMsg(String userName, String message) {
//        MSafe.execute(() -> {
//            userConnectionMap.values().stream().filter(it -> it.getName().equals(userName)).findAny().ifPresent(it -> {
//                WebSocketSession session = it.getWebSocketSession();
//                if (null != session && session.isOpen()) {
//                    try {
//                        session.sendMessage(new TextMessage(message));
//                    } catch (IOException e) {
//                        log.error("sendMsg error:{}", e.getMessage());
//                    }
//                }
//            });
//        });
//
//    }
//
//    @Override
//    public void initConnection(WebSocketSession session) throws IOException {
//        BroadcastConnectInfo broadcastConnectInfo = new BroadcastConnectInfo();
//        broadcastConnectInfo.setWebSocketSession(session);
//        broadcastConnectInfo.setId(session.getId());
//        //hermes username
//        String uid = String.valueOf(session.getAttributes().get("username"));
//        if (StringUtils.isEmpty(uid)) {
//            log.info("uid is empty");
//            session.close();
//            return;
//        }
//
//        //前端的问题,造成需要断开之前的连接(服务器自我保护)
//        List<WebSocketSession> sessionList = userConnectionMap.entrySet().stream().filter(it -> it.getValue().getUid().equals(uid))
//                .map(it -> it.getValue().getWebSocketSession()).collect(Collectors.toList());
//
//        sessionList.stream().forEach(it -> {
//            log.warn("init close session:{}", it);
//            safeClose(it);
//        });
//
//
//        broadcastConnectInfo.setName(uid);
//        broadcastConnectInfo.setUid(uid);
//        Account account = userService.queryUserByName(uid);
//        log.info("init connection session id:{} {} {}", session.getId(), uid, account);
//        BroadCastData data = new BroadCastData();
//        data.setMessage(account.getName() + "已连接");
//        data.setOperation("connected");
//        data.setUsername(account.getUserName());
//        data.setName(account.getName());
//        userConnectionMap.putIfAbsent(session.getId(), broadcastConnectInfo);
//        log.info("broadcast  users.size:{}", userConnectionMap.size());
//        this.sendBroadCastData(data);
//    }
//
//    @Override
//    public void recvHandle(String buffer, WebSocketSession session) throws IOException {
//        BroadCastData data = new Gson().fromJson(buffer, (Type) BroadCastData.class);
//        String uid = String.valueOf(session.getAttributes().get("username"));
//        Account account = userService.queryUserByName(uid);
//        data.setOperation("receive");
//        data.setName(account.getName());
//        data.setUsername(account.getUserName());
//        this.pass2Users(new Gson().toJson(data, BroadCastData.class));
//    }
//
//    public void pass2Users(String buffer) {
//        List<String> brokenIds = Lists.newArrayList();
//        userConnectionMap.forEach((k, broadcastConnectInfo) -> {
//            WebSocketSession oneSession = broadcastConnectInfo.getWebSocketSession();
//            if (oneSession.isOpen()) {
//                try {
//                    this.sendMessage(broadcastConnectInfo.getWebSocketSession(), buffer.getBytes());
//                } catch (IOException e) {
//                    log.error("BroadcastServiceImpl pass2Users:" + e.getMessage(), e);
//                }
//            } else {
//                brokenIds.add(k);
//                try {
//                    broadcastConnectInfo.getWebSocketSession().close();
//                } catch (IOException e) {
//                }
//            }
//        });
//        brokenIds.stream().forEach(it -> userConnectionMap.remove(it));
//    }
//
//    @Override
//    public void sendMessage(WebSocketSession session, byte[] buffer) throws IOException {
//        if (session.isOpen()) {
//            session.sendMessage(new TextMessage(buffer));
//        } else {
//            userConnectionMap.remove(session.getId());
//            log.info("BroadcastServiceImpl sendMessage close: {}", session.getId());
//        }
//    }
//
//    public void sendBroadCastData(BroadCastData data) throws IOException {
//        String msgJson = new Gson().toJson(data, BroadCastData.class);
//        this.pass2Users(msgJson);
//    }
//
//    @Override
//    public void close(WebSocketSession session) throws IOException {
//        log.info("close session id:{} size:{}", session.getId(), userConnectionMap.size());
//        safeClose(session);
//        try {
//            String uid = String.valueOf(session.getAttributes().get("username"));
//            Account account = userService.queryUserByName(uid);
//            log.info("close connection session id:{} {}", session.getId(), uid);
//            BroadCastData data = new BroadCastData();
//            data.setMessage(account.getName() + "已断开连接");
//            data.setOperation("disconnected");
//            data.setUsername(account.getUserName());
//            this.sendBroadCastData(data);
//        } catch (Throwable e) {
//            log.error("BroadcastServiceImpl close:" + e.getMessage(), e);
//        }
//    }
//
//
//    private void safeClose(WebSocketSession session) {
//        try {
//            userConnectionMap.remove(session.getId());
//            session.close();
//        } catch (Throwable ex) {
//            log.error("close error:{}", ex.getMessage());
//        }
//    }
//}