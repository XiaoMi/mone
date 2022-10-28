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
//import com.xiaomi.aegis.utils.AegisSignUtil;
//import com.xiaomi.aegis.vo.UserInfoVO;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.http.server.ServletServerHttpRequest;
//import org.springframework.lang.Nullable;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.server.HandshakeInterceptor;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.Map;
//
///**
// * @author tsingfu
// */
//@Slf4j
//public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {
//
//    private String currentUsePublicKey;
//
//    public WebSocketHandshakeInterceptor(String currentUsePublicKey) {
//        this.currentUsePublicKey = currentUsePublicKey;
//    }
//
//    @Override
//    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
//        HttpServletRequest request = getHttpServletRequest(serverHttpRequest);
//        if (null != request) {
//            String signAndUserSignData = request.getHeader("x-proxy-userdetail");
//            if (StringUtils.isEmpty(signAndUserSignData)) {
//                return false;
//            }
//            log.info("beforeHandshake header x-proxy-userdetail is not empty");
//            String userJson = AegisSignUtil.verifySignGetInfo(signAndUserSignData, currentUsePublicKey);
//            UserInfoVO user = (new Gson()).fromJson(userJson, UserInfoVO.class);
//            if (null != user) {
//                map.put("username", user.getUser());
//                return true;
//            }
//        }
//        log.info("beforeHandshake false");
//        return false;
//    }
//
//    @Override
//    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {}
//
//    @Nullable
//    private HttpServletRequest getHttpServletRequest(ServerHttpRequest request) {
//        if (request instanceof ServletServerHttpRequest) {
//            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest)request;
//            return serverRequest.getServletRequest();
//        } else {
//            return null;
//        }
//    }
//}
