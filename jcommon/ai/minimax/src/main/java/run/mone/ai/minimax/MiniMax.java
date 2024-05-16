package run.mone.ai.minimax;

import okhttp3.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public class MiniMax {


    public static byte[] call_Text_To_Speech(String groupId, String authorization, String content) {
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.MINUTES).build();
        // 设置请求体的内容类型和内容
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        String url = String.format("https://api.minimax.chat/v1/text_to_speech?GroupId=%s", groupId);

        // 构建请求
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(mediaType, content))
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
