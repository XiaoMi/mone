package run.mone.hive.memory.longterm.llm.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import lombok.EqualsAndHashCode;

import run.mone.hive.memory.longterm.config.LlmConfig;
import run.mone.hive.memory.longterm.llm.LLMBase;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * OpenAI LLM实现
 * 支持GPT-4、GPT-3.5等模型
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class OpenAiLLM implements LLMBase {
    
    private final LlmConfig config;
    private final HttpClient httpClient;
    private final Gson gson;
    private final String apiKey;
    private final String baseUrl;
    
    public OpenAiLLM(LlmConfig config) {
        this.config = config;
        this.gson = new Gson();
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
            
        // 获取API密钥
        this.apiKey = config.getApiKey() != null 
            ? config.getApiKey() 
            : System.getenv("OPENAI_API_KEY");
            
        if (this.apiKey == null || this.apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("OpenAI API key is required. Set it in config or OPENAI_API_KEY environment variable.");
        }
        
        // 设置基础URL
        this.baseUrl = config.getBaseUrl() != null 
            ? config.getBaseUrl() 
            : "https://api.openai.com/v1";
            
        validateConfig();
    }
    
    @Override
    public String generateResponse(List<Map<String, Object>> messages, String responseFormat) {
        try {
            // 构建请求体
            Map<String, Object> requestBody = buildRequestBody(messages, responseFormat);
            
            // 发送请求
            String response = sendRequest(requestBody);
            
            // 解析响应
            return parseResponse(response);
            
        } catch (Exception e) {
            log.error("Error generating response from OpenAI", e);
            throw new RuntimeException("Failed to generate response from OpenAI", e);
        }
    }
    
    @Override
    public String generateResponse(List<Map<String, Object>> messages, 
                                  List<Map<String, Object>> tools,
                                  String toolChoice,
                                  String responseFormat) {
        try {
            // 构建请求体 (包含工具)
            Map<String, Object> requestBody = buildRequestBodyWithTools(messages, tools, toolChoice, responseFormat);
            
            // 发送请求
            String response = sendRequest(requestBody);
            
            // 解析响应
            return parseResponse(response);
            
        } catch (Exception e) {
            log.error("Error generating response with tools from OpenAI", e);
            throw new RuntimeException("Failed to generate response with tools from OpenAI", e);
        }
    }
    
    @Override
    public boolean supportsVision() {
        String model = config.getModel().toLowerCase();
        return model.contains("gpt-4") && (model.contains("vision") || model.contains("turbo"));
    }
    
    private Map<String, Object> buildRequestBody(List<Map<String, Object>> messages, String responseFormat) {
        Map<String, Object> requestBody = new HashMap<>();
        
        requestBody.put("model", config.getModel());
        requestBody.put("messages", messages);
        
        // 获取支持的参数
        Map<String, Object> additionalParams = new HashMap<>();
        additionalParams.putAll(config.getConfig());
        
        Map<String, Object> supportedParams = getSupportedParams(additionalParams);
        requestBody.putAll(supportedParams);
        
        // 设置响应格式
        if (responseFormat != null && "json_object".equals(responseFormat)) {
            Map<String, Object> format = new HashMap<>();
            format.put("type", "json_object");
            requestBody.put("response_format", format);
        }
        
        return requestBody;
    }
    
    private Map<String, Object> buildRequestBodyWithTools(List<Map<String, Object>> messages,
                                                         List<Map<String, Object>> tools,
                                                         String toolChoice,
                                                         String responseFormat) {
        Map<String, Object> requestBody = buildRequestBody(messages, responseFormat);
        
        if (tools != null && !tools.isEmpty()) {
            requestBody.put("tools", tools);
            
            if (toolChoice != null) {
                requestBody.put("tool_choice", toolChoice);
            }
        }
        
        return requestBody;
    }
    
    private String sendRequest(Map<String, Object> requestBody) throws Exception {
        String requestJson = gson.toJson(requestBody);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/chat/completions"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(requestJson))
            .timeout(Duration.ofMinutes(2));

        // 添加自定义头
        if (config.getCustomHeaders() != null) {
            config.getCustomHeaders().forEach(requestBuilder::header);
        }

        HttpRequest request = requestBuilder.build();
            
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            String errorMsg = String.format("OpenAI API request failed with status %d: %s", 
                response.statusCode(), response.body());
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        
        return response.body();
    }
    
    private String parseResponse(String responseJson) {
        try {
            JsonObject jsonResponse = gson.fromJson(responseJson, JsonObject.class);
            
            if (jsonResponse.has("error")) {
                JsonObject error = jsonResponse.getAsJsonObject("error");
                String errorMsg = String.format("OpenAI API error: %s", error.get("message").getAsString());
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            JsonArray choices = jsonResponse.getAsJsonArray("choices");
            if (choices.size() == 0) {
                throw new RuntimeException("No choices in OpenAI response");
            }
            
            JsonObject firstChoice = choices.get(0).getAsJsonObject();
            JsonObject message = firstChoice.getAsJsonObject("message");
            
            // 检查是否有工具调用
            if (message.has("tool_calls") && message.get("tool_calls").getAsJsonArray().size() > 0) {
                JsonArray toolCalls = message.getAsJsonArray("tool_calls");
                // 返回工具调用信息
                return gson.toJson(Map.of("tool_calls", toolCalls));
            }
            
            // 返回常规内容
            return message.get("content").getAsString();
            
        } catch (Exception e) {
            log.error("Error parsing OpenAI response: {}", responseJson, e);
            throw new RuntimeException("Failed to parse OpenAI response", e);
        }
    }
    
    /**
     * 检查API密钥的有效性
     */
    public boolean validateApiKey() {
        try {
            List<Map<String, Object>> testMessages = List.of(
                Map.of("role", "user", "content", "Hello")
            );
            
            generateResponse(testMessages, null);
            return true;
            
        } catch (Exception e) {
            log.warn("API key validation failed", e);
            return false;
        }
    }
    
    /**
     * 获取可用模型列表
     */
    public List<String> getAvailableModels() {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/models"))
                .header("Authorization", "Bearer " + apiKey)
                .GET();

            // 添加自定义头
            if (config.getCustomHeaders() != null) {
                config.getCustomHeaders().forEach(requestBuilder::header);
            }

            HttpRequest request = requestBuilder.build();
                
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
                JsonArray models = jsonResponse.getAsJsonArray("data");
                
                List<String> modelList = new ArrayList<>();
                for (int i = 0; i < models.size(); i++) {
                    JsonElement element = models.get(i);
                    JsonObject model = element.getAsJsonObject();
                    modelList.add(model.get("id").getAsString());
                }
                return modelList;
            }
            
        } catch (Exception e) {
            log.error("Error getting available models", e);
        }
        
        return List.of();
    }
    
    @Override
    public void close() {
        // HttpClient 不需要显式关闭
        log.debug("OpenAI LLM closed");
    }
}
