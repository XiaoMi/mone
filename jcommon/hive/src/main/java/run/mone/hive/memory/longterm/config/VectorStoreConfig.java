package run.mone.hive.memory.longterm.config;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.HashMap;

/**
 * 向量存储配置类
 * 支持多种向量存储后端：Qdrant、Chroma、Weaviate、Pinecone等
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VectorStoreConfig {
    
    /**
     * 向量存储提供商类型
     */
    public enum Provider {
        CHROMA("chroma");

        private final String value;
        
        Provider(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static Provider fromString(String value) {
            for (Provider provider : Provider.values()) {
                if (provider.value.equalsIgnoreCase(value)) {
                    return provider;
                }
            }
            throw new IllegalArgumentException("Unknown provider: " + value);
        }
    }
    
    /**
     * 提供商
     */
    @Builder.Default
    private Provider provider = Provider.CHROMA;
    
    /**
     * 集合名称
     */
    @Builder.Default
    private String collectionName = "mem1";
    
    /**
     * 嵌入向量维度
     */
    @Builder.Default
    private int embeddingModelDims = 1536;

    private String model;
    
    /**
     * 连接配置
     */
    @Builder.Default
    private Map<String, Object> config = new HashMap<>();
    
    /**
     * 存储路径 (对于本地存储如FAISS)
     */
    private String path;
    
    /**
     * 主机地址
     */
    @Builder.Default
    private String host = "localhost";
    
    /**
     * 端口
     */
    @Builder.Default
    private int port = 6333;
    
    /**
     * API密钥
     */
    private String apiKey;
    
    /**
     * 数据库名称 (对于支持数据库概念的存储)
     */
    private String database;

    /**
     * 嵌入函数名称
     */
    private String embeddingFunction;

    /**
     * 基础URL
     */
    private String baseUrl;
    
    /**
     * 从Map创建配置
     */
    public static VectorStoreConfig fromMap(Map<String, Object> configMap) {
        var builder = VectorStoreConfig.builder();

        if (configMap.containsKey("model")) {
            builder.model(configMap.get("model").toString());
        }
        
        if (configMap.containsKey("provider")) {
            String providerStr = (String) configMap.get("provider");
            builder.provider(Provider.fromString(providerStr));
        }
        
        if (configMap.containsKey("collectionName")) {
            builder.collectionName((String) configMap.get("collectionName"));
        }
        
        if (configMap.containsKey("embeddingModelDims")) {
            builder.embeddingModelDims((Integer) configMap.get("embeddingModelDims"));
        }
        
        if (configMap.containsKey("host")) {
            builder.host((String) configMap.get("host"));
        }
        
        if (configMap.containsKey("port")) {
            builder.port((Integer) configMap.get("port"));
        }
        
        if (configMap.containsKey("apiKey")) {
            builder.apiKey((String) configMap.get("apiKey"));
        }
        
        if (configMap.containsKey("database")) {
            builder.database((String) configMap.get("database"));
        }
        
        if (configMap.containsKey("path")) {
            builder.path((String) configMap.get("path"));
        }
        
        if (configMap.containsKey("config")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> config = (Map<String, Object>) configMap.get("config");
            builder.config(config);
        }

        if (configMap.containsKey("embeddingFunction")) {
            builder.embeddingFunction((String) configMap.get("embeddingFunction"));
        }

        if (configMap.containsKey("baseUrl")) {
            builder.baseUrl((String) configMap.get("baseUrl"));
        }

        return builder.build();
    }
    


    /**
     * 获取Chroma默认配置（本地嵌入式）
     */
    public static VectorStoreConfig chromaDefault() {
        return VectorStoreConfig.builder()
                .provider(Provider.CHROMA)
                .host("localhost")
                .port(8000)
                .path("./data/chroma")
                .collectionName("mem0")
                .embeddingModelDims(1536)
                .build();
    }

    /**
     * 获取本地嵌入式Chroma配置（测试用）
     */
    public static VectorStoreConfig chromaEmbedded() {
        return VectorStoreConfig.builder()
                .provider(Provider.CHROMA)
                .host("localhost")
                .path("./data/chroma_embedded")
                .collectionName("embedded_mem0")
                .embeddingModelDims(384)
                .build();
    }
}
