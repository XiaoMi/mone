package run.mone.hive.memory.longterm.llm.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import run.mone.hive.memory.longterm.config.LlmConfig;
import run.mone.hive.memory.longterm.llm.LLMBase;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.Gson;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Ollama LLM实现
 * 支持本地部署的Ollama模型
 */
@Slf4j
@Data
public class OllamaLLM implements LLMBase {
    private final LlmConfig config;
    private final HttpClient httpClient;
    private final Gson gson;
    
    private static final String DEFAULT_BASE_URL = "http://localhost:11434";
    private static final String DEFAULT_MODEL = "llama3.1";
    
    public OllamaLLM(LlmConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
        this.gson = new Gson();
        
        // 设置默认值
        if (config.getModel() == null || config.getModel().isEmpty()) {
            config.setModel(DEFAULT_MODEL);
        }
        
        if (config.getBaseUrl() == null || config.getBaseUrl().isEmpty()) {
            config.setBaseUrl(DEFAULT_BASE_URL);
        }
        
        validateConfig();
        log.info("Ollama LLM initialized with model: {} at {}", config.getModel(), config.getBaseUrl());
    }
    
    @Override
    public String generateResponse(List<Map<String, Object>> messages, String responseFormat) {
        try {
            // 构建请求
            JsonObject request = buildRequest(messages);
            
            // API URL
            String apiUrl = config.getBaseUrl() + "/api/chat";
            
            // 发送HTTP请求
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .timeout(Duration.ofMinutes(5)); // Ollama可能比较慢

            // 添加自定义头
            if (config.getCustomHeaders() != null) {
                config.getCustomHeaders().forEach(requestBuilder::header);
            }

            HttpRequest httpRequest = requestBuilder.build();
            
            HttpResponse<String> response = httpClient.send(httpRequest, 
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                log.error("Ollama API error: {} - {}", response.statusCode(), response.body());
                throw new RuntimeException("Ollama API error: " + response.statusCode());
            }
            
            // 解析响应
            return parseResponse(response.body());
            
        } catch (Exception e) {
            log.error("Error generating response from Ollama", e);
            throw new RuntimeException("Failed to generate response from Ollama", e);
        }
    }
    
    @Override
    public boolean supportsVision() {
        String model = config.getModel().toLowerCase();
        return model.contains("llava") || model.contains("vision");
    }
    
    private JsonObject buildRequest(List<Map<String, Object>> messages) {
        JsonObject request = new JsonObject();
        
        // 基本参数
        request.addProperty("model", config.getModel());
        request.addProperty("stream", false);
        
        // 选项配置
        JsonObject options = new JsonObject();
        
        Double temperature = config.getTemperature();
        if (temperature != null) {
            options.addProperty("temperature", temperature);
        }
        
        Integer maxTokens = config.getMaxTokens();
        if (maxTokens != null) {
            options.addProperty("num_predict", maxTokens);
        }
        
        Double topP = config.getTopP();
        if (topP != null) {
            options.addProperty("top_p", topP);
        }
        request.add("options", options);
        
        // 构建消息数组
        JsonArray messagesArray = new JsonArray();
        
        for (Map<String, Object> message : messages) {
            String role = (String) message.get("role");
            String content = (String) message.get("content");
            
            JsonObject messageObj = new JsonObject();
            messageObj.addProperty("role", role);
            messageObj.addProperty("content", content);
            messagesArray.add(messageObj);
        }
        
        request.add("messages", messagesArray);
        
        return request;
    }
    
    private String parseResponse(String responseBody) {
        try {
            JsonObject response = JsonParser.parseString(responseBody).getAsJsonObject();
            
            // 检查错误
            if (response.has("error")) {
                String errorMessage = response.get("error").getAsString();
                throw new RuntimeException("Ollama API error: " + errorMessage);
            }
            
            // 提取消息内容
            if (response.has("message")) {
                JsonObject message = response.getAsJsonObject("message");
                if (message.has("content")) {
                    return message.get("content").getAsString();
                }
            }
            
            throw new RuntimeException("No valid content found in Ollama response");
            
        } catch (Exception e) {
            log.error("Error parsing Ollama response: {}", responseBody, e);
            throw new RuntimeException("Failed to parse Ollama response", e);
        }
    }
    
    /**
     * 获取可用模型列表
     */
    public List<String> getAvailableModels() {
        try {
            String apiUrl = config.getBaseUrl() + "/api/tags";
            
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .timeout(Duration.ofSeconds(30));

            // 添加自定义头
            if (config.getCustomHeaders() != null) {
                config.getCustomHeaders().forEach(requestBuilder::header);
            }

            HttpRequest request = requestBuilder.build();
            
            HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonObject responseObj = JsonParser.parseString(response.body()).getAsJsonObject();
                if (responseObj.has("models")) {
                    JsonArray models = responseObj.getAsJsonArray("models");
                    List<String> modelNames = new java.util.ArrayList<>();
                    for (int i = 0; i < models.size(); i++) {
                        JsonObject model = models.get(i).getAsJsonObject();
                        modelNames.add(model.get("name").getAsString());
                    }
                    return modelNames;
                }
            }
            
            log.warn("Failed to get available models from Ollama: {}", response.statusCode());
            return getCommonModels();
            
        } catch (Exception e) {
            log.error("Error getting available models", e);
            return getCommonModels();
        }
    }
    
    /**
     * 获取常见模型列表
     */
    public static List<String> getCommonModels() {
        return List.of(
            "llama3.1",
            "llama3.1:70b",
            "llama3.1:8b", 
            "llama3.2",
            "llama3.2:1b",
            "llama3.2:3b",
            "gemma2",
            "gemma2:2b",
            "gemma2:9b",
            "qwen2.5",
            "qwen2.5:7b",
            "mistral",
            "codellama",
            "llava",
            "dolphin-mistral"
        );
    }
    
    /**
     * 检查模型是否可用
     */
    public boolean isModelAvailable(String modelName) {
        List<String> availableModels = getAvailableModels();
        return availableModels.contains(modelName);
    }
}