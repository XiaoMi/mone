package run.mone.hive.llm;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.SneakyThrows;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.schema.AiMessage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Data
public class BaseLLM {


    protected LLMConfig config;

    private LLMProvider llmProvider;

    private Gson gson = new Gson();

    private boolean google = false;


    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");


    public BaseLLM(LLMConfig config) {
        this.config = config;
        this.llmProvider = config.getLlmProvider();
    }

    public String chat(String prompt) {
        return ask(prompt).join();
    }


    public String chat(List<AiMessage> msgList) {
        return chatCompletion(System.getenv(llmProvider.getEnvName()), msgList, llmProvider.getDefaultModel(), "");
    }

    public String chat(List<AiMessage> msgList, String systemPrompt) {
        return chatCompletion(System.getenv(llmProvider.getEnvName()), msgList, llmProvider.getDefaultModel(), systemPrompt);
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
        return chatCompletion(apiKey, Lists.newArrayList(AiMessage.builder().role("user").content(content).build()), model, "");
    }

    @SneakyThrows
    public String chatCompletion(String apiKey, List<AiMessage> messages, String model, String systemPrompt) {
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


        if (this.config.isJson()) {
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


        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }
            String responseBody = response.body().string();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            return jsonResponse.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
        }
    }

    private JsonObject createMessageObject(String role, String content) {
        JsonObject message = new JsonObject();
        message.addProperty("role", role);
        message.addProperty("content", content);
        return message;
    }

}