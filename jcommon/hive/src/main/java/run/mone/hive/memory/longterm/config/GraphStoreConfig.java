package run.mone.hive.memory.longterm.config;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.HashMap;

/**
 * 图存储配置类
 * 支持多种图数据库：Neo4j、Kuzu、Memgraph等
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphStoreConfig {
    
    /**
     * 图存储提供商类型
     */
    public enum Provider {
        NEO4J("neo4j"),
       ;
        
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
            throw new IllegalArgumentException("Unknown graph store provider: " + value);
        }
    }
    
    /**
     * 提供商
     */
    private Provider provider;
    
    /**
     * 连接URL
     */
    private String url;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 数据库名称
     */
    private String database;
    
    /**
     * 是否启用
     */
    @Builder.Default
    private boolean enabled = false;
    
    /**
     * 额外配置
     */
    @Builder.Default
    private Map<String, Object> config = new HashMap<>();

    /**
     * LLM配置
     */
    private LlmConfig llm;

    /**
     * 嵌入模型配置
     */
    private EmbedderConfig embedder;

    /**
     * 自定义提示词
     */
    private String customPrompt;

    /**
     * 相似度阈值
     */
    private double threshold;


    /**
     * 从Map创建配置
     */
    public static GraphStoreConfig fromMap(Map<String, Object> configMap) {
        GraphStoreConfig.GraphStoreConfigBuilder builder = GraphStoreConfig.builder();
        
        if (configMap.containsKey("provider")) {
            String providerStr = (String) configMap.get("provider");
            builder.provider(Provider.fromString(providerStr));
        }
        
        if (configMap.containsKey("url")) {
            builder.url((String) configMap.get("url"));
        }
        
        if (configMap.containsKey("username")) {
            builder.username((String) configMap.get("username"));
        }
        
        if (configMap.containsKey("password")) {
            builder.password((String) configMap.get("password"));
        }
        
        if (configMap.containsKey("database")) {
            builder.database((String) configMap.get("database"));
        }
        
        if (configMap.containsKey("enabled")) {
            builder.enabled((Boolean) configMap.get("enabled"));
        }
        
        if (configMap.containsKey("config")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> config = (Map<String, Object>) configMap.get("config");
            builder.config(config);
        }

        if (configMap.containsKey("llm")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> llmMap = (Map<String, Object>) configMap.get("llm");
            builder.llm(LlmConfig.fromMap(llmMap));
        }

        if (configMap.containsKey("embedder")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> embedderMap = (Map<String, Object>) configMap.get("embedder");
            builder.embedder(EmbedderConfig.fromMap(embedderMap));
        }

        if (configMap.containsKey("customPrompt")) {
            builder.customPrompt((String) configMap.get("customPrompt"));
        }

        if (configMap.containsKey("threshold")) {
            builder.threshold((Double) configMap.get("threshold"));
        }

        return builder.build();
    }
    

    /**
     * 获取Neo4j默认配置
     */
    public static GraphStoreConfig neo4jDefault() {
        return GraphStoreConfig.builder()
                .provider(Provider.NEO4J)
                .url("bolt://localhost:7687")
                .username("neo4j")
                .password("password")
                .database("neo4j")
                .enabled(false)
                .threshold(0.7)
                .build();
    }
    
    

}
