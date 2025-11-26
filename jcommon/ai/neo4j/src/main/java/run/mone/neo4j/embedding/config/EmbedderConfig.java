package run.mone.neo4j.embedding.config;

import lombok.Data;
import java.util.Map;

/**
 * 嵌入模型配置类
 * 用于配置各种嵌入模型的参数
 */
@Data
public class EmbedderConfig {
    
    /**
     * 模型名称
     */
    private String model;
    
    /**
     * API基础URL
     */
    private String baseUrl;
    
    /**
     * API密钥
     */
    private String apiKey;
    
    /**
     * 嵌入向量维度
     */
    private Integer embeddingDims;
    
    /**
     * 自定义请求头
     */
    private Map<String, String> customHeaders;
    
    /**
     * 连接超时时间（秒）
     */
    private Integer connectTimeout;
    
    /**
     * 读取超时时间（秒）
     */
    private Integer readTimeout;
    
    /**
     * 最大重试次数
     */
    private Integer maxRetries;
    
    /**
     * 批处理大小
     */
    private Integer batchSize;
    
    public EmbedderConfig() {
        // 设置默认值
        this.connectTimeout = 30;
        this.readTimeout = 180;
        this.maxRetries = 3;
        this.batchSize = 100;
    }
    
    public EmbedderConfig(String model, String baseUrl) {
        this();
        this.model = model;
        this.baseUrl = baseUrl;
    }
    
    public EmbedderConfig(String model, String baseUrl, String apiKey) {
        this(model, baseUrl);
        this.apiKey = apiKey;
    }
}
