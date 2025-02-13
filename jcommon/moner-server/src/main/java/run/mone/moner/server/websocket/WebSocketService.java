
package run.mone.moner.server.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WebSocketService {

    @Autowired
    private WebSocketHandler webSocketHandler;

    public void sendMessageToAllClients(String message) {
        try {
            webSocketHandler.sendMessageToAll(message);
        } catch (IOException e) {
            // Handle the exception (e.g., log it)
            e.printStackTrace();
        }
    }
}
