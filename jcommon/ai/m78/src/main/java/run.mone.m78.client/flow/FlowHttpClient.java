package run.mone.m78.client.flow;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import run.mone.m78.client.model.ClientType;
import run.mone.m78.client.M78Client;
import run.mone.m78.client.model.M78FlowReq;
import run.mone.m78.client.util.GsonUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author HawickMason@xiaomi.com
 * @date 8/22/24 10:36
 */
@Slf4j
public class FlowHttpClient implements M78Client {

    private String name = ClientType.FLOW_HTTP.getTypeName();

    private String url = "";

    private String token;

    private long timeout = 60;

    private final ClientType type = ClientType.FLOW_HTTP;

    public FlowHttpClient() {
        // default constructor
    }

    private FlowHttpClient(Builder builder) {
        name = builder.name;
        url = builder.url;
        token = builder.token;
        timeout = builder.timeout;
    }

    public static FlowHttpClient.Builder builder() {
        return new FlowHttpClient.Builder();
    }

    public String callFlow(M78FlowReq flowReq, JsonObject jsonObject) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .build();

        // 使用 Gson 的 JsonObject 构建请求体
        JsonObject mainObject = new JsonObject();
        mainObject.addProperty("userName", flowReq.getUserName());
        mainObject.addProperty("flowId", flowReq.getFlowId());
        mainObject.add("input", GsonUtils.GSON.toJsonTree(flowReq.getInputs()));

        if (jsonObject != null) {
            if (jsonObject.has("history")) {
                JsonElement history = jsonObject.remove("history");
                mainObject.add("history", history);
            }
            if (jsonObject.size() > 0) {
                mainObject.add("params", jsonObject);
            }
        }

        String jsonBody = mainObject.toString();

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonBody);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", Optional.ofNullable(token).orElseThrow(() -> new IllegalArgumentException("须传递token!")))
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Throwable e) {
            log.error("callFlow error", e);
        }
        return "";
    }

    @Override
    public String getName() {
        return StringUtils.isNotBlank(name) ? name : type.getTypeName();
    }

    @Override
    public ClientType getClientType() {
        return type;
    }

    public static final class Builder {

        private String name = ClientType.FLOW_HTTP.getTypeName();

        private String url = "";
        private String token;

        public long timeout = 60;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder name(String val) {
            name = val;
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

        public Builder timeout(long val) {
            timeout = val;
            return this;
        }

        public FlowHttpClient build() {
            return new FlowHttpClient(this);
        }
    }
}
