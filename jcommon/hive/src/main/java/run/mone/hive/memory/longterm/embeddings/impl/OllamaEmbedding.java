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
 * Ollama嵌入模型实现
 * 支持本地部署的Ollama嵌入模型
 */
@Slf4j
@Data
public class OllamaEmbedding implements EmbeddingBase {
    private final EmbedderConfig config;
    private final HttpClient httpClient;
    private final Gson gson;
    
    private static final String DEFAULT_BASE_URL = "http://localhost:11434";
    private static final String DEFAULT_MODEL = "embeddinggemma";
    
    public OllamaEmbedding(EmbedderConfig config) {
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
        
        log.info("Ollama Embedding initialized with model: {} at {}", config.getModel(), config.getBaseUrl());
    }
    
    @Override
    public List<Double> embed(String text, String memoryAction) {
        try {
            // 构建请求
            JsonObject request = new JsonObject();
            request.addProperty("model", config.getModel());
            request.addProperty("input", text);
            
            // API URL
            String apiUrl = config.getBaseUrl() + "/api/embed";
            
            // 发送HTTP请求
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
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
                log.error("Ollama API error: {} - {}", response.statusCode(), response.body());
                throw new RuntimeException("Ollama API error: " + response.statusCode());
            }
            
            // 解析响应
            return parseEmbeddingResponse(response.body());
            
        } catch (Exception e) {
            log.error("Error generating embedding from Ollama", e);
            throw new RuntimeException("Failed to generate embedding from Ollama", e);
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
        List<List<Double>> embeddings = new ArrayList<>();
        
        // Ollama的嵌入API一次只能处理一个文本
        for (String text : texts) {
            embeddings.add(embed(text, "add"));
        }
        
        return embeddings;
    }
    
    @Override
    public int getDimensions() {
        Integer dims = config.getEmbeddingDims();
        if (dims != null) {
            return dims;
        }
        
        // 根据模型返回默认维度
        String model = config.getModel().toLowerCase();
        if (model.contains("embeddinggemma")) {
            return 3584;
        } else if (model.contains("nomic-embed-text")) {
            return 768;
        } else if (model.contains("mxbai-embed-large")) {
            return 1024;
        } else if (model.contains("all-minilm")) {
            return 384;
        } else {
            return 768; // 默认维度
        }
    }
    
    private List<Double> parseEmbeddingResponse(String responseBody) {
        try {
            JsonObject response = JsonParser.parseString(responseBody).getAsJsonObject();
            
            // 检查错误
            if (response.has("error")) {
                String errorMessage = response.get("error").getAsString();
                throw new RuntimeException("Ollama API error: " + errorMessage);
            }
            
            // 提取嵌入向量 - Ollama返回的是embeddings数组，取第一个
            if (response.has("embeddings")) {
                JsonArray embeddingsArray = response.getAsJsonArray("embeddings");
                if (embeddingsArray.size() > 0) {
                    JsonArray embeddingArray = embeddingsArray.get(0).getAsJsonArray();
                    List<Double> embedding = new ArrayList<>();
                    
                    for (int i = 0; i < embeddingArray.size(); i++) {
                        embedding.add(embeddingArray.get(i).getAsDouble());
                    }
                    
                    return embedding;
                }
            }
            
            // 兼容旧格式 - 单个embedding字段
            if (response.has("embedding")) {
                JsonArray embeddingArray = response.getAsJsonArray("embedding");
                List<Double> embedding = new ArrayList<>();
                
                for (int i = 0; i < embeddingArray.size(); i++) {
                    embedding.add(embeddingArray.get(i).getAsDouble());
                }
                
                return embedding;
            }
            
            throw new RuntimeException("No valid embedding found in Ollama response");
            
        } catch (Exception e) {
            log.error("Error parsing Ollama embedding response: {}", responseBody, e);
            throw new RuntimeException("Failed to parse Ollama embedding response", e);
        }
    }
    
    /**
     * 获取可用模型列表
     */
    public List<String> getAvailableModels() {
        try {
            String apiUrl = config.getBaseUrl() + "/api/tags";
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .timeout(Duration.ofSeconds(30))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonObject responseObj = JsonParser.parseString(response.body()).getAsJsonObject();
                if (responseObj.has("models")) {
                    JsonArray models = responseObj.getAsJsonArray("models");
                    List<String> modelNames = new ArrayList<>();
                    
                    for (int i = 0; i < models.size(); i++) {
                        JsonObject model = models.get(i).getAsJsonObject();
                        String name = model.get("name").getAsString();
                        // 过滤出嵌入模型
                        if (isEmbeddingModel(name)) {
                            modelNames.add(name);
                        }
                    }
                    
                    return modelNames;
                }
            }
            
            log.warn("Failed to get available models from Ollama: {}", response.statusCode());
            return getCommonEmbeddingModels();
            
        } catch (Exception e) {
            log.error("Error getting available models", e);
            return getCommonEmbeddingModels();
        }
    }
    
    /**
     * 检查是否为嵌入模型
     */
    private boolean isEmbeddingModel(String modelName) {
        String name = modelName.toLowerCase();
        return name.contains("embed") || 
               name.contains("nomic") ||
               name.contains("mxbai") ||
               name.contains("bge") ||
               name.contains("gemma") ||
               name.contains("sentence");
    }
    
    /**
     * 获取常见嵌入模型列表
     */
    public static List<String> getCommonEmbeddingModels() {
        return List.of(
            "embeddinggemma",
            "nomic-embed-text",
            "mxbai-embed-large",
            "all-minilm",
            "bge-base",
            "bge-large",
            "bge-small"
        );
    }
    
    /**
     * 检查模型是否可用
     */
    public boolean isModelAvailable(String modelName) {
        List<String> availableModels = getAvailableModels();
        return availableModels.contains(modelName);
    }
    
    /**
     * 获取模型维度信息
     */
    public static int getModelDimensions(String model) {
        switch (model.toLowerCase()) {
            case "embeddinggemma":
                return 3584;
            case "nomic-embed-text":
            case "bge-base":
                return 768;
            case "mxbai-embed-large":
            case "bge-large":
                return 1024;
            case "all-minilm":
            case "bge-small":
                return 384;
            default:
                return 768; // 默认
        }
    }
}