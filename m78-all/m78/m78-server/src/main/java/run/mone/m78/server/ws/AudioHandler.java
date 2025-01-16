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

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaomi.hera.trace.annotation.Trace;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import run.mone.m78.api.bo.multiModal.audio.AsrRecognizedError;
import run.mone.m78.api.bo.multiModal.audio.AsrRecognizedRes;
import run.mone.m78.common.Constant;
import run.mone.m78.common.URIParser;
import run.mone.m78.common.WebsocketMessageType;
import run.mone.m78.service.asr.AsrResponse;
import run.mone.m78.service.asr.tencent.TencentAsrService;
import run.mone.m78.service.asr.xiaoai.XiaoAiService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AudioHandler extends AbstractWebSocketHandler {

    private int maxConnectionSize;

    private static final List<String> MULTI_MODAL_AUDIO_URIS = Arrays.asList("/ws/sockjs/multiModal/audio", "/ws/multiModal/audio");


    private List<WebSocketSession> sessions = new ArrayList<>();

    private TencentAsrService tencentAsrService;
    private XiaoAiService xiaoAiService;


    private static final String VENDOR_XIAOAI="xiaoai";
    private static final String VENDOR_TENCENT="tencent";
    private static final String VENDOR="vendor";

    public AudioHandler(int maxConnectionSize, TencentAsrService tencentAsrService, XiaoAiService xiaoAiService) {
        this.maxConnectionSize = maxConnectionSize;
        this.tencentAsrService = tencentAsrService;
        this.xiaoAiService = xiaoAiService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("websocket connection url query :{}", session.getUri().getQuery());
        // 连接建立后的操作
        log.info("websocket connection established enter,session_id:{}", session.getId());

        // max session
        if (WsSessionHolder.INSTANCE.getSessionSize() >= maxConnectionSize) {
            AsrRecognizedRes res = AsrRecognizedRes.builder().code(AsrRecognizedError.ASR_HANDSHAKE_CONN_LIMIT.getCode()).message("fail").build();
            WsSessionHolder.INSTANCE.sendMsgBySessionId(session.getId(), JSON.toJSONString(res) , WebsocketMessageType.MULTI_MODAL_AUDIO_STREAM_FAIL_MESSAGE, "");
            TimeUnit.MILLISECONDS.sleep(500);
            log.error("ws connection max session is exceed the threshold value : " + maxConnectionSize);
            session.close();
            return;
        }
        String vendor = URIParser.getQueryParamValue(session.getUri(), VENDOR);
        // 需要提前存储
        WsSessionHolder.INSTANCE.setSessionBySessionId(session.getId(), session);
        try{
            String scene = URIParser.getQueryParamValue(session.getUri(), "scene");
            //给老版本一个错误，1个月后可以删除
            //判断是否可以握手，如果不通过就1️以json格式返回错误
            if ("xiaoai".equals(vendor) && StringUtils.isBlank(scene)) {
                AsrResponse.sendMessageBySessionId(session.getId(), 426,"识别当前语音版本较低，请退出APP重试",WebsocketMessageType.MULTI_MODAL_AUDIO_STREAM_FAIL_MESSAGE);
                TimeUnit.MILLISECONDS.sleep(500);
                session.close();
                return ;
            }
            if(VENDOR_XIAOAI.equals(vendor)){
                xiaoAiService.doConnect(session);
            }else if(VENDOR_TENCENT.equals(vendor)){
                // 创建连接到腾讯的asr websocket
                tencentAsrService.doConnect(session);
            }else{
                log.error("vendor is not support");
                session.close();
            }
        }catch (Exception e){
            AsrRecognizedRes res = AsrRecognizedRes.builder().code(AsrRecognizedError.ASR_ESTABLISH_FAILED.getCode()).message("fail").build();
            WsSessionHolder.INSTANCE.sendMsgBySessionId(session.getId(), JSON.toJSONString(res) , WebsocketMessageType.MULTI_MODAL_AUDIO_STREAM_RESULT, "");
            log.error("websocket connection error,sessionId={}",session.getId(),e);
            WsSessionHolder.INSTANCE.clearSessionBySessionId(session.getId());
            throw e;
        }


    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try{
            String uri = session.getUri().getPath();
            String vendor = URIParser.getQueryParamValue(session.getUri(), VENDOR);
            if (uriMatch(MULTI_MODAL_AUDIO_URIS, uri)) {
                if(VENDOR_XIAOAI.equals(vendor)){
                    xiaoAiService.handler(session,message);
                }else if(VENDOR_TENCENT.equals(vendor)){
                    tencentAsrService.writeTextMessage(message, session.getId());
                }
            }
        }catch (Exception e){
            log.error("sessionId={},handle text message Error: {}",session.getId(), e.getMessage());
            throw e;
        }
    }


    @Trace
    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        String uri = session.getUri().getPath();
        String vendor = URIParser.getQueryParamValue(session.getUri(), VENDOR);
        try {
            if (uriMatch(MULTI_MODAL_AUDIO_URIS, uri)) {
                if (VENDOR_XIAOAI.equals(vendor)){
                    xiaoAiService.writeBinaryMessage(session,message);
                }else if(VENDOR_TENCENT.equals(vendor)){
                    tencentAsrService.writeBinaryMessage(message, session.getId());
                }
            }
        } catch (Exception e) {
            log.error("handle binary message Error: {}", e.getMessage());
        }
    }


    private boolean uriMatch(List<String> allowUris, String uri) {
        for (String allowUri : allowUris) {
            if (uri.contains(allowUri)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        exception.printStackTrace();
        log.error("WebSocket transport error for session ID: {}, URI: {}, IP: {}",
                session.getId(), session.getUri(), session.getRemoteAddress());
//        // 尝试关闭会话
//        if (session.isOpen()) {
//            try {
//                session.close(CloseStatus.SERVER_ERROR);
//            } catch (IOException e) {
//                log.error("Error closing WebSocket session: ", e);
//            }
//        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.warn("websocket afterConnectionClosed, session_id={},uri={}", session.getId(),session.getUri());
        String uri = session.getUri().getPath();
        String vendor = URIParser.getQueryParamValue(session.getUri(), VENDOR);
        if (uriMatch(MULTI_MODAL_AUDIO_URIS, uri)) {
            if(VENDOR_XIAOAI.equals(vendor)){
                xiaoAiService.close(session);
            }else if(VENDOR_TENCENT.equals(vendor)){
                tencentAsrService.closeAndClearTencentWs(session.getId());
            }
        }
        // 连接关闭后的操作
        WsSessionHolder.INSTANCE.clearSessionBySessionId(session.getId());
    }


}
