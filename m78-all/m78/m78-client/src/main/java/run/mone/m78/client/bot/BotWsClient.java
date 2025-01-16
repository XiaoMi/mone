package run.mone.m78.client.bot;

import com.google.common.base.Stopwatch;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.apache.commons.lang3.StringUtils;
import run.mone.m78.client.M78Client;
import run.mone.m78.client.model.M78Message;
import run.mone.m78.client.model.M78MessageCategory;
import run.mone.m78.client.model.M78MessageType;
import run.mone.m78.client.model.ClientType;
import run.mone.m78.client.util.Base64Utils;
import run.mone.m78.client.util.GsonUtils;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @author HawickMason@xiaomi.com
 * @date 8/22/24 10:32
 */
@Slf4j
public class BotWsClient implements M78Client {

    private String name;

    private final ClientType type = ClientType.BOT_WS;
    private WebSocket ws;

    private String projectName;

    private String url = "";

    private String token = "";

    private CountDownLatch latch;

    private BotWsClient(Builder builder) {
        name = builder.name;
        projectName = builder.projectName;
        url = builder.url;
        token = builder.token;
        latch = builder.latch;
    }

    public void start(Consumer<M78Message> consumer) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("athena-token", token)
                .build();

        StringBuilder sb = new StringBuilder();

        WebSocketListener listener = new WebSocketListener() {

            Stopwatch sw = Stopwatch.createStarted();

            private AtomicBoolean cancel = new AtomicBoolean(false);


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

                if (StringUtils.isBlank(messageType)) {
                    log.warn("no valid message type, will discard!");
                    return;
                }
                if (messageType.startsWith("BOT")) {
                    dispatchBotMsg(webSocket, messageType, msg);
                } else if (messageType.startsWith("FLOW")) {
                    dispatchFlowMsg(webSocket, messageType, msg);
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

            private void dispatchBotMsg(WebSocket webSocket, String messageType, JsonObject msg) {
                //发生错误了
                if (messageType.equals("BOT_STREAM_FAILURE")) {
                    String message = GsonUtils.get(msg, "message", "");
                    String id = GsonUtils.get(msg, "id", UUID.randomUUID().toString());
                    consumer.accept(M78Message.builder().projectName(projectName).text(message).type(M78MessageType.failure).id(id).build());
                    if (null != latch) {
                        latch.countDown();
                    }
                    return;
                }

                if (messageType.equals("BOT_STREAM_BEGIN")) {
                    String id = GsonUtils.get(msg, "id", UUID.randomUUID().toString());
                    consumer.accept(M78Message.builder().projectName(projectName).text("").type(M78MessageType.begin).id(id).build());
                    return;
                }

                if (messageType.equals("BOT_STREAM_RESULT")) {
                    log.info("BOT_STREAM_RESULT:{}", sb);
                    String id = GsonUtils.get(msg, "id", UUID.randomUUID().toString());
                    consumer.accept(M78Message.builder().projectName(projectName).text(sb.toString()).type(M78MessageType.success).id(id).build());
                    webSocket.close(1000, null);
                    if (null != latch) {
                        latch.countDown();
                    }
                    return;
                }

                if (messageType.equals("BOT_STREAM_EVENT")) {
                    String message = GsonUtils.get(msg, "message", "");
                    //解决中文乱码的问题
                    message = Base64Utils.decodeBase64String(message);
                    log.info("message:{}", message);
                    sb.append(message);
                    //如果被取消了,则不再追加内容了
                    if (!this.cancel.get()) {
                        String id = GsonUtils.get(msg, "id", UUID.randomUUID().toString());
                        consumer.accept(M78Message.builder().projectName(projectName).code(false).text(message).type(M78MessageType.process).id(id).build());
                    }
                }

                if (messageType.equals("BOT_RESULT")) {
                    String message = GsonUtils.get(msg, "data", "");
                    log.info("message:{}", message);
                    if (!this.cancel.get()) {
                        String id = GsonUtils.get(msg, "id", UUID.randomUUID().toString());
                        consumer.accept(M78Message.builder().projectName(projectName).code(false).text("").type(M78MessageType.begin).id(id).build());
                        consumer.accept(M78Message.builder().projectName(projectName).code(false).text(message).type(M78MessageType.process).id(id).build());
                        consumer.accept(M78Message.builder().projectName(projectName).code(false).text("").type(M78MessageType.success).id(id).build());
                    }
                }
            }

            private void dispatchFlowMsg(WebSocket webSocket, String messageType, JsonObject msg) {
                if (messageType.equals("FLOW_EXECUTE_STATUS")
                        || messageType.equals("FLOW_EXECUTE_FAILURE")) {
                    String message = msg.toString();
                    String id = GsonUtils.get(msg, "id", UUID.randomUUID().toString());
                    consumer.accept(M78Message.builder()
                            .projectName(projectName)
                            .text(message)
                            .category(M78MessageCategory.flow)
                            .type(M78MessageType.process)
                            .id(id)
                            .build());
                    if (null != latch) {
                        latch.countDown();
                    }
                    return;
                }

                if (messageType.equals("FLOW_EXECUTE_MESSAGE")) {
                    // NOT USED FOR NOW
                }
            }
        };
        ws = client.newWebSocket(request, listener);
        log.info("init finish");
    }


    //发送消息
    public void send(JsonObject req) {
        ws.send(req.toString());
    }

    //发送消息 - 1
    public void send(String req) {
        ws.send(req);
    }

    @Override
    public String getName() {
        return StringUtils.isNotBlank(name) ? name : type.getTypeName();
    }

    @Override
    public ClientType getClientType() {
        return type;
    }

    public static BotWsClient.Builder builder() {
        return new BotWsClient.Builder();
    }


    public static final class Builder {
        private String name;
        private String projectName;
        private String url = "";
        private String token;
        private CountDownLatch latch;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder projectName(String val) {
            projectName = val;
            return this;
        }

        public Builder url(String val) {
            url = val;
            return this;
        }

        public Builder token(String val) {
            token = val;
            return this;
        }

        public Builder latch(CountDownLatch val) {
            latch = val;
            return this;
        }

        public BotWsClient build() {
            return new BotWsClient(this);
        }
    }
}
