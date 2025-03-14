package run.mone.mcp.high.risk.element.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;

public class HttpClient {
    private final OkHttpClient client;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public HttpClient() {
        this.client = new OkHttpClient();
    }

    public JsonObject post(String url, JsonObject jsonBody) throws IOException {
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
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

    public JsonObject get(String url, Map<String, String> queryParams, Map<String, String> headers) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }

        Request.Builder requestBuilder = new Request.Builder()
                .url(urlBuilder.build())
                .get();
                
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        Request request = requestBuilder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String responseBody = response.body().string();
            return JsonParser.parseString(responseBody).getAsJsonObject();
        }
    }

    public JsonObject get(String url, Map<String, String> queryParams) throws IOException {
        return get(url, queryParams, null);
    }

    public JsonObject post(String url, Map<String, String> queryParams, Map<String, String> headers) throws IOException {
        // 将Map转换为JsonObject
        JsonObject jsonBody = new JsonObject();
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            jsonBody.addProperty(entry.getKey(), entry.getValue());
        }
        
        return post(url, jsonBody);
    }
}
