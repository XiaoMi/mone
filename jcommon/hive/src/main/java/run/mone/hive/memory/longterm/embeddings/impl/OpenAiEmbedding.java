package run.mone.hive.memory.longterm.embeddings.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.Data;

import run.mone.hive.memory.longterm.config.EmbedderConfig;
import run.mone.hive.memory.longterm.embeddings.EmbeddingBase;

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
 * OpenAI嵌入实现
 * 支持text-embedding-3-small、text-embedding-3-large等模型
 */
@Slf4j
@Data
public class OpenAiEmbedding implements EmbeddingBase {
    
    private final EmbedderConfig config;
    private final HttpClient httpClient;
    private final Gson gson;
    private final String apiKey;
    private final String baseUrl;
    
    public OpenAiEmbedding(EmbedderConfig config) {
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
    public List<Double> embed(String text, String memoryAction) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Text to embed cannot be null or empty");
        }
        
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("input", text);
            requestBody.put("model", config.getModel());
            
            // 某些模型支持dimensions参数
            if (supportsCustomDimensions()) {
                requestBody.put("dimensions", config.getEmbeddingDims());
            }
            
            String requestJson = gson.toJson(requestBody);
            
            // 发送请求
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/embeddings"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .timeout(Duration.ofMinutes(1))
                .build();
                
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                String errorMsg = String.format("OpenAI Embedding API request failed with status %d: %s", 
                    response.statusCode(), response.body());
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            // 解析响应
            return parseEmbeddingResponse(response.body());
            
        } catch (Exception e) {
            log.error("Error generating embedding for text: {}", text.substring(0, Math.min(50, text.length())), e);
            throw new RuntimeException("Failed to generate embedding", e);
        }
    }
    
    @Override
    public List<List<Double>> embedBatch(List<String> texts, String memoryAction) {
        if (texts == null || texts.isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            // 构建批量请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("input", texts);
            requestBody.put("model", config.getModel());
            
            if (supportsCustomDimensions()) {
                requestBody.put("dimensions", config.getEmbeddingDims());
            }
            
            String requestJson = gson.toJson(requestBody);
            
            // 发送请求
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/embeddings"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .timeout(Duration.ofMinutes(2))
                .build();
                
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                String errorMsg = String.format("OpenAI Batch Embedding API request failed with status %d: %s", 
                    response.statusCode(), response.body());
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            // 解析批量响应
            return parseBatchEmbeddingResponse(response.body());
            
        } catch (Exception e) {
            log.error("Error generating batch embeddings for {} texts", texts.size(), e);
            throw new RuntimeException("Failed to generate batch embeddings", e);
        }
    }
    
    @Override
    public int getDimensions() {
        return config.getEmbeddingDims();
    }
    
    private List<Double> parseEmbeddingResponse(String responseJson) {
        try {
            JsonObject jsonResponse = gson.fromJson(responseJson, JsonObject.class);
            
            if (jsonResponse.has("error")) {
                JsonObject error = jsonResponse.getAsJsonObject("error");
                String errorMsg = String.format("OpenAI Embedding API error: %s", error.get("message").getAsString());
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            JsonArray data = jsonResponse.getAsJsonArray("data");
            if (data.size() == 0) {
                throw new RuntimeException("No embeddings in OpenAI response");
            }
            
            JsonObject firstEmbedding = data.get(0).getAsJsonObject();
            JsonArray embedding = firstEmbedding.getAsJsonArray("embedding");
            
            List<Double> result = new ArrayList<>();
            for (int i = 0; i < embedding.size(); i++) {
                result.add(embedding.get(i).getAsDouble());
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("Error parsing OpenAI embedding response: {}", responseJson, e);
            throw new RuntimeException("Failed to parse OpenAI embedding response", e);
        }
    }
    
    private List<List<Double>> parseBatchEmbeddingResponse(String responseJson) {
        try {
            JsonObject jsonResponse = gson.fromJson(responseJson, JsonObject.class);
            
            if (jsonResponse.has("error")) {
                JsonObject error = jsonResponse.getAsJsonObject("error");
                String errorMsg = String.format("OpenAI Batch Embedding API error: %s", error.get("message").getAsString());
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            JsonArray data = jsonResponse.getAsJsonArray("data");
            List<List<Double>> results = new ArrayList<>();
            
            for (int i = 0; i < data.size(); i++) {
                JsonObject embeddingObj = data.get(i).getAsJsonObject();
                JsonArray embedding = embeddingObj.getAsJsonArray("embedding");
                
                List<Double> embeddingList = new ArrayList<>();
                for (int j = 0; j < embedding.size(); j++) {
                    embeddingList.add(embedding.get(j).getAsDouble());
                }
                
                results.add(embeddingList);
            }
            
            return results;
            
        } catch (Exception e) {
            log.error("Error parsing OpenAI batch embedding response: {}", responseJson, e);
            throw new RuntimeException("Failed to parse OpenAI batch embedding response", e);
        }
    }
    
    private boolean supportsCustomDimensions() {
        String model = config.getModel().toLowerCase();
        return model.contains("text-embedding-3");
    }
    
    /**
     * 检查API密钥的有效性
     */
    public boolean validateApiKey() {
        try {
            embed("test", "add");
            return true;
        } catch (Exception e) {
            log.warn("API key validation failed", e);
            return false;
        }
    }
    
    /**
     * 获取可用的嵌入模型列表
     */
    public List<String> getAvailableModels() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/models"))
                .header("Authorization", "Bearer " + apiKey)
                .GET()
                .build();
                
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
                JsonArray models = jsonResponse.getAsJsonArray("data");
                
                List<String> embeddingModels = new ArrayList<>();
                for (int i = 0; i < models.size(); i++) {
                    JsonElement element = models.get(i);
                    JsonObject model = element.getAsJsonObject();
                    String id = model.get("id").getAsString();
                    if (id.contains("embedding")) {
                        embeddingModels.add(id);
                    }
                }
                return embeddingModels;
            }
            
        } catch (Exception e) {
            log.error("Error getting available embedding models", e);
        }
        
        return List.of();
    }
    
    @Override
    public void close() {
        // HttpClient 不需要显式关闭
        log.debug("OpenAI Embedding closed");
    }
}
