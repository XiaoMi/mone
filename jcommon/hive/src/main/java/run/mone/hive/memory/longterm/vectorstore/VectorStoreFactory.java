package run.mone.hive.memory.longterm.vectorstore;

import run.mone.hive.memory.longterm.config.VectorStoreConfig;
import run.mone.hive.memory.longterm.vectorstore.impl.*;

/**
 * 向量存储工厂类
 * 根据配置创建相应的向量存储实例
 */
public class VectorStoreFactory {
    
    /**
     * 创建向量存储实例
     * 
     * @param config 向量存储配置
     * @return 向量存储实例
     */
    public static VectorStoreBase create(VectorStoreConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Vector store config cannot be null");
        }
        
        switch (config.getProvider()) {
            case CHROMA:
                return new ChromaVectorStore(config);
            case NEO4J:
                return new Neo4jVectorStore(config);
            default:
                throw new IllegalArgumentException("Unsupported vector store provider: " + config.getProvider());
        }
    }
    
    /**
     * 创建默认的向量存储实例（优先本地嵌入式）
     *
     * @return 本地向量存储实例
     */
    public static VectorStoreBase createDefault() {
        return create(VectorStoreConfig.chromaDefault());
    }

    /**
     * 为测试创建本地向量存储实例
     *
     * @return 本地Chroma向量存储实例
     */
    public static VectorStoreBase createLocalForTesting() {
        return create(VectorStoreConfig.builder()
                .provider(VectorStoreConfig.Provider.CHROMA)
                .collectionName("test_collection")
                .host("localhost")
                .path("./data/test/chroma")
                .embeddingModelDims(384)
                .build());
    }

    /**
     * 创建Neo4j向量存储实例
     *
     * @return Neo4j向量存储实例
     */
    public static VectorStoreBase createNeo4j() {
        return create(VectorStoreConfig.neo4jDefault());
    }

    /**
     * 为测试创建Neo4j向量存储实例
     *
     * @return Neo4j向量存储实例
     */
    public static VectorStoreBase createNeo4jForTesting() {
        return create(VectorStoreConfig.neo4jForTesting());
    }
    
    /**
     * 重置向量存储
     * 
     * @param vectorStore 要重置的向量存储
     * @return 新的向量存储实例
     */
    public static VectorStoreBase reset(VectorStoreBase vectorStore) {
        if (vectorStore != null) {
            VectorStoreConfig config = vectorStore.getConfig();
            vectorStore.close();
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
    public static boolean isProviderSupported(VectorStoreConfig.Provider provider) {
        try {
            VectorStoreConfig config = VectorStoreConfig.builder()
                .provider(provider)
                .collectionName("test")
                .build();
            create(config);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
