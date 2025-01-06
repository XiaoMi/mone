package run.mone.hive.llm;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSource;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.schema.AiMessage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Data
@Slf4j
public class LLM {

    protected LLMConfig config;

    private LLMProvider llmProvider;

    private Gson gson = new Gson();

    private boolean google = false;

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");


    public LLM(LLMConfig config) {
        this.config = config;
        this.llmProvider = config.getLlmProvider();
    }

    public String chat(String prompt) {
        return ask(prompt).join();
    }


    public String chat(List<AiMessage> msgList) {
        return chatCompletion(System.getenv(llmProvider.getEnvName()), msgList, llmProvider.getDefaultModel(), "", null);
    }

    public String chat(List<AiMessage> msgList, String systemPrompt) {
        return chatCompletion(System.getenv(llmProvider.getEnvName()), msgList, llmProvider.getDefaultModel(), systemPrompt, null);
    }

    public String chat(List<AiMessage> msgList, LLMConfig config) {
        return chatCompletion(System.getenv(llmProvider.getEnvName()), msgList, llmProvider.getDefaultModel(), "", config);
    }


    public String getApiUrl() {
        return llmProvider.getUrl();
    }


    public CompletableFuture<String> ask(String prompt) {
        if (config.isDebug()) {
            return CompletableFuture.completedFuture("res:" + prompt);
        } else {
            return CompletableFuture.completedFuture(chatCompletion(System.getenv(llmProvider.getEnvName()), prompt, llmProvider.getDefaultModel()));
        }
    }

    public String chatCompletion(String apiKey, String content, String model) {
        return chatCompletion(apiKey, Lists.newArrayList(AiMessage.builder().role("user").content(content).build()), model, "", null);
    }

    @SneakyThrows
    public String chatCompletion(String apiKey, List<AiMessage> messages, String model, String systemPrompt, LLMConfig clientConfig) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);

        if (this.config.isStream()) {
            requestBody.addProperty("stream", true);
        }
        JsonArray msgArray = new JsonArray();

        for (AiMessage message : messages) {
            msgArray.add(createMessageObject(message.getRole(), message.getContent()));
        }


        if (this.config.isJson() || (null != clientConfig && clientConfig.isJson())) {
            String jsonSystemPrompt = """
                     返回结果请用JSON返回(如果用户没有指定json格式,则直接返回{"content":$res}),thx
                    """;
            JsonObject rf = new JsonObject();
            rf.addProperty("type", "json_object");
            requestBody.add("response_format", rf);
            msgArray.add(createMessageObject("system", jsonSystemPrompt));
        } else {
            if (StringUtils.isNotEmpty(systemPrompt)) {
                msgArray.add(createMessageObject("system", systemPrompt));
            }
        }


        requestBody.add("messages", msgArray);
        String apiUrl = getApiUrl();

        String rb = requestBody.toString();

        log.info("call llm:{}\nmessage:{}\n", model, rb);
        Stopwatch sw = Stopwatch.createStarted();
        String res = "";

        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(RequestBody.create(rb, JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }
            String responseBody = response.body().string();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            res = jsonResponse.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
            return res;
        } finally {
            log.info("call llm res:\n{}\n use time:{}ms", res, sw.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    private JsonObject createMessageObject(String role, String content) {
        JsonObject message = new JsonObject();
        message.addProperty("role", role);
        message.addProperty("content", content);
        return message;
    }


    public void chatCompletionStream(String apiKey, List<AiMessage> messages, String model, BiConsumer<String, JsonObject> messageHandler, Consumer<String> lineConsumer) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);
        requestBody.addProperty("stream", true);

        JsonArray msgArray = new JsonArray();

        for (AiMessage message : messages) {
            msgArray.add(createMessageObject(message.getRole(), message.getContent()));
        }

        requestBody.add("messages", gson.toJsonTree(msgArray));

        Request request = new Request.Builder()
                .url(getApiUrl())
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Accept", "text/event-stream")
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.error("Stream request failed", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected response code: " + response);
                    }
                    SSEReader reader = new SSEReader(responseBody.source());
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("===>" + line);
                        lineConsumer.accept(line);
                        if (line.startsWith("data: ")) {
                            String data = line.substring(6);
                            if ("[DONE]".equals(data)) {
                                JsonObject jsonResponse = new JsonObject();
                                jsonResponse.addProperty("type", "finish");
                                messageHandler.accept("[DONE]", jsonResponse);
                                break;
                            }
                            JsonObject jsonResponse = gson.fromJson(data, JsonObject.class);
                            String content = jsonResponse.getAsJsonArray("choices")
                                    .get(0).getAsJsonObject()
                                    .getAsJsonObject("delta")
                                    .get("content").getAsString();
                            jsonResponse.addProperty("type", "event");
                            messageHandler.accept(content, jsonResponse);
                        }
                    }
                    System.out.println("FINISH");
                }
            }
        });
    }

    private static class SSEReader {
        private final BufferedSource source;

        public SSEReader(BufferedSource source) {
            this.source = source;
        }

        public String readLine() throws IOException {
            try {
                // 使用 readUtf8LineStrict 确保读取完整行
                String line = source.readUtf8LineStrict();
                // 如果是空行，继续读取下一行
                while (line != null && line.trim().isEmpty()) {
                    line = source.readUtf8LineStrict();
                }
                return line;
            } catch (IOException e) {
                // 如果到达流的末尾，返回null
                if (source.exhausted()) {
                    return null;
                }
                throw e;
            }
        }
    }


}