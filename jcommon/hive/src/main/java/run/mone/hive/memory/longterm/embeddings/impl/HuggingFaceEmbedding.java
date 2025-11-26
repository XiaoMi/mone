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
 * HuggingFace嵌入模型实现
 * 支持HuggingFace Inference API
 */
@Slf4j
@Data
public class HuggingFaceEmbedding implements EmbeddingBase {
    private final EmbedderConfig config;
    private final HttpClient httpClient;
    private final Gson gson;
    
    private static final String HF_API_URL = "https://api-inference.huggingface.co/pipeline/feature-extraction/";
    private static final String DEFAULT_MODEL = "sentence-transformers/all-MiniLM-L6-v2";
    
    public HuggingFaceEmbedding(EmbedderConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
        this.gson = new Gson();
        
        // 设置默认模型
        if (config.getModel() == null || config.getModel().isEmpty()) {
            config.setModel(DEFAULT_MODEL);
        }
        
        log.info("HuggingFace Embedding initialized with model: {}", config.getModel());
    }
    
    @Override
    public List<Double> embed(String text, String memoryAction) {
        try {
            // 构建请求
            JsonObject request = new JsonObject();
            request.addProperty("inputs", text);
            
            // 构建API URL
            String apiUrl = HF_API_URL + config.getModel();
            
            // 发送HTTP请求
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .timeout(Duration.ofMinutes(2));

            // 添加自定义请求头
            if (config.getCustomHeaders() != null) {
                config.getCustomHeaders().forEach(requestBuilder::header);
            }

            HttpRequest httpRequest = requestBuilder.build();
            
            HttpResponse<String> response = httpClient.send(httpRequest, 
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                log.error("HuggingFace API error: {} - {}", response.statusCode(), response.body());
                throw new RuntimeException("HuggingFace API error: " + response.statusCode());
            }
            
            // 解析响应
            return parseEmbeddingResponse(response.body());
            
        } catch (Exception e) {
            log.error("Error generating embedding from HuggingFace", e);
            throw new RuntimeException("Failed to generate embedding from HuggingFace", e);
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
        if (model.contains("all-minilm-l6-v2")) {
            return 384;
        } else if (model.contains("all-mpnet-base-v2")) {
            return 768;
        } else if (model.contains("distilbert")) {
            return 768;
        } else if (model.contains("roberta")) {
            return 768;
        } else {
            return 384; // 默认维度
        }
    }
    
    private List<Double> parseEmbeddingResponse(String responseBody) {
        try {
            // HuggingFace返回的是二维数组，我们取第一个向量
            JsonArray response = JsonParser.parseString(responseBody).getAsJsonArray();
            
            List<Double> embedding = new ArrayList<>();
            if (response.size() > 0) {
                JsonArray firstEmbedding = response.get(0).getAsJsonArray();
                for (int i = 0; i < firstEmbedding.size(); i++) {
                    embedding.add(firstEmbedding.get(i).getAsDouble());
                }
            }
            
            if (embedding.isEmpty()) {
                throw new RuntimeException("No valid embedding found in HuggingFace response");
            }
            
            return embedding;
            
        } catch (Exception e) {
            log.error("Error parsing HuggingFace embedding response: {}", responseBody, e);
            throw new RuntimeException("Failed to parse HuggingFace embedding response", e);
        }
    }
    
    private String getApiKey() {
        String apiKey = config.getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = System.getenv("HUGGINGFACE_API_KEY");
        }
        
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("HuggingFace API key not found. Set apiKey in config or HUGGINGFACE_API_KEY environment variable");
        }
        
        return apiKey;
    }
    
    /**
     * 获取支持的模型列表
     */
    public static List<String> getSupportedModels() {
        return List.of(
            "sentence-transformers/all-MiniLM-L6-v2",
            "sentence-transformers/all-mpnet-base-v2",
            "sentence-transformers/paraphrase-MiniLM-L6-v2",
            "sentence-transformers/distilbert-base-nli-mean-tokens",
            "sentence-transformers/roberta-base-nli-mean-tokens",
            "sentence-transformers/all-MiniLM-L12-v2",
            "sentence-transformers/multi-qa-MiniLM-L6-cos-v1",
            "sentence-transformers/all-distilroberta-v1",
            "jinaai/jina-embeddings-v2-base-en",
            "BAAI/bge-small-en-v1.5",
            "BAAI/bge-base-en-v1.5",
            "BAAI/bge-large-en-v1.5"
        );
    }
    
    /**
     * 验证模型是否支持
     */
    public static boolean isModelSupported(String model) {
        return getSupportedModels().contains(model);
    }
    
    /**
     * 获取模型维度信息
     */
    public static int getModelDimensions(String model) {
        switch (model.toLowerCase()) {
            case "sentence-transformers/all-minilm-l6-v2":
            case "sentence-transformers/paraphrase-minilm-l6-v2":
            case "sentence-transformers/multi-qa-minilm-l6-cos-v1":
            case "baai/bge-small-en-v1.5":
                return 384;
            case "sentence-transformers/all-mpnet-base-v2":
            case "sentence-transformers/distilbert-base-nli-mean-tokens":
            case "sentence-transformers/roberta-base-nli-mean-tokens":
            case "sentence-transformers/all-minilm-l12-v2":
            case "sentence-transformers/all-distilroberta-v1":
            case "jinaai/jina-embeddings-v2-base-en":
            case "baai/bge-base-en-v1.5":
                return 768;
            case "baai/bge-large-en-v1.5":
                return 1024;
            default:
                return 384; // 默认
        }
    }
}