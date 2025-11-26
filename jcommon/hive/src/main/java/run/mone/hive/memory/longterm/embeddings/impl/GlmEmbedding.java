package run.mone.hive.memory.longterm.embeddings.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import run.mone.hive.memory.longterm.config.EmbedderConfig;
import run.mone.hive.memory.longterm.embeddings.EmbeddingBase;
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
import java.util.ArrayList;

/**
 * GLM嵌入模型实现
 * 支持智谱AI的GLM嵌入模型
 */
@Slf4j
@Data
public class GlmEmbedding implements EmbeddingBase {
    private final EmbedderConfig config;
    private final HttpClient httpClient;
    private final Gson gson;
    
    private static final String DEFAULT_BASE_URL = "https://open.bigmodel.cn/api/paas/v4";
    private static final String DEFAULT_MODEL = "embedding-3";
    private static final int DEFAULT_DIMENSIONS = 1024;
    
    public GlmEmbedding(EmbedderConfig config) {
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
        
        if (config.getEmbeddingDims() <= 0) {
            config.setEmbeddingDims(DEFAULT_DIMENSIONS);
        }
        
        log.info("GLM Embedding initialized with model: {} at {}", config.getModel(), config.getBaseUrl());
    }
    
    @Override
    public List<Double> embed(String text, String memoryAction) {
        try {
            // 构建请求
            JsonObject request = new JsonObject();
            request.addProperty("model", config.getModel());
            request.addProperty("input", text);
            
            // 如果配置中指定了维度，添加到请求中
            if (config.getEmbeddingDims() > 0) {
                request.addProperty("dimensions", config.getEmbeddingDims());
            }
            
            // API URL
            String apiUrl = config.getBaseUrl() + "/embeddings";
            
            // 发送HTTP请求
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + config.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .timeout(Duration.ofMinutes(3));

            // 添加自定义请求头
            if (config.getCustomHeaders() != null) {
                config.getCustomHeaders().forEach(requestBuilder::header);
            }

            HttpRequest httpRequest = requestBuilder.build();
            
            HttpResponse<String> response = httpClient.send(httpRequest, 
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                log.error("GLM API error: {} - {}", response.statusCode(), response.body());
                throw new RuntimeException("GLM API error: " + response.statusCode() + " - " + response.body());
            }
            
            // 解析响应
            return parseEmbeddingResponse(response.body());
            
        } catch (Exception e) {
            log.error("Error generating embedding from GLM", e);
            throw new RuntimeException("Failed to generate embedding from GLM", e);
        }
    }
    
    /**
     * 生成文本的嵌入向量 (便捷方法)
     */
    public List<Double> embed(String text) {
        return embed(text, "add");
    }
    
    @Override
    public List<List<Double>> embedBatch(List<String> texts, String memoryAction) {
        try {
            // 构建批量请求
            JsonObject request = new JsonObject();
            request.addProperty("model", config.getModel());
            
            // 将文本列表转换为JsonArray
            JsonArray inputArray = new JsonArray();
            for (String text : texts) {
                inputArray.add(text);
            }
            request.add("input", inputArray);
            
            // 如果配置中指定了维度，添加到请求中
            if (config.getEmbeddingDims() > 0) {
                request.addProperty("dimensions", config.getEmbeddingDims());
            }
            
            // API URL
            String apiUrl = config.getBaseUrl() + "/embeddings";
            
            // 发送HTTP请求
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + config.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .timeout(Duration.ofMinutes(5));

            // 添加自定义请求头
            if (config.getCustomHeaders() != null) {
                config.getCustomHeaders().forEach(requestBuilder::header);
            }

            HttpRequest httpRequest = requestBuilder.build();
            
            HttpResponse<String> response = httpClient.send(httpRequest, 
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                log.error("GLM API error: {} - {}", response.statusCode(), response.body());
                throw new RuntimeException("GLM API error: " + response.statusCode() + " - " + response.body());
            }
            
            // 解析批量响应
            return parseBatchEmbeddingResponse(response.body());
            
        } catch (Exception e) {
            log.error("Error generating batch embeddings from GLM", e);
            // 如果批量请求失败，回退到单个请求
            log.warn("Falling back to individual embedding requests");
            List<List<Double>> embeddings = new ArrayList<>();
            for (String text : texts) {
                embeddings.add(embed(text, memoryAction));
            }
            return embeddings;
        }
    }
    
    @Override
    public int getDimensions() {
        return config.getEmbeddingDims();
    }
    
    @Override
    public EmbedderConfig getConfig() {
        return config;
    }
    
    /**
     * 解析单个嵌入响应
     */
    private List<Double> parseEmbeddingResponse(String responseBody) {
        try {
            JsonObject response = JsonParser.parseString(responseBody).getAsJsonObject();
            
            // 检查错误
            if (response.has("error")) {
                JsonObject error = response.getAsJsonObject("error");
                String errorMessage = error.has("message") ? error.get("message").getAsString() : "Unknown error";
                throw new RuntimeException("GLM API error: " + errorMessage);
            }
            
            // 提取嵌入向量
            if (response.has("data")) {
                JsonArray dataArray = response.getAsJsonArray("data");
                if (dataArray.size() > 0) {
                    JsonObject firstData = dataArray.get(0).getAsJsonObject();
                    if (firstData.has("embedding")) {
                        JsonArray embeddingArray = firstData.getAsJsonArray("embedding");
                        List<Double> embedding = new ArrayList<>();
                        
                        for (int i = 0; i < embeddingArray.size(); i++) {
                            embedding.add(embeddingArray.get(i).getAsDouble());
                        }
                        
                        return embedding;
                    }
                }
            }
            
            throw new RuntimeException("No valid embedding found in GLM response");
            
        } catch (Exception e) {
            log.error("Error parsing GLM embedding response: {}", responseBody, e);
            throw new RuntimeException("Failed to parse GLM embedding response", e);
        }
    }
    
    /**
     * 解析批量嵌入响应
     */
    private List<List<Double>> parseBatchEmbeddingResponse(String responseBody) {
        try {
            JsonObject response = JsonParser.parseString(responseBody).getAsJsonObject();
            
            // 检查错误
            if (response.has("error")) {
                JsonObject error = response.getAsJsonObject("error");
                String errorMessage = error.has("message") ? error.get("message").getAsString() : "Unknown error";
                throw new RuntimeException("GLM API error: " + errorMessage);
            }
            
            List<List<Double>> embeddings = new ArrayList<>();
            
            // 提取嵌入向量列表
            if (response.has("data")) {
                JsonArray dataArray = response.getAsJsonArray("data");
                
                for (int i = 0; i < dataArray.size(); i++) {
                    JsonObject dataItem = dataArray.get(i).getAsJsonObject();
                    if (dataItem.has("embedding")) {
                        JsonArray embeddingArray = dataItem.getAsJsonArray("embedding");
                        List<Double> embedding = new ArrayList<>();
                        
                        for (int j = 0; j < embeddingArray.size(); j++) {
                            embedding.add(embeddingArray.get(j).getAsDouble());
                        }
                        
                        embeddings.add(embedding);
                    }
                }
            }
            
            if (embeddings.isEmpty()) {
                throw new RuntimeException("No valid embeddings found in GLM batch response");
            }
            
            return embeddings;
            
        } catch (Exception e) {
            log.error("Error parsing GLM batch embedding response: {}", responseBody, e);
            throw new RuntimeException("Failed to parse GLM batch embedding response", e);
        }
    }
    
    /**
     * 获取支持的模型列表
     */
    public static List<String> getSupportedModels() {
        return List.of(
            "embedding-3",
            "embedding-2"
        );
    }
    
    /**
     * 获取模型的默认维度
     */
    public static int getModelDimensions(String model) {
        switch (model.toLowerCase()) {
            case "embedding-3":
                return 1024;
            case "embedding-2":
                return 1024;
            default:
                return 1024; // 默认维度
        }
    }
    
    /**
     * 验证API密钥
     */
    public boolean validateApiKey() {
        try {
            // 使用简单文本测试API
            embed("test", "add");
            return true;
        } catch (Exception e) {
            log.error("API key validation failed", e);
            return false;
        }
    }
    
    /**
     * 测试连接
     */
    public boolean testConnection() {
        try {
            List<Double> embedding = embed("Hello, world!", "add");
            return embedding != null && !embedding.isEmpty() && embedding.size() == getDimensions();
        } catch (Exception e) {
            log.error("Connection test failed", e);
            return false;
        }
    }
}
