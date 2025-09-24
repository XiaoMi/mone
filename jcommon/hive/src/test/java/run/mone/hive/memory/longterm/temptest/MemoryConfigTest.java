package run.mone.hive.memory.longterm.temptest;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import run.mone.hive.memory.longterm.config.*;

import java.util.Map;
import java.util.HashMap;

/**
 * 长期记忆配置测试
 * 测试各种配置类的创建和验证
 */
@Slf4j
@DisplayName("长期记忆配置测试")
public class MemoryConfigTest {
    
    @Test
    @DisplayName("测试LLM配置创建和验证")
    public void testLlmConfigCreation() {
        log.info("=== 测试LLM配置 ===");
        
        // 测试OpenAI配置
        LlmConfig openaiConfig = LlmConfig.builder()
            .provider(LlmConfig.Provider.OPENAI)
            .model("gpt-4o-mini")
            .apiKey("test-key")
            .temperature(0.1)
            .maxTokens(4000)
            .topP(1.0)
            .build();
        
        assertNotNull(openaiConfig);
        assertEquals(LlmConfig.Provider.OPENAI, openaiConfig.getProvider());
        assertEquals("gpt-4o-mini", openaiConfig.getModel());
        assertEquals("test-key", openaiConfig.getApiKey());
        assertEquals(0.1, openaiConfig.getTemperature());
        assertEquals(4000, openaiConfig.getMaxTokens());
        
        log.info("OpenAI配置创建成功: {}", openaiConfig);
        
        // 测试Claude配置
        LlmConfig claudeConfig = LlmConfig.builder()
            .provider(LlmConfig.Provider.CLAUDE)
            .model("claude-3-5-sonnet-20240620")
            .apiKey("claude-key")
            .build();
        
        assertNotNull(claudeConfig);
        assertEquals(LlmConfig.Provider.CLAUDE, claudeConfig.getProvider());
        assertEquals("claude-3-5-sonnet-20240620", claudeConfig.getModel());
        
        log.info("Claude配置创建成功: {}", claudeConfig);
        
        // 测试Ollama配置
        LlmConfig ollamaConfig = LlmConfig.builder()
            .provider(LlmConfig.Provider.OLLAMA)
            .model("llama3.1")
            .baseUrl("http://localhost:11434")
            .build();
        
        assertNotNull(ollamaConfig);
        assertEquals(LlmConfig.Provider.OLLAMA, ollamaConfig.getProvider());
        assertEquals("llama3.1", ollamaConfig.getModel());
        assertEquals("http://localhost:11434", ollamaConfig.getBaseUrl());
        
        log.info("Ollama配置创建成功: {}", ollamaConfig);
        
        log.info("✅ LLM配置测试通过");
    }
    
    @Test
    @DisplayName("测试嵌入模型配置创建")
    public void testEmbedderConfigCreation() {
        log.info("=== 测试嵌入模型配置 ===");
        
        // 测试OpenAI嵌入配置
        EmbedderConfig openaiEmbedder = EmbedderConfig.builder()
            .provider(EmbedderConfig.Provider.OPENAI)
            .model("text-embedding-3-small")
            .apiKey("embedding-key")
            .embeddingDims(1536)
            .build();
        
        assertNotNull(openaiEmbedder);
        assertEquals(EmbedderConfig.Provider.OPENAI, openaiEmbedder.getProvider());
        assertEquals("text-embedding-3-small", openaiEmbedder.getModel());
        assertEquals(1536, openaiEmbedder.getEmbeddingDims());
        
        log.info("OpenAI嵌入配置创建成功: {}", openaiEmbedder);
        
        // 测试HuggingFace嵌入配置
        EmbedderConfig hfEmbedder = EmbedderConfig.builder()
            .provider(EmbedderConfig.Provider.HUGGINGFACE)
            .model("sentence-transformers/all-MiniLM-L6-v2")
            .apiKey("hf-key")
            .embeddingDims(384)
            .build();
        
        assertNotNull(hfEmbedder);
        assertEquals(EmbedderConfig.Provider.HUGGINGFACE, hfEmbedder.getProvider());
        assertEquals("sentence-transformers/all-MiniLM-L6-v2", hfEmbedder.getModel());
        assertEquals(384, hfEmbedder.getEmbeddingDims());
        
        log.info("HuggingFace嵌入配置创建成功: {}", hfEmbedder);
        
        log.info("✅ 嵌入模型配置测试通过");
    }
    
    @Test
    @DisplayName("测试向量存储配置创建")
    public void testVectorStoreConfigCreation() {
        log.info("=== 测试向量存储配置 ===");
        
        // 测试Qdrant配置
        VectorStoreConfig qdrantConfig = VectorStoreConfig.builder()
            .provider(VectorStoreConfig.Provider.QDRANT)
            .collectionName("memory_collection")
            .host("localhost")
            .port(6333)
            .embeddingModelDims(1536)
            .build();
        
        assertNotNull(qdrantConfig);
        assertEquals(VectorStoreConfig.Provider.QDRANT, qdrantConfig.getProvider());
        assertEquals("memory_collection", qdrantConfig.getCollectionName());
        assertEquals("localhost", qdrantConfig.getHost());
        assertEquals(6333, qdrantConfig.getPort());
        assertEquals(1536, qdrantConfig.getEmbeddingModelDims());
        
        log.info("Qdrant配置创建成功: {}", qdrantConfig);
        
        // 测试默认配置
        VectorStoreConfig defaultConfig = VectorStoreConfig.qdrantDefault();
        assertNotNull(defaultConfig);
        assertEquals(VectorStoreConfig.Provider.QDRANT, defaultConfig.getProvider());
        
        log.info("默认Qdrant配置创建成功: {}", defaultConfig);
        
        log.info("✅ 向量存储配置测试通过");
    }
    
    @Test
    @DisplayName("测试图存储配置创建")
    public void testGraphStoreConfigCreation() {
        log.info("=== 测试图存储配置 ===");
        
        // 测试Neo4j配置
        GraphStoreConfig neo4jConfig = GraphStoreConfig.builder()
            .provider(GraphStoreConfig.Provider.NEO4J)
            .url("bolt://localhost:7687")
            .username("neo4j")
            .password("password")
            .database("neo4j")
            .enabled(true)
            .build();
        
        assertNotNull(neo4jConfig);
        assertEquals(GraphStoreConfig.Provider.NEO4J, neo4jConfig.getProvider());
        assertEquals("bolt://localhost:7687", neo4jConfig.getUrl());
        assertEquals("neo4j", neo4jConfig.getUsername());
        assertEquals("password", neo4jConfig.getPassword());
        assertTrue(neo4jConfig.isEnabled());
        
        log.info("Neo4j配置创建成功: {}", neo4jConfig);
        
        // 测试默认配置
        GraphStoreConfig defaultNeo4j = GraphStoreConfig.neo4jDefault();
        assertNotNull(defaultNeo4j);
        assertEquals(GraphStoreConfig.Provider.NEO4J, defaultNeo4j.getProvider());
        assertFalse(defaultNeo4j.isEnabled()); // 默认不启用
        
        log.info("默认Neo4j配置创建成功: {}", defaultNeo4j);
        
        // 测试Memgraph配置
        GraphStoreConfig memgraphConfig = GraphStoreConfig.memgraphDefault();
        assertNotNull(memgraphConfig);
        assertEquals(GraphStoreConfig.Provider.MEMGRAPH, memgraphConfig.getProvider());
        
        log.info("默认Memgraph配置创建成功: {}", memgraphConfig);
        
        log.info("✅ 图存储配置测试通过");
    }
    
    @Test
    @DisplayName("测试完整内存配置创建")
    public void testCompleteMemoryConfigCreation() {
        log.info("=== 测试完整内存配置 ===");
        
        // 创建完整配置
        MemoryConfig config = MemoryConfig.builder()
            .llm(LlmConfig.builder()
                .provider(LlmConfig.Provider.OPENAI)
                .model("gpt-4o-mini")
                .apiKey("test-key")
                .temperature(0.1)
                .maxTokens(4000)
                .build())
            .embedder(EmbedderConfig.builder()
                .provider(EmbedderConfig.Provider.OPENAI)
                .model("text-embedding-3-small")
                .apiKey("test-key")
                .embeddingDims(1536)
                .build())
            .vectorStore(VectorStoreConfig.builder()
                .provider(VectorStoreConfig.Provider.QDRANT)
                .collectionName("test_memory")
                .host("localhost")
                .port(6333)
                .embeddingModelDims(1536)
                .build())
            .graphStore(GraphStoreConfig.builder()
                .provider(GraphStoreConfig.Provider.NEO4J)
                .url("bolt://localhost:7687")
                .username("neo4j")
                .password("password")
                .enabled(false)
                .build())
            .historyDbPath("./test_history.db")
            .version("1.0.0")
            .build();
        
        // 验证配置
        assertNotNull(config);
        assertNotNull(config.getLlm());
        assertNotNull(config.getEmbedder());
        assertNotNull(config.getVectorStore());
        assertNotNull(config.getGraphStore());
        assertEquals("./test_history.db", config.getHistoryDbPath());
        assertEquals("1.0.0", config.getVersion());
        
        log.info("完整配置创建成功: {}", config);
        
        // 测试默认配置
        MemoryConfig defaultConfig = MemoryConfig.getDefault();
        assertNotNull(defaultConfig);
        assertNotNull(defaultConfig.getLlm());
        assertNotNull(defaultConfig.getEmbedder());
        assertNotNull(defaultConfig.getVectorStore());
        
        log.info("默认配置创建成功: {}", defaultConfig);
        
        log.info("✅ 完整内存配置测试通过");
    }
    
    @Test
    @DisplayName("测试配置从Map创建")
    public void testConfigFromMap() {
        log.info("=== 测试配置从Map创建 ===");
        
        // 创建配置Map
        Map<String, Object> configMap = new HashMap<>();
        
        // LLM配置
        Map<String, Object> llmMap = new HashMap<>();
        llmMap.put("provider", "openai");
        llmMap.put("model", "gpt-4o-mini");
        llmMap.put("apiKey", "test-key");
        llmMap.put("temperature", 0.1);
        configMap.put("llm", llmMap);
        
        // 嵌入模型配置
        Map<String, Object> embedderMap = new HashMap<>();
        embedderMap.put("provider", "openai");
        embedderMap.put("model", "text-embedding-3-small");
        embedderMap.put("embeddingDims", 1536);
        configMap.put("embedder", embedderMap);
        
        // 向量存储配置
        Map<String, Object> vectorStoreMap = new HashMap<>();
        vectorStoreMap.put("provider", "qdrant");
        vectorStoreMap.put("host", "localhost");
        vectorStoreMap.put("port", 6333);
        configMap.put("vectorStore", vectorStoreMap);
        
        configMap.put("version", "1.0.0-map");
        
        // 从Map创建配置
        MemoryConfig config = MemoryConfig.fromMap(configMap);
        
        assertNotNull(config);
        assertNotNull(config.getLlm());
        assertEquals(LlmConfig.Provider.OPENAI, config.getLlm().getProvider());
        assertEquals("gpt-4o-mini", config.getLlm().getModel());
        
        assertNotNull(config.getEmbedder());
        assertEquals(EmbedderConfig.Provider.OPENAI, config.getEmbedder().getProvider());
        
        assertNotNull(config.getVectorStore());
        assertEquals(VectorStoreConfig.Provider.QDRANT, config.getVectorStore().getProvider());
        
        assertEquals("1.0.0-map", config.getVersion());
        
        log.info("从Map创建配置成功: {}", config);
        
        log.info("✅ 配置从Map创建测试通过");
    }
    
    /**
     * 手动运行配置测试
     */
    public static void main(String[] args) {
        MemoryConfigTest test = new MemoryConfigTest();
        
        try {
            log.info("🚀 开始配置测试");
            
            test.testLlmConfigCreation();
            test.testEmbedderConfigCreation();
            test.testVectorStoreConfigCreation();
            test.testGraphStoreConfigCreation();
            test.testCompleteMemoryConfigCreation();
            test.testConfigFromMap();
            
            log.info("🎉 所有配置测试通过！");
            
        } catch (Exception e) {
            log.error("💥 配置测试失败", e);
        }
    }
}
