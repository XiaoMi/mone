package run.mone.ultraman.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import okhttp3.*;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2024/6/19 17:06
 */
public class BotTest {

    @SneakyThrows
    @Test
    public void postBotQuery() throws IOException {
        String url = "";

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("botId", "100336");
        jsonMap.put("userName", "zzy");
        jsonMap.put("input", "");
        jsonMap.put("params", ImmutableMap.of("input","hi"));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(jsonMap);

        RequestBody body = RequestBody.create(JSON, jsonString);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            System.out.println(response.body().string());
        }
    }


    @SneakyThrows
	@Test
	public void websocketBot() {
        String url = "";

	    OkHttpClient client = new OkHttpClient();

        JsonObject jsonReq = new JsonObject();
        jsonReq.addProperty("botId", "");
        jsonReq.addProperty("input", "hi");
        jsonReq.addProperty("topicId", UUID.randomUUID().toString());

	    Request request = new Request.Builder()
	            .url(url)
                .header("athena-token", "")
	            .build();

	    WebSocketListener listener = new WebSocketListener() {
	        @Override
	        public void onOpen(WebSocket webSocket, Response response) {
	            System.out.println("WebSocket opened: " + response);
	        }

	        @Override
	        public void onMessage(WebSocket webSocket, String text) {
	            System.out.println("Received message: " + text);
	            // 处理返回的信息
	        }

	        @Override
	        public void onClosing(WebSocket webSocket, int code, String reason) {
	            webSocket.close(1000, null);
	            System.out.println("WebSocket closing: " + code + " / " + reason);
	        }

	        @Override
	        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
	            t.printStackTrace();
	        }
	    };

	    WebSocket webSocket = client.newWebSocket(request, listener);

        webSocket.send(jsonReq.toString());

        TimeUnit.SECONDS.sleep(40);
	    client.dispatcher().executorService().shutdown();
	}

}
