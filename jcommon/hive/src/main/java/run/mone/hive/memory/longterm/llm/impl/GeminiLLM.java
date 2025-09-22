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
 * Google Gemini LLM实现
 * 支持Gemini Pro和其他Gemini模型
 */
@Slf4j
@Data
public class GeminiLLM implements LLMBase {
    private final LlmConfig config;
    private final HttpClient httpClient;
    private final Gson gson;
    
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent";
    private static final String DEFAULT_MODEL = "gemini-1.5-pro-latest";
    
    public GeminiLLM(LlmConfig config) {
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
        log.info("Gemini LLM initialized with model: {}", config.getModel());
    }
    
    @Override
    public String generateResponse(List<Map<String, Object>> messages, String responseFormat) {
        try {
            // 构建请求
            JsonObject request = buildRequest(messages);
            
            // 构建API URL
            String apiUrl = String.format(GEMINI_API_URL, config.getModel()) + "?key=" + getApiKey();
            
            // 发送HTTP请求
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
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
                log.error("Gemini API error: {} - {}", response.statusCode(), response.body());
                throw new RuntimeException("Gemini API error: " + response.statusCode());
            }
            
            // 解析响应
            return parseResponse(response.body());
            
        } catch (Exception e) {
            log.error("Error generating response from Gemini", e);
            throw new RuntimeException("Failed to generate response from Gemini", e);
        }
    }
    
    @Override
    public boolean supportsVision() {
        String model = config.getModel().toLowerCase();
        return model.contains("gemini-1.5-pro") || model.contains("gemini-pro-vision");
    }
    
    private JsonObject buildRequest(List<Map<String, Object>> messages) {
        JsonObject request = new JsonObject();
        
        // 配置参数
        JsonObject generationConfig = new JsonObject();
        
        Double temperature = config.getTemperature();
        if (temperature != null) {
            generationConfig.addProperty("temperature", temperature);
        }
        
        Integer maxTokens = config.getMaxTokens();
        if (maxTokens != null) {
            generationConfig.addProperty("maxOutputTokens", maxTokens);
        }
        
        Double topP = config.getTopP();
        if (topP != null) {
            generationConfig.addProperty("topP", topP);
        }
        request.add("generationConfig", generationConfig);
        
        // 构建内容数组
        JsonArray contents = new JsonArray();
        
        for (Map<String, Object> message : messages) {
            String role = (String) message.get("role");
            String content = (String) message.get("content");
            
            if ("system".equals(role)) {
                // Gemini系统指令单独处理
                JsonObject systemInstruction = new JsonObject();
                JsonArray parts = new JsonArray();
                JsonObject part = new JsonObject();
                part.addProperty("text", content);
                parts.add(part);
                systemInstruction.add("parts", parts);
                request.add("systemInstruction", systemInstruction);
            } else {
                // 转换角色名称
                String geminiRole = convertRole(role);
                
                JsonObject contentObj = new JsonObject();
                contentObj.addProperty("role", geminiRole);
                
                JsonArray parts = new JsonArray();
                JsonObject part = new JsonObject();
                part.addProperty("text", content);
                parts.add(part);
                contentObj.add("parts", parts);
                
                contents.add(contentObj);
            }
        }
        
        request.add("contents", contents);
        
        return request;
    }
    
    private String convertRole(String role) {
        switch (role) {
            case "user":
                return "user";
            case "assistant":
                return "model";
            default:
                return "user";
        }
    }
    
    private String parseResponse(String responseBody) {
        try {
            JsonObject response = JsonParser.parseString(responseBody).getAsJsonObject();
            
            // 检查错误
            if (response.has("error")) {
                JsonObject error = response.getAsJsonObject("error");
                String errorMessage = error.get("message").getAsString();
                throw new RuntimeException("Gemini API error: " + errorMessage);
            }
            
            // 提取内容
            if (response.has("candidates")) {
                JsonArray candidates = response.getAsJsonArray("candidates");
                if (candidates.size() > 0) {
                    JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                    if (firstCandidate.has("content")) {
                        JsonObject content = firstCandidate.getAsJsonObject("content");
                        if (content.has("parts")) {
                            JsonArray parts = content.getAsJsonArray("parts");
                            if (parts.size() > 0) {
                                JsonObject firstPart = parts.get(0).getAsJsonObject();
                                if (firstPart.has("text")) {
                                    return firstPart.get("text").getAsString();
                                }
                            }
                        }
                    }
                }
            }
            
            throw new RuntimeException("No valid content found in Gemini response");
            
        } catch (Exception e) {
            log.error("Error parsing Gemini response: {}", responseBody, e);
            throw new RuntimeException("Failed to parse Gemini response", e);
        }
    }
    
    private String getApiKey() {
        String apiKey = config.getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = System.getenv("GOOGLE_API_KEY");
        }
        
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("Gemini API key not found. Set apiKey in config or GOOGLE_API_KEY environment variable");
        }
        
        return apiKey;
    }
    
    /**
     * 获取支持的模型列表
     */
    public static List<String> getSupportedModels() {
        return List.of(
            "gemini-1.5-pro-latest",
            "gemini-1.5-pro",
            "gemini-1.5-flash-latest", 
            "gemini-1.5-flash",
            "gemini-1.0-pro-latest",
            "gemini-1.0-pro",
            "gemini-pro-vision"
        );
    }
    
    /**
     * 验证模型是否支持
     */
    public static boolean isModelSupported(String model) {
        return getSupportedModels().contains(model);
    }
}