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

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import run.mone.m78.server.SessionType;
import run.mone.m78.service.common.GsonUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public enum FlowRecordSessionHolder {

    INSTANCE;

    private static final Map<String, WebSocketSession> SESSIONID_SESSION = new ConcurrentHashMap<>();
    private static final Map<String, String> RECORD_SESSIONID = new ConcurrentHashMap<>();


    static{
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(()->{
            int sessionSize = SESSIONID_SESSION.size();
            int userSize = RECORD_SESSIONID.size();
            log.info("websocket memory record flow size is : " + userSize + ", session size is : " + sessionSize);
        },1, 1 , TimeUnit.MINUTES);
    }

    public void setRecordIdSession(String recordId, WebSocketSession session, SessionType type) {
        String sessionId = session.getId();
        log.info("flow holder set, recordId : {}, sessionId : {}, type : {}", recordId, sessionId, type.getType());
        RECORD_SESSIONID.put(recordId, sessionId);
        SESSIONID_SESSION.put(sessionId, session);
    }

    public WebSocketSession getBySessionId(String sessionId) {
        return SESSIONID_SESSION.get(sessionId);
    }

    public WebSocketSession getByRecordId(String recodeId) {
        String sessionId = RECORD_SESSIONID.get(recodeId);
        if (sessionId == null) {
            return null;
        }
        return SESSIONID_SESSION.get(sessionId);
    }

    public int getSessionSize(){
        return SESSIONID_SESSION.size();
    }

    public void clearSession(String sessionId) {
        log.info("clear recordId and session : " + sessionId);
        SESSIONID_SESSION.remove(sessionId);
        for (Map.Entry<String, String> entry : RECORD_SESSIONID.entrySet()) {
            if (entry.getValue().equals(sessionId)) {
                RECORD_SESSIONID.remove(entry.getKey());
            }
        }
    }

    public void clearRecord(String recordId){
        log.info("clear recordId and session : " + recordId);
        String sessionId = RECORD_SESSIONID.get(recordId);
        if(sessionId != null) {
            RECORD_SESSIONID.remove(recordId);
            SESSIONID_SESSION.remove(sessionId);
        }
    }

    public boolean sendMsgToRecordId(String recordId, String msg, String messageType) {
        WebSocketSession session = getByRecordId(recordId);
        if (session == null) {
            log.error("send msg by ws, recordId : " + recordId + " connection is closed");
            return false;
        }
        try {
            synchronized (session) {
                session.sendMessage(new TextMessage(GsonUtils.addMessageType(msg, messageType)));
                log.info("websocket send msg : " + msg);
            }
            return true;
        } catch (IOException e) {
            log.error("send msg by ws error, ", e);
            return false;
        }
    }

}
