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
public enum KnowledgeSessionHolder {

    INSTANCE;

    public static final Map<String, WebSocketSession> KNOWLEDGE_SESSIONID_SESSION = new ConcurrentHashMap<>();

    public static final Map<String, String> KNOWLEDGE_USER_SESSIONID = new ConcurrentHashMap<>();


    static{
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(()->{
            int sessionSize = KNOWLEDGE_SESSIONID_SESSION.size();
            int userSize = KNOWLEDGE_USER_SESSIONID.size();
            log.info("websocket memory record flow size is : "+userSize +", session size is : "+sessionSize);
        },1, 1 , TimeUnit.MINUTES);
    }

    static {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            int sessionSize = KNOWLEDGE_SESSIONID_SESSION.size();
            int userSize = KNOWLEDGE_USER_SESSIONID.size();
            log.info("websocket memory user size is : " + userSize + ", session size is : " + sessionSize);
        }, 1, 1, TimeUnit.MINUTES);
    }

    public void setSession(WebSocketSession session) {
        String sessionId = session.getId();
        log.info("set session id : {}", sessionId);
        KNOWLEDGE_SESSIONID_SESSION.put(sessionId, session);
    }

    public void setUserSession(String user, WebSocketSession session) {
        String sessionId = session.getId();
        KNOWLEDGE_USER_SESSIONID.put(user, sessionId);
        KNOWLEDGE_SESSIONID_SESSION.put(sessionId, session);
    }

    public void sendMessage(String user, BaseMessage chatMessage) {
        if (KNOWLEDGE_USER_SESSIONID.containsKey(user)) {
            chatMessage.setTraceId(TraceIdUtil.traceId());
            String sessionId = KNOWLEDGE_USER_SESSIONID.get(user);
            this.sendMsgBySessionId(sessionId, GsonUtils.gson.toJson(chatMessage));
        } else {
            log.info("User: {} has logged off.", user);
        }
    }

    public void sendMessage(String user, BaseResult result) {
        if (KNOWLEDGE_USER_SESSIONID.containsKey(user)) {
            result.setTraceId(TraceIdUtil.traceId());
            String sessionId = KNOWLEDGE_USER_SESSIONID.get(user);
            this.sendMsgBySessionId(sessionId, GsonUtils.gson.toJson(result));
        } else {
            log.info("User: {} has logged off.", user);
        }
    }


    public WebSocketSession getBySessionId(String sessionId) {
        return KNOWLEDGE_SESSIONID_SESSION.get(sessionId);
    }

    public WebSocketSession getByUser(String user) {
        String sessionId = KNOWLEDGE_USER_SESSIONID.get(user);
        if (sessionId == null) {
            return null;
        }
        return KNOWLEDGE_SESSIONID_SESSION.get(sessionId);
    }

    public int getSessionSize() {
        return KNOWLEDGE_SESSIONID_SESSION.size();
    }

    public void clearSession(String sessionId, String user) {
        log.info("clear session by sessionId : {}, sessionId exist: {}", sessionId, KNOWLEDGE_SESSIONID_SESSION.containsKey(sessionId));
        KNOWLEDGE_SESSIONID_SESSION.remove(sessionId);
        KNOWLEDGE_USER_SESSIONID.remove(user);
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
        String sessionId = KNOWLEDGE_USER_SESSIONID.get(user);
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
        String sessionId = KNOWLEDGE_USER_SESSIONID.get(user);
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
