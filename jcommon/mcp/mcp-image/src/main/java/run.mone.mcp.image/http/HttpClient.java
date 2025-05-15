package run.mone.mcp.image.http;

import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpClient {
    private final OkHttpClient client;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public HttpClient() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
    }

    public String post(String url, String param) throws IOException {
        return post(url, param, null);
    }

    public String post(String url, String param, Map<String, String> headers) throws IOException {
        RequestBody body = RequestBody.create(param.toString(), JSON);
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(body);

        if (headers != null) {
            headers.forEach(requestBuilder::addHeader);
        }

        Request request = requestBuilder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String responseBody = response.body().string();
            return responseBody;
        }
    }
}
