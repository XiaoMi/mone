package run.mone.agentx.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
public class WebSocketHolder {
    public static volatile WebSocketSession session;

    public static void sendMessage(String message) {
        if (session != null) {
            synchronized (session) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (Exception e) {
                    log.error("send message error", e);
                }
            }
        }
    }
}