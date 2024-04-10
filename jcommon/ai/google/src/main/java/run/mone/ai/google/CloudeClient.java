package run.mone.ai.google;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;
import lombok.Data;
import lombok.SneakyThrows;
import okhttp3.*;
import run.mone.ai.google.bo.RequestPayload;
import run.mone.ai.google.bo.ResponsePayload;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

/**
 * @author goodjava@qq.com
 * @date 2024/4/9 15:59
 */
@Data
public class CloudeClient {

    private String url = "https://us-central1-aiplatform.googleapis.com/v1/projects/";

    private String googleUrl = "www.googleapis.com";

    private String projectId = "";

    private String model = "claude-3-haiku@20240307";

    private String token;


    @SneakyThrows
    public String token() {
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                        new FileInputStream("/tmp/key.json"))
                .createScoped(Collections.singleton("https://" + googleUrl + "/auth/cloud-platform"));
        // Use the credentials to authenticate and generate an access token
        credentials.refreshIfExpired();
        AccessToken token = credentials.getAccessToken();
        // Now you can use the access token
        this.token = token.getTokenValue();
        return this.token;
    }


    public ResponsePayload call(String token, RequestPayload requestPayload) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, new Gson().toJson(requestPayload));
        Request request = new Request.Builder()
                .url(url + projectId + "/locations/us-central1/publishers/anthropic/models/" + model + ":streamRawPredict")
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            // Handle the response
            return new Gson().fromJson(response.body().string(), ResponsePayload.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
