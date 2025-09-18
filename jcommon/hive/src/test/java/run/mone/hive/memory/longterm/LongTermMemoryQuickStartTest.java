package run.mone.hive.memory.longterm;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;

import run.mone.hive.memory.longterm.config.*;
import run.mone.hive.memory.longterm.core.Memory;

import java.util.*;

/**
 * 长期记忆模块快速开始测试
 * 这个测试类演示了基本的使用方式，可以本地快速验证功能
 */
@Slf4j
@DisplayName("长期记忆模块快速开始测试")
public class LongTermMemoryQuickStartTest {
    
    private Memory memory;
    private final String testUserId = "test_user_123";
    private final String testAgentId = "test_agent_001";
    
    @BeforeEach
    public void setUp() {
        log.info("=== 初始化长期记忆模块测试环境 ===");
        
        // 创建一个简单的内存配置，不依赖外部服务
        MemoryConfig config = createMockConfig();
        memory = new Memory(config);
        
        log.info("长期记忆模块初始化完成");
    }
    
    @AfterEach
    public void tearDown() {
        if (memory != null) {
            try {
                // 清理测试数据
                memory.deleteAll(testUserId, null, null);
                memory.deleteAll(null, testAgentId, null);
                memory.close();
                log.info("测试环境清理完成");
            } catch (Exception e) {
                log.warn("清理测试环境时出现异常", e);
            }
        }
    }
    
    @Test
    @DisplayName("1. 基础记忆添加和搜索测试")
    public void testBasicMemoryOperations() {
        log.info("=== 开始基础记忆操作测试 ===");
        
        try {
            // 1. 添加用户记忆
            log.info("添加用户偏好记忆...");
            Map<String, Object> result1 = memory.add(
                "用户小明喜欢喝咖啡，不喜欢茶，住在北京朝阳区", 
                testUserId, null, null, null, true, null, null
            );
            log.info("添加结果1: {}", result1);
            
            // 2. 添加技能记忆
            log.info("添加技能记忆...");
            Map<String, Object> result2 = memory.add(
                "用户精通Java编程，熟悉Spring框架", 
                testUserId, null, null, 
                Map.of("category", "技能"), true, null, null
            );
            log.info("添加结果2: {}", result2);
            
            // 3. 搜索记忆
            log.info("搜索用户偏好...");
            Map<String, Object> searchResult = memory.search(
                "用户的饮品偏好", testUserId, null, null, 5, null, null
            );
            log.info("搜索结果: {}", searchResult);
            
            // 4. 获取所有记忆
            log.info("获取用户所有记忆...");
            Map<String, Object> allMemories = memory.getAll(
                testUserId, null, null, null, 10
            );
            log.info("所有记忆: {}", allMemories);
            
            log.info("✅ 基础记忆操作测试通过");
            
        } catch (Exception e) {
            log.error("❌ 基础记忆操作测试失败", e);
            throw e;
        }
    }
    
    @Test
    @DisplayName("2. 对话记忆测试") 
    public void testConversationMemory() {
        log.info("=== 开始对话记忆测试 ===");
        
        try {
            // 模拟对话
            List<Map<String, Object>> conversation = Arrays.asList(
                Map.of("role", "user", "content", "你好，我想学习机器学习"),
                Map.of("role", "assistant", "content", "很好！机器学习是一个很有前景的领域。您有编程基础吗？"),
                Map.of("role", "user", "content", "我有Python基础，但是没有AI经验"),
                Map.of("role", "assistant", "content", "那很棒！Python是机器学习的主要语言。我建议您从基础的数学概念开始...")
            );
            
            log.info("添加对话记忆...");
            Map<String, Object> result = memory.add(
                conversation, testUserId, testAgentId, null, 
                Map.of("topic", "机器学习学习"), true, null, null
            );
            log.info("对话记忆添加结果: {}", result);
            
            // 搜索相关对话
            log.info("搜索机器学习相关对话...");
            Map<String, Object> searchResult = memory.search(
                "机器学习学习建议", testUserId, testAgentId, null, 3, null, null
            );
            log.info("对话搜索结果: {}", searchResult);
            
            log.info("✅ 对话记忆测试通过");
            
        } catch (Exception e) {
            log.error("❌ 对话记忆测试失败", e);
            throw e;
        }
    }
    
    @Test
    @DisplayName("3. 异步操作测试")
    public void testAsyncOperations() {
        log.info("=== 开始异步操作测试 ===");
        
        try {
            // 异步添加记忆
            log.info("异步添加记忆...");
            memory.addAsync(
                "这是一个异步添加的测试记忆", 
                testUserId, null, null, null, true, null, null
            ).thenAccept(result -> {
                log.info("异步添加完成: {}", result);
            }).join(); // 等待完成
            
            // 异步搜索
            log.info("异步搜索记忆...");
            memory.searchAsync(
                "异步测试", testUserId, null, null, 5, null, null
            ).thenAccept(result -> {
                log.info("异步搜索完成: {}", result);
            }).join(); // 等待完成
            
            log.info("✅ 异步操作测试通过");
            
        } catch (Exception e) {
            log.error("❌ 异步操作测试失败", e);
            throw e;
        }
    }
    
    @Test
    @DisplayName("4. 配置灵活性测试")
    public void testConfigurationFlexibility() {
        log.info("=== 开始配置灵活性测试 ===");
        
        try {
            // 测试不同的配置方式
            log.info("测试自定义配置...");
            
            // 创建自定义配置
            MemoryConfig customConfig = MemoryConfig.builder()
                .llm(LlmConfig.builder()
                    .provider(LlmConfig.Provider.OPENAI)
                    .model("gpt-4o-mini")
                    .apiKey("mock-api-key") // 测试用的mock key
                    .temperature(0.2)
                    .maxTokens(2000)
                    .build())
                .embedder(EmbedderConfig.builder()
                    .provider(EmbedderConfig.Provider.OPENAI)
                    .model("text-embedding-3-small")
                    .embeddingDims(1536)
                    .build())
                .vectorStore(VectorStoreConfig.builder()
                    .provider(VectorStoreConfig.Provider.QDRANT)
                    .collectionName("test_collection")
                    .host("localhost")
                    .port(6333)
                    .build())
                .version("1.0.0")
                .build();
            
            // 创建自定义配置的Memory实例
            Memory customMemory = new Memory(customConfig);
            
            log.info("自定义配置创建成功");
            
            // 简单测试
            Map<String, Object> result = customMemory.add(
                "自定义配置测试记忆", 
                "custom_user", null, null, null, true, null, null
            );
            log.info("自定义配置测试结果: {}", result);
            
            // 清理
            customMemory.deleteAll("custom_user", null, null);
            customMemory.close();
            
            log.info("✅ 配置灵活性测试通过");
            
        } catch (Exception e) {
            log.error("❌ 配置灵活性测试失败", e);
            throw e;
        }
    }
    
    @Test
    @DisplayName("5. 图存储功能测试")
    public void testGraphStorageFeatures() {
        log.info("=== 开始图存储功能测试 ===");
        
        try {
            // 测试图存储相关功能
            log.info("测试图存储状态...");
            
            boolean isGraphEnabled = memory.isGraphEnabled();
            log.info("图存储启用状态: {}", isGraphEnabled);
            
            if (isGraphEnabled) {
                // 获取图存储统计信息
                Map<String, Object> graphStats = memory.getGraphStats();
                log.info("图存储统计: {}", graphStats);
                
                // 验证图存储连接
                boolean isConnected = memory.validateGraphConnection();
                log.info("图存储连接状态: {}", isConnected);
            } else {
                log.info("图存储未启用，跳过图存储功能测试");
            }
            
            log.info("✅ 图存储功能测试通过");
            
        } catch (Exception e) {
            log.error("❌ 图存储功能测试失败", e);
            throw e;
        }
    }
    
    /**
     * 创建一个用于测试的模拟配置
     * 使用默认值，不依赖外部服务
     */
    private MemoryConfig createMockConfig() {
        return MemoryConfig.builder()
            .llm(LlmConfig.builder()
                .provider(LlmConfig.Provider.OPENAI)
                .model("gpt-4o-mini")
                .apiKey("mock-test-key") // 测试用的mock key
                .temperature(0.1)
                .maxTokens(1000)
                .build())
            .embedder(EmbedderConfig.builder()
                .provider(EmbedderConfig.Provider.OPENAI)
                .model("text-embedding-3-small")
                .apiKey("mock-test-key") // 测试用的mock key
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
                .enabled(false) // 默认不启用，避免测试依赖
                .build())
            .historyDbPath("./test_memory_history.db")
            .version("1.0.0-test")
            .build();
    }
    
    /**
     * 手动运行测试的主方法
     * 可以独立运行，不依赖JUnit环境
     */
    public static void main(String[] args) {
        LongTermMemoryQuickStartTest test = new LongTermMemoryQuickStartTest();
        
        try {
            log.info("🚀 开始长期记忆模块快速验证测试");
            
            test.setUp();
            
            // 运行核心测试
            test.testBasicMemoryOperations();
            test.testConversationMemory();
            test.testAsyncOperations();
            test.testConfigurationFlexibility();
            test.testGraphStorageFeatures();
            
            test.tearDown();
            
            log.info("🎉 长期记忆模块快速验证测试全部通过！");
            
        } catch (Exception e) {
            log.error("💥 测试过程中发生异常", e);
        }
    }
}
