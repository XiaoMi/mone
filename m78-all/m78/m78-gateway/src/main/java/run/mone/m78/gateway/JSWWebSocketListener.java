package run.mone.m78.gateway;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.apache.commons.lang3.StringUtils;
import run.mone.m78.gateway.bo.QueryBO;
import run.mone.m78.gateway.bo.RecordBO;
import run.mone.m78.service.common.GsonUtils;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
public class JSWWebSocketListener extends WebSocketListener {

    public String sessionId;

    public String query;

    public String group;

    public String requestId;

    public List<RecordBO> history;

    private Consumer<JsonObject> consumer;

    public JSWWebSocketListener(String sessionId, String group, String query, String requestId, List<RecordBO> history, Consumer<JsonObject> consumer) {
        this.sessionId = sessionId;
        this.group = group;
        this.query = query;
        this.requestId = requestId;
        this.consumer = consumer;
        this.history = history;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        try {
            JsonObject jsonObject = GsonUtils.gson.fromJson(text, JsonObject.class);
            jsonObject.addProperty("gatewayType", "jsw");
            if (jsonObject.get("originalAction").getAsString().equals("login")) {
                requestId = StringUtils.isEmpty(requestId) ? UUID.randomUUID().toString() : requestId;
                QueryBO queryBO = QueryBO.builder().query(query)
                        .group(group)
                        .sessionId(jsonObject.get("sessionId").getAsString())
                        .action("query").from("manual")
                        .requestId(requestId)
                        .history(history)
                        .messageId(UUID.randomUUID().toString()).build();
                webSocket.send(GsonUtils.gson.toJson(queryBO));
            } else if (jsonObject.get("originalAction").getAsString().equals("query")){
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
