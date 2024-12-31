package run.mone.ai.gpt;

import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import run.mone.ai.gpt.bo.ResponsePayload;
import run.mone.ai.gpt.bo.multiModal.GptVisionRequest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Data
@Slf4j
public class GptClient {

    private static Gson gson = new Gson();

    public ResponsePayload visionCall(String url, String token, GptVisionRequest gptVisionRequest) {
        return baseCall(url, token, gson.toJson(gptVisionRequest));
    }

    private ResponsePayload baseCall(String url, String token, String bodyStr) {
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.MINUTES).build();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, bodyStr);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("api-key", token)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 429) {
                ResponsePayload res = new ResponsePayload();
                ResponsePayload.Error error = new ResponsePayload.Error();
                error.setCode("429");
                error.setMessage("被gpt限流了");
                res.setError(error);
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
