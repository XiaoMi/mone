package run.mone.m78.gateway;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import run.mone.m78.gateway.bo.*;
import run.mone.m78.service.common.GsonUtils;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class PrivateKnowledgeWebSocketListener extends WebSocketListener {

    public String sessionId;

    private Consumer<JsonObject> consumer;

    public PrivateKnowledgeWebSocketListener(String sessionId, Consumer<JsonObject> consumer) {
        this.sessionId = sessionId;
        this.consumer = consumer;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        try {
            JsonObject jsonObject = GsonUtils.gson.fromJson(text, JsonObject.class);
            jsonObject.addProperty("gatewayType", "privateKnowledge");
            jsonObject.addProperty("sessionId", sessionId);
            jsonObject.addProperty("originalAction", "query");
            JsonObject msg = new JsonObject();
            msg.addProperty("msg", jsonObject.toString());
            msg.addProperty("sessionId", sessionId);
            consumer.accept(msg);
            if (jsonObject.get("data") != null) {
                int status = jsonObject.get("data").getAsJsonObject().get("status").getAsInt();
                if (status == 200) {
                    webSocket.close(1000, "Bye bye");
                }
            }
        } catch (Exception e) {
            log.error("knowledge onMessage error: ", e);
        }

    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        log.warn("knowledge webSocket closing: " + reason);
        webSocket.close(code, reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        log.error("knowledge webSocket error: ", t);
    }
}
