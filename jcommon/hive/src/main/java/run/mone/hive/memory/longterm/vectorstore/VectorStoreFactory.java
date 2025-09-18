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
            case LOCAL:
                return new LocalVectorStore(config);
            case QDRANT:
                return new QdrantVectorStore(config);
            case CHROMA:
                throw new UnsupportedOperationException("Chroma not implemented yet");
            case WEAVIATE:
                throw new UnsupportedOperationException("Weaviate not implemented yet");
            case PINECONE:
                throw new UnsupportedOperationException("Pinecone not implemented yet");
            case FAISS:
                throw new UnsupportedOperationException("FAISS not implemented yet");
            case ELASTICSEARCH:
                throw new UnsupportedOperationException("Elasticsearch not implemented yet");
            case REDIS:
                throw new UnsupportedOperationException("Redis not implemented yet");
            case PGVECTOR:
                throw new UnsupportedOperationException("PgVector not implemented yet");
            case MILVUS:
                throw new UnsupportedOperationException("Milvus not implemented yet");
            default:
                throw new IllegalArgumentException("Unsupported vector store provider: " + config.getProvider());
        }
    }
    
    /**
     * 创建默认的Qdrant向量存储实例
     * 
     * @return Qdrant向量存储实例
     */
    public static VectorStoreBase createDefault() {
        return create(VectorStoreConfig.qdrantDefault());
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
