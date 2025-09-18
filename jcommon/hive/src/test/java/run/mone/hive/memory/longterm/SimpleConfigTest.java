package run.mone.hive.memory.longterm;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import run.mone.hive.memory.longterm.config.*;

/**
 * 简单配置测试 - 只测试配置创建，不依赖其他复杂功能
 */
@Slf4j
@DisplayName("简单配置测试")
public class SimpleConfigTest {
    
    @Test
    @DisplayName("测试基础配置创建")
    public void testBasicConfigCreation() {
        log.info("=== 开始基础配置创建测试 ===");
        
        try {
            // 测试LLM配置
            LlmConfig llmConfig = LlmConfig.builder()
                .provider(LlmConfig.Provider.OPENAI)
                .model("gpt-4o-mini")
                .apiKey("test-key")
                .build();
            
            assertNotNull(llmConfig, "LLM配置不应为null");
            assertEquals(LlmConfig.Provider.OPENAI, llmConfig.getProvider(), "提供商应为OPENAI");
            log.info("✅ LLM配置创建成功: {}", llmConfig.getProvider());
            
            // 测试嵌入配置
            EmbedderConfig embedderConfig = EmbedderConfig.builder()
                .provider(EmbedderConfig.Provider.OPENAI)
                .model("text-embedding-3-small")
                .build();
            
            assertNotNull(embedderConfig, "嵌入配置不应为null");
            assertEquals(EmbedderConfig.Provider.OPENAI, embedderConfig.getProvider(), "提供商应为OPENAI");
            log.info("✅ 嵌入配置创建成功: {}", embedderConfig.getProvider());
            
            // 测试向量存储配置
            VectorStoreConfig vectorConfig = VectorStoreConfig.builder()
                .provider(VectorStoreConfig.Provider.QDRANT)
                .collectionName("test_collection")
                .build();
            
            assertNotNull(vectorConfig, "向量存储配置不应为null");
            assertEquals(VectorStoreConfig.Provider.QDRANT, vectorConfig.getProvider(), "提供商应为QDRANT");
            log.info("✅ 向量存储配置创建成功: {}", vectorConfig.getProvider());
            
            // 测试图存储配置
            GraphStoreConfig graphConfig = GraphStoreConfig.builder()
                .provider(GraphStoreConfig.Provider.NEO4J)
                .url("bolt://localhost:7687")
                .enabled(false)
                .build();
            
            assertNotNull(graphConfig, "图存储配置不应为null");
            assertEquals(GraphStoreConfig.Provider.NEO4J, graphConfig.getProvider(), "提供商应为NEO4J");
            log.info("✅ 图存储配置创建成功: {}", graphConfig.getProvider());
            
            // 测试完整内存配置
            MemoryConfig memoryConfig = MemoryConfig.builder()
                .llm(llmConfig)
                .embedder(embedderConfig)
                .vectorStore(vectorConfig)
                .graphStore(graphConfig)
                .version("test-1.0.0")
                .build();
            
            assertNotNull(memoryConfig, "内存配置不应为null");
            assertNotNull(memoryConfig.getLlm(), "LLM配置不应为null");
            assertNotNull(memoryConfig.getEmbedder(), "嵌入配置不应为null");
            assertNotNull(memoryConfig.getVectorStore(), "向量存储配置不应为null");
            assertNotNull(memoryConfig.getGraphStore(), "图存储配置不应为null");
            assertEquals("test-1.0.0", memoryConfig.getVersion(), "版本应匹配");
            
            log.info("✅ 完整内存配置创建成功: version={}", memoryConfig.getVersion());
            
            log.info("🎉 所有基础配置测试通过！");
            
        } catch (Exception e) {
            log.error("❌ 基础配置测试失败", e);
            throw new RuntimeException("配置测试失败", e);
        }
    }
    
    @Test
    @DisplayName("测试默认配置")
    public void testDefaultConfigurations() {
        log.info("=== 开始默认配置测试 ===");
        
        try {
            // 测试默认配置
            MemoryConfig defaultConfig = MemoryConfig.getDefault();
            assertNotNull(defaultConfig, "默认配置不应为null");
            assertNotNull(defaultConfig.getLlm(), "默认LLM配置不应为null");
            assertNotNull(defaultConfig.getEmbedder(), "默认嵌入配置不应为null");
            assertNotNull(defaultConfig.getVectorStore(), "默认向量存储配置不应为null");
            
            log.info("✅ 默认配置验证通过");
            log.info("默认LLM提供商: {}", defaultConfig.getLlm().getProvider());
            log.info("默认嵌入提供商: {}", defaultConfig.getEmbedder().getProvider());
            log.info("默认向量存储提供商: {}", defaultConfig.getVectorStore().getProvider());
            
            // 测试图存储默认配置
            GraphStoreConfig neo4jDefault = GraphStoreConfig.neo4jDefault();
            assertNotNull(neo4jDefault, "Neo4j默认配置不应为null");
            assertEquals(GraphStoreConfig.Provider.NEO4J, neo4jDefault.getProvider(), "应为Neo4j");
            assertFalse(neo4jDefault.isEnabled(), "默认应不启用");
            
            log.info("✅ Neo4j默认配置验证通过");
            
            GraphStoreConfig memgraphDefault = GraphStoreConfig.memgraphDefault();
            assertNotNull(memgraphDefault, "Memgraph默认配置不应为null");
            assertEquals(GraphStoreConfig.Provider.MEMGRAPH, memgraphDefault.getProvider(), "应为Memgraph");
            
            log.info("✅ Memgraph默认配置验证通过");
            
            VectorStoreConfig qdrantDefault = VectorStoreConfig.qdrantDefault();
            assertNotNull(qdrantDefault, "Qdrant默认配置不应为null");
            assertEquals(VectorStoreConfig.Provider.QDRANT, qdrantDefault.getProvider(), "应为Qdrant");
            
            log.info("✅ Qdrant默认配置验证通过");
            
            log.info("🎉 所有默认配置测试通过！");
            
        } catch (Exception e) {
            log.error("❌ 默认配置测试失败", e);
            throw new RuntimeException("默认配置测试失败", e);
        }
    }
    
    @Test
    @DisplayName("测试枚举值")
    public void testEnumValues() {
        log.info("=== 开始枚举值测试 ===");
        
        try {
            // 测试LLM提供商枚举
            LlmConfig.Provider[] llmProviders = LlmConfig.Provider.values();
            assertTrue(llmProviders.length > 0, "LLM提供商枚举不应为空");
            log.info("LLM提供商数量: {}", llmProviders.length);
            
            for (LlmConfig.Provider provider : llmProviders) {
                assertNotNull(provider.getValue(), "提供商值不应为null");
                assertEquals(provider, LlmConfig.Provider.fromString(provider.getValue()), "fromString应返回相同枚举");
            }
            
            // 测试嵌入提供商枚举
            EmbedderConfig.Provider[] embedderProviders = EmbedderConfig.Provider.values();
            assertTrue(embedderProviders.length > 0, "嵌入提供商枚举不应为空");
            log.info("嵌入提供商数量: {}", embedderProviders.length);
            
            // 测试向量存储提供商枚举
            VectorStoreConfig.Provider[] vectorProviders = VectorStoreConfig.Provider.values();
            assertTrue(vectorProviders.length > 0, "向量存储提供商枚举不应为空");
            log.info("向量存储提供商数量: {}", vectorProviders.length);
            
            // 测试图存储提供商枚举
            GraphStoreConfig.Provider[] graphProviders = GraphStoreConfig.Provider.values();
            assertTrue(graphProviders.length > 0, "图存储提供商枚举不应为空");
            log.info("图存储提供商数量: {}", graphProviders.length);
            
            log.info("🎉 所有枚举值测试通过！");
            
        } catch (Exception e) {
            log.error("❌ 枚举值测试失败", e);
            throw new RuntimeException("枚举值测试失败", e);
        }
    }
    
    // 简单断言方法
    private void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError(message);
        }
    }
    
    private void assertEquals(Object expected, Object actual, String message) {
        if (!java.util.Objects.equals(expected, actual)) {
            throw new AssertionError(String.format("%s: expected <%s> but was <%s>", message, expected, actual));
        }
    }
    
    private void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
    
    private void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }
    
    /**
     * 手动运行测试
     */
    public static void main(String[] args) {
        SimpleConfigTest test = new SimpleConfigTest();
        
        try {
            log.info("🚀 开始简单配置测试");
            
            test.testBasicConfigCreation();
            test.testDefaultConfigurations();
            test.testEnumValues();
            
            log.info("🎉 所有简单配置测试通过！长期记忆模块配置系统工作正常！");
            
        } catch (Exception e) {
            log.error("💥 简单配置测试失败", e);
            System.exit(1);
        }
    }
}
