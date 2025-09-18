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
import java.util.HashMap;

/**
 * Claude (Anthropic) LLM实现
 * 基于mem0的AnthropicLLM实现
 */
@Slf4j
@Data
public class ClaudeLLM implements LLMBase {
    private final LlmConfig config;
    private final HttpClient httpClient;
    private final Gson gson;
    
    private static final String ANTHROPIC_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String DEFAULT_MODEL = "claude-3-5-sonnet-20240620";
    
    public ClaudeLLM(LlmConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
        this.gson = new Gson();
        
        // 设置默认模型
        if (config.getModel() == null || config.getModel().isEmpty()) {
            config.setModel(DEFAULT_MODEL);
        }
        
        validateConfig();
        log.info("Claude LLM initialized with model: {}", config.getModel());
    }
    
    @Override
    public String generateResponse(List<Map<String, Object>> messages, String responseFormat) {
        try {
            // 构建请求
            JsonObject request = buildRequest(messages);
            
            // 发送HTTP请求
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(ANTHROPIC_API_URL))
                .header("Content-Type", "application/json")
                .header("anthropic-version", "2023-06-01")
                .header("x-api-key", getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .timeout(Duration.ofMinutes(2));

            // 添加自定义头
            if (config.getCustomHeaders() != null) {
                config.getCustomHeaders().forEach(requestBuilder::header);
            }

            HttpRequest httpRequest = requestBuilder.build();
            
            HttpResponse<String> response = httpClient.send(httpRequest, 
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                log.error("Claude API error: {} - {}", response.statusCode(), response.body());
                throw new RuntimeException("Claude API error: " + response.statusCode());
            }
            
            // 解析响应
            return parseResponse(response.body());
            
        } catch (Exception e) {
            log.error("Error generating response from Claude", e);
            throw new RuntimeException("Failed to generate response from Claude", e);
        }
    }
    
    @Override
    public boolean supportsVision() {
        String model = config.getModel().toLowerCase();
        return model.contains("claude-3");
    }
    
    private JsonObject buildRequest(List<Map<String, Object>> messages) {
        JsonObject request = new JsonObject();
        
        // 基本参数
        request.addProperty("model", config.getModel());
        
        Integer maxTokens = config.getMaxTokens();
        request.addProperty("max_tokens", maxTokens != null ? maxTokens : 4000);
        
        Double temperature = config.getTemperature();
        if (temperature != null) {
            request.addProperty("temperature", temperature);
        }
        
        Double topP = config.getTopP();
        if (topP != null) {
            request.addProperty("top_p", topP);
        }
        
        // 分离系统消息和其他消息
        String systemMessage = "";
        JsonArray messagesArray = new JsonArray();
        
        for (Map<String, Object> message : messages) {
            String role = (String) message.get("role");
            String content = (String) message.get("content");
            
            if ("system".equals(role)) {
                systemMessage = content;
            } else {
                JsonObject messageObj = new JsonObject();
                messageObj.addProperty("role", role);
                messageObj.addProperty("content", content);
                messagesArray.add(messageObj);
            }
        }
        
        request.add("messages", messagesArray);
        
        if (!systemMessage.isEmpty()) {
            request.addProperty("system", systemMessage);
        }
        
        return request;
    }
    
    private String parseResponse(String responseBody) {
        try {
            JsonObject response = JsonParser.parseString(responseBody).getAsJsonObject();
            
            // 检查错误
            if (response.has("error")) {
                JsonObject error = response.getAsJsonObject("error");
                String errorMessage = error.get("message").getAsString();
                throw new RuntimeException("Claude API error: " + errorMessage);
            }
            
            // 提取内容
            if (response.has("content")) {
                JsonArray content = response.getAsJsonArray("content");
                if (content.size() > 0) {
                    JsonObject firstContent = content.get(0).getAsJsonObject();
                    if (firstContent.has("text")) {
                        return firstContent.get("text").getAsString();
                    }
                }
            }
            
            throw new RuntimeException("No valid content found in Claude response");
            
        } catch (Exception e) {
            log.error("Error parsing Claude response: {}", responseBody, e);
            throw new RuntimeException("Failed to parse Claude response", e);
        }
    }
    
    private String getApiKey() {
        String apiKey = config.getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = System.getenv("ANTHROPIC_API_KEY");
        }
        
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("Claude API key not found. Set apiKey in config or ANTHROPIC_API_KEY environment variable");
        }
        
        return apiKey;
    }
    
    /**
     * 获取支持的模型列表
     */
    public static List<String> getSupportedModels() {
        return List.of(
            "claude-3-5-sonnet-20240620",
            "claude-3-5-haiku-20241022", 
            "claude-3-opus-20240229",
            "claude-3-sonnet-20240229",
            "claude-3-haiku-20240307"
        );
    }
    
    /**
     * 验证模型是否支持
     */
    public static boolean isModelSupported(String model) {
        return getSupportedModels().contains(model);
    }
}