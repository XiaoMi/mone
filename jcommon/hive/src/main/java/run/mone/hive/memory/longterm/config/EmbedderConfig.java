package run.mone.hive.memory.longterm.config;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.HashMap;

/**
 * 嵌入模型配置类
 * 支持多种嵌入模型提供商：OpenAI、Hugging Face、Azure OpenAI等
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmbedderConfig {
    
    /**
     * 嵌入模型提供商类型
     */
    public enum Provider {
        OPENAI("openai"),
        AZURE_OPENAI("azure_openai"),
        HUGGINGFACE("huggingface"),
        OLLAMA("ollama"),
        GEMINI("gemini"),
        BEDROCK("bedrock"),
        VERTEXAI("vertexai"),
        TOGETHER("together");
        
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
            throw new IllegalArgumentException("Unknown embedder provider: " + value);
        }
    }
    
    /**
     * 提供商
     */
    @Builder.Default
    private Provider provider = Provider.OPENAI;
    
    /**
     * 模型名称
     */
    @Builder.Default
    private String model = "text-embedding-3-small";
    
    /**
     * API密钥
     */
    private String apiKey;
    
    /**
     * 基础URL
     */
    private String baseUrl;
    
    /**
     * 嵌入向量维度
     */
    @Builder.Default
    private int embeddingDims = 1536;
    
    /**
     * 额外配置
     */
    @Builder.Default
    private Map<String, Object> config = new HashMap<>();
    
    /**
     * 从Map创建配置
     */
    public static EmbedderConfig fromMap(Map<String, Object> configMap) {
        EmbedderConfig.EmbedderConfigBuilder builder = EmbedderConfig.builder();
        
        if (configMap.containsKey("provider")) {
            String providerStr = (String) configMap.get("provider");
            builder.provider(Provider.fromString(providerStr));
        }
        
        if (configMap.containsKey("model")) {
            builder.model((String) configMap.get("model"));
        }
        
        if (configMap.containsKey("apiKey")) {
            builder.apiKey((String) configMap.get("apiKey"));
        }
        
        if (configMap.containsKey("baseUrl")) {
            builder.baseUrl((String) configMap.get("baseUrl"));
        }
        
        if (configMap.containsKey("embeddingDims")) {
            Object dims = configMap.get("embeddingDims");
            if (dims instanceof Number) {
                builder.embeddingDims(((Number) dims).intValue());
            }
        }
        
        if (configMap.containsKey("config")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> config = (Map<String, Object>) configMap.get("config");
            builder.config(config);
        }
        
        return builder.build();
    }
    
    /**
     * 获取OpenAI默认配置
     */
    public static EmbedderConfig openAiDefault() {
        return EmbedderConfig.builder()
                .provider(Provider.OPENAI)
                .model("text-embedding-3-small")
                .embeddingDims(1536)
                .build();
    }
    
    /**
     * 获取Hugging Face默认配置
     */
    public static EmbedderConfig huggingFaceDefault() {
        return EmbedderConfig.builder()
                .provider(Provider.HUGGINGFACE)
                .model("sentence-transformers/all-MiniLM-L6-v2")
                .embeddingDims(384)
                .build();
    }
    
    /**
     * 获取Ollama默认配置
     */
    public static EmbedderConfig ollamaDefault() {
        return EmbedderConfig.builder()
                .provider(Provider.OLLAMA)
                .model("nomic-embed-text")
                .baseUrl("http://localhost:11434")
                .embeddingDims(768)
                .build();
    }
}
