package run.mone.ai.google;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import run.mone.ai.google.bo.Content;
import run.mone.ai.google.bo.RequestPayload;
import run.mone.ai.google.bo.ResponsePayload;
import run.mone.ai.google.bo.multiModal.GVisionRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2024/4/9 15:59
 */
@Data
@Slf4j
public class CloudeClient {

    private String url = "https://us-central1-aiplatform.googleapis.com/v1/projects/";

    private String googleUrl = "www.googleapis.com";

    private String projectId = "";

    private String model = "claude-3-haiku@20240307";

    private String token;

    private static Gson gson = new Gson();

    @SneakyThrows
    public String token(String model) {
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                        new FileInputStream("/tmp/key-"+model+".json"))
                .createScoped(Collections.singleton("https://" + googleUrl + "/auth/cloud-platform"));
        // Use the credentials to authenticate and generate an access token
        credentials.refreshIfExpired();
        AccessToken token = credentials.getAccessToken();
        // Now you can use the access token
        this.token = token.getTokenValue();
        return this.token;
    }

    public ResponsePayload visionCall(String url, String token, GVisionRequest GVisionRequest) {
        return baseCall(url, token, gson.toJson(GVisionRequest));
    }

    public ResponsePayload call(String token, RequestPayload requestPayload) {
        String callUrl = url + projectId + "/locations/us-central1/publishers/anthropic/models/" + model + ":streamRawPredict";
        return call(callUrl, token, requestPayload);
    }

    public ResponsePayload call(String url, String token, RequestPayload requestPayload) {
        return baseCall(url, token, gson.toJson(requestPayload));
    }

    private ResponsePayload baseCall(String url, String token, String bodyStr) {
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.MINUTES).build();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, bodyStr);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 429) {
                ResponsePayload res = new ResponsePayload();
                Content content = new Content();
                content.setText(gson.toJson(ImmutableMap.of("message", "被claude3限流了", "code", "429")));
                log.info("claude res:{}", content.getText());
                res.setContent(Lists.newArrayList(content));
                return res;
            }
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            // Handle the response
            String res = response.body().string();
            log.info("claude3 res:{}", res);
            return new Gson().fromJson(res, ResponsePayload.class);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}
