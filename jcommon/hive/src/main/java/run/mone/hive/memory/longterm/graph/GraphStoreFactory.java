package run.mone.hive.memory.longterm.graph;

import run.mone.hive.memory.longterm.config.GraphStoreConfig;
import run.mone.hive.memory.longterm.graph.impl.*;

/**
 * 图数据库工厂类
 * 根据配置创建相应的图数据库实例
 * 基于mem0的图存储设计
 */
public class GraphStoreFactory {
    
    /**
     * 创建图数据库实例
     * 
     * @param config 图数据库配置
     * @return 图数据库实例
     */
    public static GraphStoreBase create(GraphStoreConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Graph store config cannot be null");
        }
        
        if (!config.isEnabled()) {
            return null; // 如果未启用图存储，返回null
        }
        
        switch (config.getProvider()) {
            case LOCAL:
                return new LocalGraphStore(config);
            case NEO4J:
                return new Neo4jGraphStore(config);
            case MEMGRAPH:
                return new MemgraphGraphStore(config);
            case NEPTUNE:
                return new NeptuneGraphStore(config);
            case KUZU:
                return new KuzuGraphStore(config);
            default:
                throw new IllegalArgumentException("Unsupported graph store provider: " + config.getProvider());
        }
    }
    
    /**
     * 创建默认的图数据库实例（优先本地嵌入式）
     *
     * @return 本地图数据库实例
     */
    public static GraphStoreBase createDefault() {
        GraphStoreConfig config = GraphStoreConfig.kuzuDefault();
        return create(config);
    }

    /**
     * 为测试创建本地图数据库实例
     *
     * @return 本地Kuzu图数据库实例
     */
    public static GraphStoreBase createLocalForTesting() {
        return create(GraphStoreConfig.builder()
                .provider(GraphStoreConfig.Provider.KUZU)
                .enabled(true)
                .url("./data/test/kuzu")
                .build());
    }
    
    /**
     * 重置图数据库
     * 
     * @param graphStore 要重置的图数据库
     * @return 新的图数据库实例
     */
    public static GraphStoreBase reset(GraphStoreBase graphStore) {
        if (graphStore != null) {
            GraphStoreConfig config = graphStore.getConfig();
            graphStore.close();
            return create(config);
        }
        return createDefault();
    }
    
    /**
     * 检查提供商是否被支持
     * 
     * @param provider 提供商
     * @return 是否支持
     */
    public static boolean isProviderSupported(GraphStoreConfig.Provider provider) {
        try {
            GraphStoreConfig config = GraphStoreConfig.builder()
                .provider(provider)
                .enabled(true)
                .build();
            create(config);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * 获取所有支持的提供商列表
     * 
     * @return 支持的提供商列表
     */
    public static java.util.List<GraphStoreConfig.Provider> getSupportedProviders() {
        return java.util.Arrays.asList(
            GraphStoreConfig.Provider.NEO4J,
            GraphStoreConfig.Provider.MEMGRAPH,
            GraphStoreConfig.Provider.NEPTUNE,
            GraphStoreConfig.Provider.KUZU
        );
    }
    
    /**
     * 验证配置有效性
     * 
     * @param config 图数据库配置
     * @return 是否有效
     */
    public static boolean validateConfig(GraphStoreConfig config) {
        if (config == null) {
            return false;
        }
        
        try {
            GraphStoreBase graphStore = create(config);
            if (graphStore != null) {
                boolean isValid = graphStore.validateConnection();
                graphStore.close();
                return isValid;
            }
            return true; // 如果图存储未启用，认为配置有效
        } catch (Exception e) {
            return false;
        }
    }
}