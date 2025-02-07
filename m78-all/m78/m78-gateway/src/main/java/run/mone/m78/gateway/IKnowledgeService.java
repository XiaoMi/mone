package run.mone.m78.gateway;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import run.mone.m78.gateway.bo.RecordBO;
import run.mone.m78.gateway.bo.RequestBO;
import run.mone.m78.server.ws.KnowledgeConsumer;

import java.util.List;

public interface IKnowledgeService {
    void queryKnowledge(RequestBO requestBO, String sessionId);

    default void sendWithSessionId(String url, String sessionId, String group, String login, String query, String requestId, List<RecordBO> history) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        WebSocketListener listener = new JSWWebSocketListener(sessionId, group, query, requestId, history, new KnowledgeConsumer());
        WebSocket webSocket = client.newWebSocket(request, listener);
        webSocket.send(login);
        client.dispatcher().executorService().shutdown();
    }
}
