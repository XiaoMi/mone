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
import com.xiaomi.hera.trace.context.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import run.mone.m78.common.WebsocketMessageType;
import run.mone.m78.service.bo.BaseMessage;
import run.mone.m78.service.bo.BaseResult;
import run.mone.m78.service.common.GsonUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public enum WsSessionHolder {

    INSTANCE;

    public static final Map<String, WebSocketSession> SESSIONID_SESSION = new ConcurrentHashMap<>();

    public static final Map<String, String> USER_SESSIONID = new ConcurrentHashMap<>();

    static {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            int sessionSize = SESSIONID_SESSION.size();
            int userSize = USER_SESSIONID.size();
            log.info("websocket memory user size is : " + userSize + ", session size is : " + sessionSize);
        }, 1, 1, TimeUnit.MINUTES);
    }

    public void setUserSession(String user, WebSocketSession session) {
        String sessionId = session.getId();
        USER_SESSIONID.put(user, sessionId);
        SESSIONID_SESSION.put(sessionId, session);
    }

    public void setSessionBySessionId(String sessionId, WebSocketSession session) {
        SESSIONID_SESSION.put(sessionId, session);
    }

    public void sendMessage(String user, BaseMessage chatMessage) {
        if (USER_SESSIONID.containsKey(user)) {
            chatMessage.setTraceId(TraceIdUtil.traceId());
            String sessionId = USER_SESSIONID.get(user);
            this.sendMsgBySessionId(sessionId, GsonUtils.gson.toJson(chatMessage));
        } else {
            log.info("User: {} has logged off.", user);
        }
    }

    public void sendMessage(String user, BaseResult result) {
        if (USER_SESSIONID.containsKey(user)) {
            result.setTraceId(TraceIdUtil.traceId());
            String sessionId = USER_SESSIONID.get(user);
            this.sendMsgBySessionId(sessionId, GsonUtils.gson.toJson(result));
        } else {
            log.info("User: {} has logged off.", user);
        }
    }


    public WebSocketSession getBySessionId(String sessionId) {
        return SESSIONID_SESSION.get(sessionId);
    }

    public WebSocketSession getByUser(String user) {
        String sessionId = USER_SESSIONID.get(user);
        if (sessionId == null) {
            return null;
        }
        return SESSIONID_SESSION.get(sessionId);
    }

    public int getSessionSize() {
        return SESSIONID_SESSION.size();
    }

    public void clearSession(String sessionId, String user) {
        SESSIONID_SESSION.remove(sessionId);
        USER_SESSIONID.remove(user);
    }

    public void clearSessionBySessionId(String sessionId) {
        SESSIONID_SESSION.remove(sessionId);
    }

    public boolean sendMsgBySessionId(String sessionId, String msg, String messageType) {
        return sendMsgBySessionId(sessionId, msg, messageType, "");
    }

    public boolean sendMsgBySessionId(String sessionId, String msg, String messageType, String msgId) {
        WebSocketSession session = getBySessionId(sessionId);
        if (session == null) {
            log.error("send msg by ws, sessionId : " + sessionId + " connection is closed");
            return false;
        }
        try {
            synchronized (session) {
                // FIXME: 当msg为空时,有可能也是大模型的返回...现在是客户端容错会丢弃这条消息
                session.sendMessage(new TextMessage(GsonUtils.addMessageTypeAndMsgId(msg, messageType, msgId)));
            }
            return true;
        } catch (Exception e) {
            log.error("send msg by ws error, ", e);
            return false;
        }
    }


    public boolean sendMessageByWebSocket(String user, JsonObject msg, String messageType) {
        String sessionId = USER_SESSIONID.get(user);
        if (null == sessionId) {
            return false;
        }
        WebSocketSession session = getBySessionId(sessionId);
        if (session == null) {
            log.error("send msg by ws, sessionId : " + sessionId + " connection is closed");
            return false;
        }
        try {
            msg.addProperty(WebsocketMessageType.MESSAGE_TYPE_KEY, messageType);
            // add traceId
            msg.addProperty("traceId", TraceIdUtil.traceId());
            synchronized (session) {
                session.sendMessage(new TextMessage(GsonUtils.gson.toJson(msg)));
            }
            return true;
        } catch (Exception e) {
            log.error("send msg by ws error, ", e);
            return false;
        }
    }

    public boolean sendMessageByWebSocket(String user, Object msg) {
        String sessionId = USER_SESSIONID.get(user);
        if (null == sessionId) {
            return false;
        }
        WebSocketSession session = getBySessionId(sessionId);
        if (session == null) {
            log.error("send msg by ws, sessionId : " + sessionId + " connection is closed");
            return false;
        }
        try {
            synchronized (session) {
                session.sendMessage(new TextMessage(GsonUtils.gson.toJson(msg)));
            }
            return true;
        } catch (Exception e) {
            log.error("send msg by ws error, ", e);
            return false;
        }
    }


    public boolean sendMsgBySessionId(String sessionId, String msg) {
        WebSocketSession session = getBySessionId(sessionId);
        if (session == null) {
            log.error("send msg by ws, sessionId : " + sessionId + " connection is closed");
            return false;
        }
        try {
            synchronized (session) {
                session.sendMessage(new TextMessage(msg));
            }
            return true;
        } catch (Exception e) {
            log.error("send msg by ws error, ", e);
            return false;
        }
    }

}
