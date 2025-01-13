package run.mone.hive.llm;

import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2025/1/7 16:43
 */
public class AudioLLM {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");


    //文本转语音 (ruanmengnvsheng)
    public byte[] generateSpeech(String apiKey, String text, String voice, String outputPath) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "step-tts-mini");
        requestBody.addProperty("input", text);
        requestBody.addProperty("voice", voice);

        Request request = new Request.Builder()
                .url("https://api.stepfun.com/v1/audio/speech")
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }
            byte[] audioData = response.body().bytes();
            if (outputPath != null) {
                java.nio.file.Files.write(java.nio.file.Paths.get(outputPath), audioData);
                return null;
            }
            return audioData;
        }
    }

}
