package run.mone.hive.utils;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import run.mone.hive.llm.LLMProvider;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ModelInfoUtils {

    @Data
    @Builder
    public static class ModelInfo {
        private String modelName;
        private Integer tokenLimit;
    }

    private static final Map<LLMProvider, Map<String, ModelInfo>> providerModelInfoMap;
    private static final Map<String, ModelInfo> openRouterModelInfo = new ConcurrentHashMap<>();
    private static final Gson gson = new Gson();

    static {
        // Initialize for non-OpenRouter models
        Map<String, ModelInfo> deepSeekMap = new ConcurrentHashMap<>();
        deepSeekMap.put("deepseek-chat", ModelInfo.builder().modelName("deepseek-chat").tokenLimit(163840).build());

        providerModelInfoMap = new ConcurrentHashMap<>();
        providerModelInfoMap.put(LLMProvider.DEEPSEEK, deepSeekMap);
        providerModelInfoMap.put(LLMProvider.OPENROUTER, openRouterModelInfo);
    }

    public static Optional<ModelInfo> getModelInfo(LLMProvider provider, String modelName) {
        if (provider == LLMProvider.OPENROUTER) {
            // For OpenRouter, fetch dynamically if not present
            if (!openRouterModelInfo.containsKey(modelName)) {
                fetchOpenRouterModels();
            }
        }

        return Optional.ofNullable(providerModelInfoMap.get(provider))
                .map(modelMap -> modelMap.get(modelName));
    }

    @SneakyThrows
    private static void fetchOpenRouterModels() {
        log.info("Fetching model info from OpenRouter...");
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url("https://openrouter.ai/api/v1/models")
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Failed to fetch openrouter models: {}", response);
                return;
            }
            String responseBody = response.body().string();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            JsonArray data = jsonResponse.getAsJsonArray("data");
            for (JsonElement item : data) {
                JsonObject modelObject = item.getAsJsonObject();
                String id = modelObject.get("id").getAsString();
                int contextLength = modelObject.get("context_length").getAsInt();
                openRouterModelInfo.put(id, ModelInfo.builder().modelName(id).tokenLimit(contextLength).build());
            }
            log.info("Successfully fetched and cached {} models from OpenRouter.", data.size());
        } catch (Exception e) {
            log.error("Error fetching openrouter models", e);
        }
    }
}
