package run.mone.mcp.cursor.miapi.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import java.util.concurrent.TimeUnit;
import java.io.IOException;

public class HttpClient {
    private final OkHttpClient client;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public HttpClient() {
        this.client = new OkHttpClient().newBuilder()
                .connectTimeout(600, TimeUnit.SECONDS)      // 连接超时
                .readTimeout(600, TimeUnit.SECONDS)         // 读取超时
                .writeTimeout(600, TimeUnit.SECONDS)        // 写入超时
                .build();
    }

    public JsonObject get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String responseBody = response.body().string();
            return JsonParser.parseString(responseBody).getAsJsonObject();
        }
    }

    public JsonObject post(String url, String param) throws IOException {
        RequestBody body = RequestBody.create(param.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String responseBody = response.body().string();
            return JsonParser.parseString(responseBody).getAsJsonObject();
        }
    }
}
