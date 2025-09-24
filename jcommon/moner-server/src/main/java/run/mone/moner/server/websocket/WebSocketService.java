
package run.mone.moner.server.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class WebSocketService {

    @Autowired
    private WebSocketHandler webSocketHandler;

    public void sendMessageToAllClients(String message) {
        try {
            webSocketHandler.sendMessageToAll(message);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
