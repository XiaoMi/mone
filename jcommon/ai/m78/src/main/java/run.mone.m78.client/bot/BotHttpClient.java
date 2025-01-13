package run.mone.m78.client.bot;

import com.google.gson.Gson;
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
import run.mone.m78.client.model.History;
import run.mone.m78.client.model.M78BotReq;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * <p>BotHttpClient类实现了M78Client接口，负责与M78Bot接口进行HTTP通信。
 * 该类提供了构建HTTP请求、发送请求并处理响应的功能。
 * <blockquote><pre>
 * 主要功能包括：
 * - 通过callBot方法调用M78Bot接口并返回响应结果。
 * - 提供获取客户端名称和类型的方法。
 * - 提供一个内部Builder类用于构建BotHttpClient实例。
 *  </pre></blockquote>
 * 该类使用了OkHttpClient进行HTTP请求，并支持通过JsonObject传递额外的参数信息。
 * 需要传递token进行授权。
 *
 * @author HawickMason@xiaomi.com
 * @date 8/22/24 10:36
 * @see M78Client
 * @see ClientType
 * @see M78BotReq
 * @see JsonObject
 * @see OkHttpClient
 */
@Slf4j
public class BotHttpClient implements M78Client {

    private String name = ClientType.BOT_HTTP.getTypeName();

    private String url = "https://mone.test.mi.com/open-apis/ai-plugin-new/feature/router/probot/query";

    private String token;

    private String model;

    private final ClientType type = ClientType.BOT_HTTP;

    private long timeout = 60; // second

    public BotHttpClient() {
        // default constructor
    }

    private BotHttpClient(Builder builder) {
        name = builder.name;
        url = builder.url;
        token = builder.token;
        timeout = builder.timeout;
    }


    public String callBot(M78BotReq botReq, JsonObject jsonObject) {
        return callBot(botReq, jsonObject, null);
    }


    public static Function<String, String> DEFAULT_FUNCTION = res -> {
        log.info("res:{}", res);
        JsonObject resObj = JsonParser.parseString(JsonParser.parseString(res)
                        .getAsJsonObject()
                        .get("data").getAsString())
                .getAsJsonObject()
                .get("result")
                .getAsJsonObject();
        JsonElement data = null;
        if (resObj.has("data")) {
            data = resObj.get("data");
        } else {
            data = resObj.get("content");
        }
        if (data.isJsonPrimitive()) {
            return data.getAsString();
        }
        return data.toString();
    };

    /**
     * 调用M78Bot接口并返回响应结果
     *
     * @param botReq     包含请求信息的M78BotReq对象
     * @param jsonObject 额外的参数信息，JsonObject类型
     * @return 接口响应结果字符串，若发生异常则返回空字符串
     */
    public String callBot(M78BotReq botReq, JsonObject jsonObject, Function<String, String> function) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .build();

        // 使用 Gson 的 JsonObject 构建请求体
        JsonObject mainObject = new JsonObject();
        mainObject.addProperty("userName", botReq.getUserName());
        mainObject.addProperty("botId", botReq.getBotId());
        mainObject.addProperty("input", botReq.getInput());

        if (StringUtils.isNotBlank(this.model)) {
            mainObject.addProperty("model", this.model);
        }

        if (StringUtils.isNotEmpty(botReq.getToken())) {
            mainObject.addProperty("token", botReq.getToken());
        }

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

        log.debug("BotHttpClient callBot jsonBody:{}", jsonBody);

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
            String res = response.body().string();
            if (null == function) {
                return res;
            }
            return function.apply(res);
        } catch (Throwable e) {
            log.error("callBot error", e);
        }
        return "";
    }


    public String callBot(M78BotReq botReq, JsonObject jsonObject, History history, Function<String, String> function) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .callTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .build();

        // 使用 Gson 的 JsonObject 构建请求体
        JsonObject mainObject = new JsonObject();
        mainObject.addProperty("userName", botReq.getUserName());
        mainObject.addProperty("botId", botReq.getBotId());
        mainObject.addProperty("input", botReq.getInput());

        if (null != history) {
            mainObject.add("history", new Gson().toJsonTree(history.getMessages()));
        }

        if (jsonObject != null) {
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
            String res = response.body().string();
            if (null == function) {
                return res;
            }
            return function.apply(res);
        } catch (Throwable e) {
            log.error("callBot error", e);
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

    public static BotHttpClient.Builder builder() {
        return new BotHttpClient.Builder();
    }


    public static final class Builder {
        private String name = ClientType.BOT_HTTP.getTypeName();
        private String url = "https://mone.test.mi.com/open-apis/ai-plugin-new/feature/router/probot/query";
        private String token;

        private String model;

        private long timeout = 60; // second

        public Builder() {
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

        public Builder model(String val) {
            model = val;
            return this;
        }

        public Builder timeout(long val) {
            timeout = val;
            return this;
        }

        public BotHttpClient build() {
            return new BotHttpClient(this);
        }
    }
}
