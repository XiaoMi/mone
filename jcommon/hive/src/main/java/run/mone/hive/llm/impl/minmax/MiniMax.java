package run.mone.hive.llm.impl.minmax;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import okhttp3.*;
import okio.ByteString;
import run.mone.hive.common.GsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2025/4/23 15:38
 */
public class MiniMax {


    @SneakyThrows
    public byte[] generateAudio(String groupId, String apiKey, String text) {
        String url = "https://api.minimax.chat/v1/t2a_v2?GroupId=" + groupId;

        // 构建请求体
        JsonObject requestBody = buildTtsStreamBody(text);

        // 构建请求
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .post(RequestBody.create(MediaType.parse("application/json"), requestBody.toString()))
                .build();

        // 发送请求并处理响应
        return processStreamResponse(request);
    }


    private JsonObject buildTtsStreamBody(String text) {
        JsonObject body = new JsonObject();
        body.addProperty("model", "speech-02-turbo");
        body.addProperty("text", text);
        body.addProperty("stream", true);

        // 语音设置
        JsonObject voiceSetting = new JsonObject();
        voiceSetting.addProperty("voice_id", "male-qn-qingse");
        voiceSetting.addProperty("speed", 1.0);
        voiceSetting.addProperty("vol", 1.0);
        voiceSetting.addProperty("pitch", 0);
        body.add("voice_setting", voiceSetting);

        // 发音词典
        JsonObject pronunciationDict = new JsonObject();
        JsonArray tone = new JsonArray();
        tone.add("处理/(chu3)(li3)");
        tone.add("危险/dangerous");
        pronunciationDict.add("tone", tone);
        body.add("pronunciation_dict", pronunciationDict);

        // 音频设置
        JsonObject audioSetting = new JsonObject();
        audioSetting.addProperty("sample_rate", 32000);
        audioSetting.addProperty("bitrate", 128000);
        audioSetting.addProperty("format", "mp3");
        audioSetting.addProperty("channel", 1);
        body.add("audio_setting", audioSetting);

        return body;
    }


    private byte[] processStreamResponse(Request request) throws IOException {
        OkHttpClient client = new OkHttpClient();

        List<byte[]> audioChunks = new ArrayList<>();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("请求失败: " + response);
            }

            try (ResponseBody responseBody = response.body()) {
                if (responseBody == null) {
                    throw new IOException("响应体为空");
                }

                InputStream inputStream = responseBody.byteStream();
                byte[] buffer = new byte[8192];
                int bytesRead;
                StringBuilder lineBuilder = new StringBuilder();

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    String chunk = new String(buffer, 0, bytesRead);
                    lineBuilder.append(chunk);

                    // 处理可能的多行数据
                    String content = lineBuilder.toString();
                    int newlineIndex;
                    while ((newlineIndex = content.indexOf('\n')) != -1) {
                        String line = content.substring(0, newlineIndex);
                        content = content.substring(newlineIndex + 1);
                        lineBuilder = new StringBuilder(content);

                        if (line.startsWith("data:")) {
                            String jsonStr = line.substring(5);
                            try {
                                JsonObject jsonObject = GsonUtils.gson.fromJson(jsonStr, JsonObject.class);
                                if (jsonObject.has("data") && !jsonObject.has("extra_info")) {
                                    JsonObject data = jsonObject.getAsJsonObject("data");
                                    if (data.has("audio")) {
                                        String audioHex = data.get("audio").getAsString();
                                        byte[] audioBytes = ByteString.decodeHex(audioHex).toByteArray();
                                        audioChunks.add(audioBytes);
                                    }
                                }
                            } catch (Exception e) {
                                System.err.println("JSON解析错误: " + e.getMessage());
                            }
                        }
                    }
                }
            }
        }

        // 合并所有音频块
        int totalLength = 0;
        for (byte[] chunk : audioChunks) {
            totalLength += chunk.length;
        }

        byte[] completeAudio = new byte[totalLength];
        int currentPosition = 0;
        for (byte[] chunk : audioChunks) {
            System.arraycopy(chunk, 0, completeAudio, currentPosition, chunk.length);
            currentPosition += chunk.length;
        }

        return completeAudio;
    }

}
