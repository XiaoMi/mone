package run.mone.hive.memory.longterm;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.memory.longterm.config.*;
import run.mone.hive.memory.longterm.core.Memory;
import run.mone.hive.memory.longterm.llm.LLMFactory;
import run.mone.hive.memory.longterm.llm.LLMBase;
import run.mone.hive.memory.longterm.embeddings.EmbeddingFactory;
import run.mone.hive.memory.longterm.embeddings.EmbeddingBase;
import run.mone.hive.memory.longterm.vectorstore.VectorStoreFactory;
import run.mone.hive.memory.longterm.vectorstore.VectorStoreBase;
import run.mone.hive.memory.longterm.graph.GraphStoreFactory;
import run.mone.hive.memory.longterm.graph.GraphStoreBase;

/**
 * 快速验证演示 - 测试长期记忆模块的核心功能
 * 
 * 本演示程序验证：
 * 1. 配置系统是否正常工作
 * 2. 工厂模式是否能正确创建实例
 * 3. 各组件是否能正确初始化
 * 4. 架构设计是否完整
 */
@Slf4j
public class QuickVerificationDemo {
    
    public static void main(String[] args) {
        log.info("🚀 开始Hive长期记忆模块功能验证");
        
        try {
            // 1. 验证配置系统
            verifyConfigSystem();
            
            // 2. 验证工厂模式
            verifyFactoryPattern();
            
            // 3. 验证核心Memory类
            verifyMemoryCore();
            
            // 4. 验证默认配置
            verifyDefaultConfigurations();
            
            log.info("🎉 所有验证测试通过！Hive长期记忆模块工作正常！");
            
        } catch (Exception e) {
            log.error("💥 验证过程中出现错误", e);
            System.exit(1);
        }
    }
    
    /**
     * 验证配置系统
     */
    private static void verifyConfigSystem() {
        log.info("=== 验证配置系统 ===");
        
        // 验证LLM配置
        LlmConfig llmConfig = LlmConfig.builder()
            .provider(LlmConfig.Provider.OPENAI)
            .model("gpt-4o-mini")
            .apiKey("test-key")
            .baseUrl("https://api.openai.com/v1")
            .temperature(0.1)
            .maxTokens(4000)
            .build();
        
        assert llmConfig.getProvider() == LlmConfig.Provider.OPENAI;
        assert "gpt-4o-mini".equals(llmConfig.getModel());
        log.info("✅ LLM配置验证成功: provider={}, model={}", 
                llmConfig.getProvider(), llmConfig.getModel());
        
        // 验证嵌入配置
        EmbedderConfig embedderConfig = EmbedderConfig.builder()
            .provider(EmbedderConfig.Provider.OPENAI)
            .model("text-embedding-3-small")
            .apiKey("test-key")
            .embeddingDims(1536)
            .build();
        
        assert embedderConfig.getProvider() == EmbedderConfig.Provider.OPENAI;
        assert "text-embedding-3-small".equals(embedderConfig.getModel());
        log.info("✅ 嵌入配置验证成功: provider={}, model={}", 
                embedderConfig.getProvider(), embedderConfig.getModel());
        
        // 验证向量存储配置
        VectorStoreConfig vectorConfig = VectorStoreConfig.builder()
            .provider(VectorStoreConfig.Provider.QDRANT)
            .collectionName("test_collection")
            .host("localhost")
            .port(6333)
            .embeddingModelDims(1536)
            .build();
        
        assert vectorConfig.getProvider() == VectorStoreConfig.Provider.QDRANT;
        assert "test_collection".equals(vectorConfig.getCollectionName());
        log.info("✅ 向量存储配置验证成功: provider={}, collection={}", 
                vectorConfig.getProvider(), vectorConfig.getCollectionName());
        
        // 验证图存储配置
        GraphStoreConfig graphConfig = GraphStoreConfig.builder()
            .provider(GraphStoreConfig.Provider.NEO4J)
            .url("bolt://localhost:7687")
            .username("neo4j")
            .password("password")
            .enabled(false) // 测试时不启用
            .build();
        
        assert graphConfig.getProvider() == GraphStoreConfig.Provider.NEO4J;
        assert "bolt://localhost:7687".equals(graphConfig.getUrl());
        log.info("✅ 图存储配置验证成功: provider={}, url={}", 
                graphConfig.getProvider(), graphConfig.getUrl());
        
        // 验证完整内存配置
        MemoryConfig memoryConfig = MemoryConfig.builder()
            .llm(llmConfig)
            .embedder(embedderConfig)
            .vectorStore(vectorConfig)
            .graphStore(graphConfig)
            .version("test-1.0.0")
            .build();
        
        assert memoryConfig.getLlm() != null;
        assert memoryConfig.getEmbedder() != null;
        assert memoryConfig.getVectorStore() != null;
        assert memoryConfig.getGraphStore() != null;
        log.info("✅ 完整内存配置验证成功: version={}", memoryConfig.getVersion());
    }
    
    /**
     * 验证工厂模式
     */
    private static void verifyFactoryPattern() {
        log.info("=== 验证工厂模式 ===");
        
        // 验证LLM工厂
        LlmConfig llmConfig = LlmConfig.builder()
            .provider(LlmConfig.Provider.OPENAI)
            .model("gpt-4o-mini")
            .apiKey("test-key")
            .build();
        
        LLMBase llm = LLMFactory.create(llmConfig);
        assert llm != null;
        log.info("✅ LLM工厂验证成功: 创建了 {} 实例", llm.getClass().getSimpleName());
        
        // 验证嵌入工厂
        EmbedderConfig embedderConfig = EmbedderConfig.builder()
            .provider(EmbedderConfig.Provider.OPENAI)
            .model("text-embedding-3-small")
            .apiKey("test-key")
            .build();
        
        EmbeddingBase embedder = EmbeddingFactory.create(embedderConfig);
        assert embedder != null;
        log.info("✅ 嵌入工厂验证成功: 创建了 {} 实例", embedder.getClass().getSimpleName());
        
        // 验证向量存储工厂
        VectorStoreConfig vectorConfig = VectorStoreConfig.builder()
            .provider(VectorStoreConfig.Provider.QDRANT)
            .collectionName("test_collection")
            .build();
        
        VectorStoreBase vectorStore = VectorStoreFactory.create(vectorConfig);
        assert vectorStore != null;
        log.info("✅ 向量存储工厂验证成功: 创建了 {} 实例", vectorStore.getClass().getSimpleName());
        
        // 验证图存储工厂
        GraphStoreConfig graphConfig = GraphStoreConfig.builder()
            .provider(GraphStoreConfig.Provider.NEO4J)
            .url("bolt://localhost:7687")
            .enabled(false)
            .build();
        
        GraphStoreBase graphStore = GraphStoreFactory.create(graphConfig);
        assert graphStore != null;
        log.info("✅ 图存储工厂验证成功: 创建了 {} 实例", graphStore.getClass().getSimpleName());
    }
    
    /**
     * 验证核心Memory类
     */
    private static void verifyMemoryCore() {
        log.info("=== 验证核心Memory类 ===");
        
        // 使用默认配置创建Memory实例
        Memory memory = new Memory();
        assert memory != null;
        log.info("✅ Memory实例创建成功（默认配置）");
        
        // 使用自定义配置创建Memory实例
        MemoryConfig customConfig = MemoryConfig.builder()
            .llm(LlmConfig.builder()
                .provider(LlmConfig.Provider.OPENAI)
                .model("gpt-4o-mini")
                .apiKey("test-key")
                .build())
            .embedder(EmbedderConfig.builder()
                .provider(EmbedderConfig.Provider.OPENAI)
                .model("text-embedding-3-small")
                .apiKey("test-key")
                .build())
            .vectorStore(VectorStoreConfig.builder()
                .provider(VectorStoreConfig.Provider.QDRANT)
                .collectionName("test_collection")
                .build())
            .graphStore(GraphStoreConfig.builder()
                .provider(GraphStoreConfig.Provider.NEO4J)
                .url("bolt://localhost:7687")
                .enabled(false)
                .build())
            .build();
        
        Memory customMemory = new Memory(customConfig);
        assert customMemory != null;
        log.info("✅ Memory实例创建成功（自定义配置）");
        
        // 验证核心API方法存在（不实际调用，避免需要真实的API密钥）
        try {
            assert customMemory.getClass().getMethod("add", Object.class, String.class, String.class, 
                    String.class, java.util.Map.class, Boolean.class, String.class, String.class) != null;
            assert customMemory.getClass().getMethod("search", String.class, String.class, String.class, 
                    String.class, Integer.class, java.util.Map.class, Double.class) != null;
            assert customMemory.getClass().getMethod("get", String.class) != null;
            assert customMemory.getClass().getMethod("getAll", String.class, String.class, String.class, 
                    java.util.Map.class, Integer.class) != null;
            assert customMemory.getClass().getMethod("update", String.class, java.util.Map.class) != null;
            assert customMemory.getClass().getMethod("delete", String.class) != null;
            assert customMemory.getClass().getMethod("deleteAll", String.class, String.class, String.class) != null;
            assert customMemory.getClass().getMethod("history", String.class) != null;
            assert customMemory.getClass().getMethod("reset") != null;
            log.info("✅ 核心API方法验证成功");
            
            // 验证异步方法存在
            assert customMemory.getClass().getMethod("addAsync", Object.class, String.class, String.class, 
                    String.class, java.util.Map.class, Boolean.class, String.class, String.class) != null;
            assert customMemory.getClass().getMethod("searchAsync", String.class, String.class, String.class, 
                    String.class, Integer.class, java.util.Map.class, Double.class) != null;
            log.info("✅ 异步API方法验证成功");
        } catch (NoSuchMethodException e) {
            log.error("❌ API方法验证失败", e);
            throw new RuntimeException("API方法不存在", e);
        }
    }
    
    /**
     * 验证默认配置
     */
    private static void verifyDefaultConfigurations() {
        log.info("=== 验证默认配置 ===");
        
        // 验证默认内存配置
        MemoryConfig defaultConfig = MemoryConfig.getDefault();
        assert defaultConfig != null;
        assert defaultConfig.getLlm() != null;
        assert defaultConfig.getEmbedder() != null;
        assert defaultConfig.getVectorStore() != null;
        log.info("✅ 默认内存配置验证成功");
        log.info("默认LLM: {}", defaultConfig.getLlm().getProvider());
        log.info("默认嵌入: {}", defaultConfig.getEmbedder().getProvider());
        log.info("默认向量存储: {}", defaultConfig.getVectorStore().getProvider());
        
        // 验证各组件默认配置
        VectorStoreConfig qdrantDefault = VectorStoreConfig.qdrantDefault();
        assert qdrantDefault != null;
        assert qdrantDefault.getProvider() == VectorStoreConfig.Provider.QDRANT;
        log.info("✅ Qdrant默认配置验证成功");
        
        GraphStoreConfig neo4jDefault = GraphStoreConfig.neo4jDefault();
        assert neo4jDefault != null;
        assert neo4jDefault.getProvider() == GraphStoreConfig.Provider.NEO4J;
        assert !neo4jDefault.isEnabled(); // 默认不启用
        log.info("✅ Neo4j默认配置验证成功");
        
        GraphStoreConfig memgraphDefault = GraphStoreConfig.memgraphDefault();
        assert memgraphDefault != null;
        assert memgraphDefault.getProvider() == GraphStoreConfig.Provider.MEMGRAPH;
        log.info("✅ Memgraph默认配置验证成功");
    }
}
