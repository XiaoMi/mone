package run.mone.mcp.gateway.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;

public class HttpClient {
    private final OkHttpClient client;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final String X_API_KEY;

    private final String X_API_USER;

    public HttpClient() {
        this.client = new OkHttpClient();
        this.X_API_KEY = System.getenv("GATEWAY_API_KEY");
        this.X_API_USER = System.getenv("GATEWAY_API_USER");
    }

    public JsonObject get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .header("x-api-key", X_API_KEY)
                .header("mone-skip-mi-dun-username", X_API_USER)
                .header("gw-tenant-id", "1")
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
                .header("x-api-key", X_API_KEY)
                .header("mone-skip-mi-dun-username", X_API_USER)
                .header("gw-tenant-id", "1")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String responseBody = response.body().string();
            return JsonParser.parseString(responseBody).getAsJsonObject();
        }
    }
}
