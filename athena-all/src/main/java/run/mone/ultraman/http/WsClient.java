package run.mone.ultraman.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xiaomi.youpin.tesla.ip.bo.AiMessage;
import com.xiaomi.youpin.tesla.ip.bo.AiMessageType;
import com.xiaomi.youpin.tesla.ip.common.Base64Utils;
import com.xiaomi.youpin.tesla.ip.common.ConfigUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import run.mone.ultraman.common.GsonUtils;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2024/5/29 18:20
 */
@Slf4j
@Data
public class WsClient {

    private WebSocket ws;

    private String id;

    private String projectName;

    private String url = "ws://127.0.0.1/ws/bot/abc";

    private CountDownLatch latch;

    public void init(Consumer<AiMessage> consumer) {
        OkHttpClient client = new OkHttpClient();
        String token = ConfigUtils.getConfig().getzToken();
        Request request = new Request.Builder()
                .url(url)
                .header("athena-token", token)
                .build();
        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                log.info("ws open");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                log.info("Received:{}", text);
                JsonObject msg = JsonParser.parseString(text).getAsJsonObject();
                String type = GsonUtils.get(msg, "type", "");
                String messageType = GsonUtils.get(msg, "messageType", "");

                //发生错误了
                if (messageType.equals("BOT_STREAM_FAILURE")) {
                    String message = GsonUtils.get(msg, "message", "");
                    consumer.accept(AiMessage.builder().projectName(projectName).text(message).type(AiMessageType.failure).id(id).build());
                    if (null != latch) {
                        latch.countDown();
                    }
                    return;
                }

                if (messageType.equals("BOT_STREAM_BEGIN")) {
                    consumer.accept(AiMessage.builder().projectName(projectName).text("").type(AiMessageType.begin).id(id).build());
                    return;
                }

                if (messageType.equals("BOT_STREAM_RESULT")) {
                    consumer.accept(AiMessage.builder().projectName(projectName).text("").type(AiMessageType.success).id(id).build());
                    webSocket.close(1000, null);
                    if (null != latch) {
                        latch.countDown();
                    }
                    return;
                }

                String message = GsonUtils.get(msg, "message", "");

                if (messageType.equals("BOT_STREAM_EVENT")) {
                    //解决中文乱码的问题
                    message = Base64Utils.decodeBase64String(message);
                    log.info("message:{}", message);
                    consumer.accept(AiMessage.builder().projectName(projectName).code(false).text(message).type(AiMessageType.process).id(id).build());
                }

            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(1000, null);
                log.info("Closing: " + code + " / " + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                log.error("Error: " + t.getMessage());
            }
        };
        ws = client.newWebSocket(request, listener);
        log.info("init finish");
    }


    //发送消息
    public void send(JsonObject req) {
        ws.send(req.toString());
    }


}
