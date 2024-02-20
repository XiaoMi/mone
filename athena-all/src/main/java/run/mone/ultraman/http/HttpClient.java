package run.mone.ultraman.http;

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import run.mone.m78.ip.common.ConfigUtils;
import run.mone.m78.ip.common.NotificationCenter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import run.mone.ultraman.AthenaContext;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2023/4/26 22:16
 */
@Slf4j
public class HttpClient {

    private static Gson gson = new Gson();

    @SneakyThrows
    public static String callAiProxy(String action, Map<String, String> params) {
        String url = ConfigUtils.getConfig().getAiProxy() + "/" + action;
        JsonObject r = new JsonObject();
        params.forEach((k, v) -> r.addProperty(k, v));
        String json = gson.toJson(r);
        return callHttpServer(url, action, json);
    }

    public static String callZServer(String url, String action, String request) {
        return callHttpServer(url, action, request);
    }


    public static String get(String url) {
        return get(url, false);
    }


    public static String get(String url, boolean useToken) {
        OkHttpClient client = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder();
        if (useToken) {
            requestBuilder.header("athena_token", AthenaContext.ins().getToken());
        }
        requestBuilder.url(url);
        try (Response response = client.newCall(requestBuilder.build()).execute()) {
            if (!response.isSuccessful()) {
                return "error";
            }
            return response.body().string();
        } catch (Exception e) {
            return "";
        }
    }

    public static String callHttpServer(String url, String action, String req) {
        return callHttpServer(url, action, req, true);
    }

    public static String callHttpServer(String url, String action, String req, boolean notify) {
        return callHttpServer(url, action, req, notify, false);
    }


    @SneakyThrows
    public static String callHttpServer(String url, String action, String req, boolean notify, boolean useToken) {
        log.info("call action:{}", action);
        Stopwatch sw = Stopwatch.createStarted();
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .build();
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), req);
            Request.Builder requestBuilder = new Request.Builder();
            if (useToken) {
                requestBuilder.header("athena_token", AthenaContext.ins().getToken());
            }
            requestBuilder.url(url);
            requestBuilder.post(body);
            requestBuilder.build();
            Call call = client.newCall(requestBuilder.build());
            Response response = call.execute();
            String res = response.body().string();
            return res;
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
            return ex.getMessage();
        } finally {
            long useTime = sw.elapsed(TimeUnit.MILLISECONDS);
            if (notify) {
                NotificationCenter.notice(null, "call http server action " + action + " use time:" + useTime + "ms", true);
            }
        }
    }

    public static String post(String url, String req) {
        return callHttpServer(url, "", req, false);
    }

    public static String post(String url, String req, boolean useToken) {
        return callHttpServer(url, "", req, false, useToken);
    }


    @SneakyThrows
    public static void asyncCallHttpServer(String url, String action, String req, Consumer<String> consumer) {
        log.info("call action:{}", action);
        Stopwatch sw = Stopwatch.createStarted();
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .build();
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), req);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    log.info("call http async error:{}", e.getMessage());
                    consumer.accept(e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    log.info("call http async success");
                    consumer.accept("success");
                }
            });

        } finally {
            long useTime = sw.elapsed(TimeUnit.MILLISECONDS);
            NotificationCenter.notice(null, "call http server action " + action + " use time:" + useTime + "ms", true);
        }
    }


    public static String callChatgml(String req) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();

        JsonObject r = new JsonObject();
        r.addProperty("prompt", req);
        r.add("history", new JsonArray());

        String json = gson.toJson(r);
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), json);

        String url = ConfigUtils.getConfig().getJavaPath();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();
        String res = response.body().string();
        return gson.fromJson(res, JsonObject.class).get("response").getAsString();
    }

}
