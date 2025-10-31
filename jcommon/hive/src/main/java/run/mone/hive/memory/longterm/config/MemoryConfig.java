package run.mone.hive.memory.longterm.config;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;

import java.util.Map;
import java.io.File;

/**
 * Memory配置类 - 复刻mem0的MemoryConfig
 * 支持向量存储、LLM、嵌入模型、图存储等配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoryConfig {
    
    /**
     * 向量存储配置
     */
    @Builder.Default
    private VectorStoreConfig vectorStore = VectorStoreConfig.builder().build();
    
    /**
     * LLM配置
     */
    private LlmConfig llm;
    
    /**
     * 嵌入模型配置
     */
    @Builder.Default
    private EmbedderConfig embedder = EmbedderConfig.builder().build();
    
    /**
     * 历史数据库路径
     */
    @Builder.Default
    private String historyDbPath = System.getProperty("user.home") + File.separator + ".mem0" + File.separator + "history.db";
    
    /**
     * 图存储配置
     */
    @Builder.Default
    private GraphStoreConfig graphStore = GraphStoreConfig.builder().build();
    
    /**
     * API版本
     */
    @Builder.Default
    private String version = "v1.1";
    
    /**
     * 自定义事实提取提示词
     */
    private String customFactExtractionPrompt;
    
    /**
     * 自定义更新记忆提示词
     */
    private String customUpdateMemoryPrompt;
    
    /**
     * 获取默认配置
     */
    public static MemoryConfig getDefault() {
        return MemoryConfig.builder().build();
    }
    
    /**
     * 从Map创建配置
     */
    public static MemoryConfig fromMap(Map<String, Object> configMap) {
        MemoryConfig.MemoryConfigBuilder builder = MemoryConfig.builder();

        if (configMap.containsKey("llm")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> llmMap = (Map<String, Object>) configMap.get("llm");
            builder.llm(LlmConfig.fromMap(llmMap));
        }
        
        if (configMap.containsKey("vectorStore")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> vectorStoreMap = (Map<String, Object>) configMap.get("vectorStore");
            builder.vectorStore(VectorStoreConfig.fromMap(vectorStoreMap));
        }
        

        if (configMap.containsKey("embedder")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> embedderMap = (Map<String, Object>) configMap.get("embedder");
            builder.embedder(EmbedderConfig.fromMap(embedderMap));
        }
        
        if (configMap.containsKey("graphStore")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> graphStoreMap = (Map<String, Object>) configMap.get("graphStore");
            builder.graphStore(GraphStoreConfig.fromMap(graphStoreMap));
        }
        
        if (configMap.containsKey("historyDbPath")) {
            builder.historyDbPath((String) configMap.get("historyDbPath"));
        }
        
        if (configMap.containsKey("version")) {
            builder.version((String) configMap.get("version"));
        }
        
        if (configMap.containsKey("customFactExtractionPrompt")) {
            builder.customFactExtractionPrompt((String) configMap.get("customFactExtractionPrompt"));
        }
        
        if (configMap.containsKey("customUpdateMemoryPrompt")) {
            builder.customUpdateMemoryPrompt((String) configMap.get("customUpdateMemoryPrompt"));
        }
        
        return builder.build();
    }
}
