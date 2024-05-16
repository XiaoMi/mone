package run.mone.ai.minimax;

import com.google.gson.Gson;
import okhttp3.*;
import run.mone.ai.minimax.bo.RequestBodyContent;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MiniMax {

    static Gson gson = new Gson();
    public static byte[] call_Text_To_Speech(String groupId, String authorization, RequestBodyContent content) {

        if (groupId == null || groupId.trim().length() < 1) {
            throw new RuntimeException("groupId is null");
        }

        if (authorization == null || authorization.trim().length() < 1) {
            throw new RuntimeException("authorization is null");
        }

        if (content == null) {
            throw new RuntimeException("content is null");
        }

        if (content.getText() == null) {
            throw new RuntimeException("content.text is null");
        }

        OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.MINUTES).build();
        // 设置请求体的内容类型和内容
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        String url = String.format("https://api.minimax.chat/v1/text_to_speech?GroupId=%s", groupId);

        // 构建请求
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(mediaType, gson.toJson(content)))
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization","Bearer "+authorization)
                .build();

        // 发送请求并获取响应
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                // 作为字节数组接收
                byte[] audioData = responseBody.bytes();
                return audioData;
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
