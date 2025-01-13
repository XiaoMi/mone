package run.mone.m78.client.flow;

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
import run.mone.m78.client.model.ClientType;
import run.mone.m78.client.model.M78FlowCMD;
import run.mone.m78.client.model.M78FlowOperateType;
import run.mone.m78.client.model.M78FlowReq;
import run.mone.m78.client.model.M78Message;
import run.mone.m78.client.model.M78MessageCategory;
import run.mone.m78.client.model.M78MessageType;
import run.mone.m78.client.util.GsonUtils;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @author HawickMason@xiaomi.com
 * @date 8/11/24 17:47
 */
@Slf4j
public class FlowWsClient implements M78Client {

    private String name = ClientType.FLOW_WS.getTypeName();

    private ClientType type = ClientType.FLOW_WS;

    private WebSocket ws;

    private String projectName;

    private String url = "";


    private CountDownLatch latch;

    private FlowWsClient(Builder builder) {
        projectName = builder.projectName;
        url = builder.url;
        latch = builder.latch;
    }

    /**
     * 初始化WebSocket连接并处理消息
     *
     * @param consumer 消息消费者，用于处理接收到的消息
     * @param userName 用户名，用于请求头中的x-account字段
     */
    public void start(Consumer<M78Message> consumer, String userName) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("x-account", userName)
                .build();

        StringBuilder sb = new StringBuilder();

        WebSocketListener listener = new WebSocketListener() {

            Stopwatch sw = Stopwatch.createStarted();

            private AtomicBoolean cancel = new AtomicBoolean(false);


            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                log.info("ws open");
                onClientInit(webSocket, response, consumer);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                log.info("Received:{}", text);
                onMessageReceived(webSocket, text, consumer);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                log.info("M78WsClient Closing, code: {}, reason:{}", code, reason);
                onClientClose(webSocket, code, reason, consumer);
                webSocket.close(1000, null);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                onClientError(webSocket, t, response, consumer);
                log.error("M78WsClient Error: {}, response:{}", t.getMessage(), response.toString());
            }
        };
        ws = client.newWebSocket(request, listener);
        log.info("init finish");
    }


    /**
     * 客户端初始化方法，可以被重写
     *
     * @param webSocket WebSocket对象
     * @param response  响应对象
     * @param consumer  消费者对象，用于处理M78Message消息
     */
    public void onClientInit(WebSocket webSocket, Response response, Consumer<M78Message> consumer) {
        // you may override this
    }

    /**
     * 处理接收到的WebSocket消息, 可以被重写
     * 默认行为是将消息处理委托给 Consumer<M78Message> consumer
     *
     * @param webSocket WebSocket对象
     * @param text      接收到的消息文本
     * @param consumer  消费处理M78Message对象的消费者
     */
    public void onMessageReceived(WebSocket webSocket, String text, Consumer<M78Message> consumer) {
        // you may override this
        JsonObject msg = JsonParser.parseString(text).getAsJsonObject();
        consumer.accept(M78Message.builder()
                .text(msg.toString())
                .category(M78MessageCategory.flow)
                .type(M78MessageType.process)
                .build());
    }

    /**
     * 当客户端关闭连接时调用此方法, 可以被重写
     *
     * @param webSocket WebSocket对象，表示客户端连接
     * @param code      关闭连接的状态码
     * @param reason    关闭连接的原因
     * @param consumer  消费者对象，用于处理M78Message消息
     */
    public void onClientClose(WebSocket webSocket, int code, String reason, Consumer<M78Message> consumer) {
        // you may override this
    }

    /**
     * 处理WebSocket客户端错误的回调方法，可以被重写
     *
     * @param webSocket WebSocket对象
     * @param t         异常对象
     * @param response  响应对象
     * @param consumer  消费者对象，用于处理M78Message
     */
    public void onClientError(WebSocket webSocket, Throwable t, Response response, Consumer<M78Message> consumer) {
        // you may override this
    }

    public void executeFlow(String userName, String flowId, Map<String, Object> inputs) {
        try {
            M78FlowReq req = M78FlowReq.builder()
                    .userName(userName)
                    .flowId(flowId)
                    .inputs(inputs)
                    .operateCmd(M78FlowOperateType.TEST_FLOW)
                    .build();
            operateFlow(req);
        } catch (Throwable e) {
            log.error("Error executing flow:", e);
        }
    }

    public void resumeFlow(String userName, String flowId, String flowRecordId) {
        try {
            M78FlowReq req = M78FlowReq.builder()
                    .userName(userName)
                    .flowId(flowId)
                    .flowRecordId(flowRecordId)
                    .operateCmd(M78FlowOperateType.OPERATE_FLOW)
                    .cmd(M78FlowCMD.MANUAL_CONFIRM_FLOW)
                    .build();
            operateFlow(req);
        } catch (Throwable e) {
            log.error("resumeFlow error:", e);
        }
    }

    public void terminateFlow(String userName, String flowId, String flowRecordId) {
        try {
            M78FlowReq req = M78FlowReq.builder()
                    .userName(userName)
                    .flowId(flowId)
                    .flowRecordId(flowRecordId)
                    .operateCmd(M78FlowOperateType.OPERATE_FLOW)
                    .cmd(M78FlowCMD.CANCEL_FLOW)
                    .build();
            operateFlow(req);
        } catch (Throwable e) {
            log.error("Failed to terminate flow:", e);
        }
    }

    public void getFlowStatus(String userName, String flowId, String flowRecordId) {
        try {
            M78FlowReq req = M78FlowReq.builder()
                    .userName(userName)
                    .flowId(flowId)
                    .flowRecordId(flowRecordId)
                    .operateCmd(M78FlowOperateType.GET_STATUS)
                    .build();
            operateFlow(req);
        } catch (Throwable e) {
            log.error("getFlowStatus error", e);
        }
    }

    protected void operateFlow(@Nonnull M78FlowReq req) {
        try {
            if (StringUtils.isBlank(req.getOperateCmd())) {
                log.error("empty operateCmd! WILL DO NOTHING!");
                return;
            }
            send(GsonUtils.GSON.toJson(req));
        } catch (Throwable e) {
            log.error("Error operating flow", e);
        }
    }

    //发送消息
    protected void send(String req) {
        ws.send(req);
    }

    private static String get(JsonObject obj, String key, String defaultValue) {
        if (obj.has(key)) {
            return obj.get(key).getAsString();
        }
        return defaultValue;
    }

    @Override
    public String getName() {
        return StringUtils.isNotBlank(name) ? name : type.getTypeName();
    }

    @Override
    public ClientType getClientType() {
        return type;
    }

    public static FlowWsClient.Builder builder() {
        return new FlowWsClient.Builder();
    }

    public static final class Builder {
        private String projectName;
        private String url = "";
        private CountDownLatch latch;

        private Builder() {
        }


        public Builder projectName(String val) {
            projectName = val;
            return this;
        }

        public Builder url(String val) {
            url = val;
            return this;
        }

        public Builder latch(CountDownLatch val) {
            latch = val;
            return this;
        }

        public FlowWsClient build() {
            return new FlowWsClient(this);
        }
    }
}

