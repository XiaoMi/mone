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
        LOCAL("local"),
        NEO4J("neo4j"),
        KUZU("kuzu"),
        MEMGRAPH("memgraph"),
        NEPTUNE("neptune");
        
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
        
        return builder.build();
    }
    
    /**
     * 获取本地图存储默认配置
     */
    public static GraphStoreConfig localDefault() {
        return GraphStoreConfig.builder()
                .provider(Provider.LOCAL)
                .url("./data/graph")
                .enabled(true)
                .build();
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
                .build();
    }
    
    
    /**
     * 创建默认的Memgraph配置
     */
    public static GraphStoreConfig memgraphDefault() {
        return GraphStoreConfig.builder()
            .provider(Provider.MEMGRAPH)
            .url("bolt://localhost:7687")
            .username("memgraph")
            .password("memgraph")
            .enabled(false)
            .build();
    }
    
    /**
     * 创建默认的Neptune配置
     */
    public static GraphStoreConfig neptuneDefault() {
        return GraphStoreConfig.builder()
            .provider(Provider.NEPTUNE)
            .url("neptune-graph://your-graph-id")
            .enabled(false)
            .build();
    }
    
    /**
     * 创建默认的Kuzu配置  
     */
    public static GraphStoreConfig kuzuDefault() {
        return GraphStoreConfig.builder()
            .provider(Provider.KUZU)
            .url(":memory:")
            .enabled(false)
            .build();
    }
}
