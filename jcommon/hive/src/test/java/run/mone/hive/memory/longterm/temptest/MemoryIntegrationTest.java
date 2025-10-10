package run.mone.hive.memory.longterm.temptest;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import run.mone.hive.memory.longterm.config.*;
import run.mone.hive.memory.longterm.core.Memory;

import java.util.*;

/**
 * 长期记忆模块集成测试
 * 这些测试需要真实的API key才能运行
 * 可以通过环境变量控制是否运行
 */
@Slf4j
@DisplayName("长期记忆模块集成测试")
public class MemoryIntegrationTest {

    private Memory memory;
    private final String testUserId = "integration_test_user";

    @BeforeEach
    public void setUp() {
        log.info("=== 初始化集成测试环境 ===");

        // 检查是否有OpenAI API key
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("未设置OPENAI_API_KEY环境变量，将使用mock配置");
            apiKey = "mock-api-key-for-test";
        }


        EmbedderConfig embedderConfig = EmbedderConfig.builder()
                .provider(EmbedderConfig.Provider.OLLAMA)
                .model("embeddinggemma")
                .embeddingDims(768)
                .build();


        // 创建真实配置
        MemoryConfig config = MemoryConfig.builder()
                .embedder(embedderConfig)
                .vectorStore(VectorStoreConfig.builder()
                        .provider(VectorStoreConfig.Provider.CHROMA)
                        .collectionName("integration_test_memory_newa")
                        .host("localhost")
                        .port(8000)
                        .embeddingModelDims(768)
                        .build())
                .graphStore(GraphStoreConfig.builder()
                        .provider(GraphStoreConfig.Provider.NEO4J)
                        .url("bolt://localhost:7687")
                        .embedder(embedderConfig)
                        .username("neo4j")
                        .password(System.getenv("NEO4J_PASSWORD"))
                        .enabled(true) // 默认不启用图存储
                        .build())
                .historyDbPath("./integration_test_history.db")
                .version("1.0.0-integration")
                .build();

        memory = new Memory(config);
        log.info("集成测试环境初始化完成");
    }

    @AfterEach
    public void tearDown() {
        if (memory != null) {
            try {
                // 清理测试数据
                memory.deleteAll(testUserId, null, null);
                memory.close();
                log.info("集成测试环境清理完成");
            } catch (Exception e) {
                log.warn("清理集成测试环境时出现异常", e);
            }
        }
    }

    @Test
    @DisplayName("简单记忆操作测试（不依赖外部API）")
    public void testSimpleMemoryOperations() {
        log.info("=== 开始简单记忆操作测试 ===");

        try {
            // 测试基本的配置和初始化
            assertNotNull(memory, "Memory实例不应为null");

            // 测试配置获取
            MemoryConfig config = memory.getConfig();
            assertNotNull(config, "配置不应为null");
            assertNotNull(config.getLlm(), "LLM配置不应为null");
            assertNotNull(config.getEmbedder(), "嵌入模型配置不应为null");
            assertNotNull(config.getVectorStore(), "向量存储配置不应为null");

            log.info("Memory实例配置验证通过");
            log.info("LLM提供商: {}", config.getLlm().getProvider());
            log.info("嵌入模型提供商: {}", config.getEmbedder().getProvider());
            log.info("向量存储提供商: {}", config.getVectorStore().getProvider());

            // 测试图存储状态
            boolean isGraphEnabled = memory.isGraphEnabled();
            log.info("图存储启用状态: {}", isGraphEnabled);

            if (isGraphEnabled) {
                Map<String, Object> graphStats = memory.getGraphStats();
                log.info("图存储统计: {}", graphStats);
            }

            log.info("✅ 简单记忆操作测试通过");

        } catch (Exception e) {
            log.error("❌ 简单记忆操作测试失败", e);
            throw e;
        }
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
    @DisplayName("真实API记忆添加测试（需要OpenAI API Key）")
    public void testRealApiMemoryAdd() {
        log.info("=== 开始真实API记忆添加测试 ===");

        try {
            String testMemory = "使用框架包括 springboot  gson  netty";

            log.info("添加记忆: {}", testMemory);
            Map<String, Object> result = memory.add(
                    testMemory, testUserId, null, null,
                    Map.of("type", "preference", "test", true),
                    true, null, null
            );

            assertNotNull(result, "添加结果不应为null");
            log.info("记忆添加成功: {}", result);

            // 等待一下，让向量化处理完成
            Thread.sleep(1000);

            // 测试搜索
            log.info("搜索相关记忆...");
            Map<String, Object> searchResult = memory.search(
                    "用户的饮品偏好", testUserId, null, null, 3, null, null
            );

            assertNotNull(searchResult, "搜索结果不应为null");
            log.info("搜索结果: {}", searchResult);

            log.info("✅ 真实API记忆添加测试通过");

        } catch (Exception e) {
            log.error("❌ 真实API记忆添加测试失败", e);
        }
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "ENABLE_GRAPH_TEST", matches = "true")
    @DisplayName("图存储集成测试（需要Neo4j运行）")
    public void testGraphStorageIntegration() {
        log.info("=== 开始图存储集成测试 ===");

        try {
            // 创建启用图存储的配置
            MemoryConfig graphConfig = MemoryConfig.builder()
                    .llm(memory.getConfig().getLlm())
                    .embedder(memory.getConfig().getEmbedder())
                    .vectorStore(memory.getConfig().getVectorStore())
                    .graphStore(GraphStoreConfig.builder()
                            .provider(GraphStoreConfig.Provider.NEO4J)
                            .url("bolt://localhost:7687")
                            .username("neo4j")
                            .password("password")
                            .enabled(true) // 启用图存储
                            .build())
                    .build();

            Memory graphMemory = new Memory(graphConfig);

            // 验证图存储连接
            boolean isConnected = graphMemory.validateGraphConnection();
            log.info("图存储连接状态: {}", isConnected);

            if (isConnected) {
                // 添加包含关系的记忆
                String relationMemory = "张三是北京大学的教授，李四是他的学生，他们正在研究机器学习";

                Map<String, Object> result = graphMemory.add(
                        relationMemory, testUserId, null, null,
                        Map.of("type", "relationship"), true, null, null
                );

                log.info("关系记忆添加结果: {}", result);

                // 搜索图记忆
                List<Map<String, Object>> graphResults = graphMemory.searchGraph("张三", 5);
                log.info("图记忆搜索结果: {}", graphResults);

                // 获取节点关系
                List<Map<String, Object>> relationships = graphMemory.getNodeRelationships("张三");
                log.info("张三的关系: {}", relationships);
            }

            graphMemory.close();

            log.info("✅ 图存储集成测试通过");

        } catch (Exception e) {
            log.error("❌ 图存储集成测试失败", e);
            throw e;
        }
    }

    @Test
    @DisplayName("多提供商配置测试")
    public void testMultiProviderConfiguration() {
        log.info("=== 开始多提供商配置测试 ===");

        try {
            // 测试Claude配置
            testProviderConfiguration(
                    LlmConfig.Provider.CLAUDE,
                    "claude-3-5-sonnet-20240620",
                    System.getenv("ANTHROPIC_API_KEY")
            );

            // 测试Gemini配置
            testProviderConfiguration(
                    LlmConfig.Provider.GEMINI,
                    "gemini-1.5-pro-latest",
                    System.getenv("GOOGLE_API_KEY")
            );

            // 测试Ollama配置
            testProviderConfiguration(
                    LlmConfig.Provider.OLLAMA,
                    "llama3.1",
                    null // Ollama不需要API key
            );

            log.info("✅ 多提供商配置测试通过");

        } catch (Exception e) {
            log.error("❌ 多提供商配置测试失败", e);
            throw e;
        }
    }

    private void testProviderConfiguration(LlmConfig.Provider provider, String model, String apiKey) {
        try {
            log.info("测试{}提供商配置...", provider.name());

            LlmConfig.LlmConfigBuilder builder = LlmConfig.builder()
                    .provider(provider)
                    .model(model);

            if (apiKey != null && !apiKey.trim().isEmpty()) {
                builder.apiKey(apiKey);
            } else if (provider == LlmConfig.Provider.OLLAMA) {
                builder.baseUrl("http://localhost:11434");
            } else {
                builder.apiKey("mock-key-for-test");
            }

            LlmConfig config = builder.build();

            assertNotNull(config, "配置不应为null");
            assertEquals(provider, config.getProvider());
            assertEquals(model, config.getModel());

            log.info("{}配置创建成功", provider.name());

        } catch (Exception e) {
            log.warn("{}配置测试失败: {}", provider.name(), e.getMessage());
        }
    }

    /**
     * 工具方法：断言不为null
     */
    private void assertNotNull(Object object, String message) {
        if (object == null) {
            throw new AssertionError(message);
        }
    }

    /**
     * 工具方法：断言相等
     */
    private void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(String.format("%s: expected <%s> but was <%s>",
                    message, expected, actual));
        }
    }

    private void assertEquals(Object expected, Object actual) {
        assertEquals(expected, actual, "Values should be equal");
    }

    /**
     * 手动运行集成测试
     */
    public static void main(String[] args) {
        MemoryIntegrationTest test = new MemoryIntegrationTest();

        try {
            log.info("🚀 开始长期记忆模块集成测试");

            test.setUp();

            // 运行基础测试
            test.testSimpleMemoryOperations();
            test.testMultiProviderConfiguration();

            // 如果有API key，运行真实API测试
            if (System.getenv("OPENAI_API_KEY") != null) {
                test.testRealApiMemoryAdd();
            } else {
                log.info("跳过真实API测试（未设置OPENAI_API_KEY）");
            }

            // 如果启用图存储测试，运行图存储测试
            if ("true".equals(System.getenv("ENABLE_GRAPH_TEST"))) {
                test.testGraphStorageIntegration();
            } else {
                log.info("跳过图存储测试（未设置ENABLE_GRAPH_TEST=true）");
            }

            test.tearDown();

            log.info("🎉 长期记忆模块集成测试完成！");

        } catch (Exception e) {
            log.error("💥 集成测试过程中发生异常", e);
        }
    }
}
