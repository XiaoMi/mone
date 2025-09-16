package run.mone.hive.memory.longterm.examples;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.memory.longterm.config.*;
import run.mone.hive.memory.longterm.core.Memory;
import run.mone.hive.memory.longterm.model.MemoryItem;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 长期记忆模块综合使用示例
 * 展示所有主要功能的使用方法
 */
@Slf4j
public class ComprehensiveMemoryExample {
    
    public static void main(String[] args) {
        ComprehensiveMemoryExample example = new ComprehensiveMemoryExample();
        
        try {
            // 基础使用示例
            example.basicUsageExample();
            
            // 自定义配置示例
            example.customConfigExample();
            
            // 异步操作示例
            example.asyncOperationExample();
            
            // 图存储示例
            example.graphStorageExample();
            
            // 多LLM提供商示例
            example.multiLlmExample();
            
            // 记忆管理示例
            example.memoryManagementExample();
            
        } catch (Exception e) {
            log.error("Example execution failed", e);
        }
    }
    
    /**
     * 基础使用示例
     */
    public void basicUsageExample() {
        log.info("=== 基础使用示例 ===");
        
        try {
            // 使用默认配置创建Memory实例
            Memory memory = new Memory();
            
            String userId = "user_123";
            String agentId = "assistant_001";
            
            // 添加用户记忆
            log.info("添加用户记忆...");
            Map<String, Object> result1 = memory.add(
                "用户小明喜欢喝咖啡，不喜欢茶。他住在北京朝阳区。", 
                userId, null, null, null, true, null, null
            );
            log.info("添加结果: {}", result1);
            
            // 添加对话记忆
            log.info("添加对话记忆...");
            List<Map<String, Object>> conversation = Arrays.asList(
                Map.of("role", "user", "content", "我最近在学习Java编程"),
                Map.of("role", "assistant", "content", "很好！Java是一门很实用的编程语言，您想从哪个方面开始学习？")
            );
            
            Map<String, Object> result2 = memory.add(
                conversation, userId, agentId, null, 
                Map.of("topic", "编程学习"), true, null, null
            );
            log.info("对话记忆添加结果: {}", result2);
            
            // 搜索记忆
            log.info("搜索用户偏好相关的记忆...");
            Map<String, Object> searchResult = memory.search(
                "用户的饮品偏好", userId, null, null, 5, null, null
            );
            log.info("搜索结果: {}", searchResult);
            
            // 获取所有记忆
            log.info("获取用户所有记忆...");
            Map<String, Object> allMemories = memory.getAll(
                userId, null, null, null, 10
            );
            log.info("所有记忆: {}", allMemories);
            
            memory.close();
            
        } catch (Exception e) {
            log.error("基础使用示例失败", e);
        }
    }
    
    /**
     * 自定义配置示例
     */
    public void customConfigExample() {
        log.info("=== 自定义配置示例 ===");
        
        try {
            // 创建自定义配置
            MemoryConfig config = MemoryConfig.builder()
                .llm(LlmConfig.builder()
                    .provider(LlmConfig.Provider.OPENAI)
                    .model("gpt-4o-mini")
                    .apiKey(System.getenv("OPENAI_API_KEY"))
                    .temperature(0.1)
                    .maxTokens(4000)
                    .build())
                .embedder(EmbedderConfig.builder()
                    .provider(EmbedderConfig.Provider.OPENAI)
                    .model("text-embedding-3-small")
                    .apiKey(System.getenv("OPENAI_API_KEY"))
                    .embeddingDims(1536)
                    .build())
                .vectorStore(VectorStoreConfig.builder()
                    .provider(VectorStoreConfig.Provider.QDRANT)
                    .collectionName("hive_memory")
                    .host("localhost")
                    .port(6333)
                    .embeddingModelDims(1536)
                    .build())
                .graphStore(GraphStoreConfig.builder()
                    .provider(GraphStoreConfig.Provider.NEO4J)
                    .url("bolt://localhost:7687")
                    .username("neo4j")
                    .password("password")
                    .enabled(true)
                    .build())
                .historyDbPath("./memory_history.db")
                .version("1.0.0")
                .build();
            
            Memory memory = new Memory(config);
            
            String userId = "advanced_user_456";
            
            // 添加复杂记忆
            log.info("添加复杂记忆...");
            Map<String, Object> complexMemory = memory.add(
                "张三是北京大学的计算机科学教授，他专门研究人工智能。李四是他的学生，正在写关于深度学习的论文。", 
                userId, null, null, 
                Map.of("domain", "学术", "type", "关系信息"), 
                true, null, null
            );
            log.info("复杂记忆添加结果: {}", complexMemory);
            
            // 如果启用了图存储，查看图存储统计
            if (memory.isGraphEnabled()) {
                log.info("图存储统计: {}", memory.getGraphStats());
                
                // 搜索图记忆
                List<Map<String, Object>> graphResults = memory.searchGraph("张三", 5);
                log.info("图记忆搜索结果: {}", graphResults);
            }
            
            memory.close();
            
        } catch (Exception e) {
            log.error("自定义配置示例失败", e);
        }
    }
    
    /**
     * 异步操作示例
     */
    public void asyncOperationExample() {
        log.info("=== 异步操作示例 ===");
        
        try {
            Memory memory = new Memory();
            String userId = "async_user_789";
            
            // 并行添加多个记忆
            List<CompletableFuture<Map<String, Object>>> futures = new ArrayList<>();
            
            String[] memories = {
                "用户喜欢看科幻电影",
                "用户经常在周末去健身房",
                "用户是一名软件工程师",
                "用户养了一只叫小白的猫"
            };
            
            log.info("开始并行添加记忆...");
            for (String memoryText : memories) {
                CompletableFuture<Map<String, Object>> future = memory.addAsync(
                    memoryText, userId, null, null, null, true, null, null
                );
                futures.add(future);
            }
            
            // 等待所有操作完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    log.info("所有异步添加操作完成");
                    
                    // 异步搜索
                    memory.searchAsync("用户的兴趣爱好", userId, null, null, 10, null, null)
                        .thenAccept(result -> {
                            log.info("异步搜索结果: {}", result);
                        });
                })
                .join(); // 等待完成
            
            memory.close();
            
        } catch (Exception e) {
            log.error("异步操作示例失败", e);
        }
    }
    
    /**
     * 图存储示例
     */
    public void graphStorageExample() {
        log.info("=== 图存储示例 ===");
        
        try {
            // 创建带图存储的配置
            MemoryConfig config = MemoryConfig.builder()
                .llm(LlmConfig.builder()
                    .provider(LlmConfig.Provider.OPENAI)
                    .model("gpt-4o-mini")
                    .apiKey(System.getenv("OPENAI_API_KEY"))
                    .build())
                .embedder(EmbedderConfig.builder()
                    .provider(EmbedderConfig.Provider.OPENAI)
                    .model("text-embedding-3-small")
                    .build())
                .vectorStore(VectorStoreConfig.qdrantDefault())
                .graphStore(GraphStoreConfig.builder()
                    .provider(GraphStoreConfig.Provider.NEO4J)
                    .url("bolt://localhost:7687")
                    .username("neo4j")
                    .password("password")
                    .enabled(true)
                    .build())
                .build();
            
            Memory memory = new Memory(config);
            String userId = "graph_user_001";
            
            if (memory.isGraphEnabled()) {
                log.info("图存储已启用");
                
                // 添加包含实体关系的记忆
                String complexText = "小明是清华大学的学生，他的导师是王教授。" +
                                   "小明正在研究机器学习，他的研究重点是自然语言处理。" +
                                   "王教授是人工智能实验室的主任。";
                
                Map<String, Object> result = memory.add(
                    complexText, userId, null, null, 
                    Map.of("type", "学术关系"), true, null, null
                );
                log.info("图存储记忆添加结果: {}", result);
                
                // 搜索图记忆
                List<Map<String, Object>> graphResults = memory.searchGraph("小明", 10);
                log.info("图记忆搜索结果: {}", graphResults);
                
                // 获取节点关系
                List<Map<String, Object>> relationships = memory.getNodeRelationships("小明");
                log.info("小明的关系: {}", relationships);
                
                // 验证图存储连接
                boolean isConnected = memory.validateGraphConnection();
                log.info("图存储连接状态: {}", isConnected);
                
            } else {
                log.warn("图存储未启用");
            }
            
            memory.close();
            
        } catch (Exception e) {
            log.error("图存储示例失败", e);
        }
    }
    
    /**
     * 多LLM提供商示例
     */
    public void multiLlmExample() {
        log.info("=== 多LLM提供商示例 ===");
        
        // OpenAI配置
        try {
            log.info("测试OpenAI配置...");
            testLlmProvider(LlmConfig.Provider.OPENAI, "gpt-4o-mini", 
                System.getenv("OPENAI_API_KEY"));
        } catch (Exception e) {
            log.warn("OpenAI测试失败: {}", e.getMessage());
        }
        
        // Claude配置
        try {
            log.info("测试Claude配置...");
            testLlmProvider(LlmConfig.Provider.CLAUDE, "claude-3-5-sonnet-20240620", 
                System.getenv("ANTHROPIC_API_KEY"));
        } catch (Exception e) {
            log.warn("Claude测试失败: {}", e.getMessage());
        }
        
        // Gemini配置
        try {
            log.info("测试Gemini配置...");
            testLlmProvider(LlmConfig.Provider.GEMINI, "gemini-1.5-pro-latest", 
                System.getenv("GOOGLE_API_KEY"));
        } catch (Exception e) {
            log.warn("Gemini测试失败: {}", e.getMessage());
        }
        
        // Ollama配置
        try {
            log.info("测试Ollama配置...");
            testLlmProvider(LlmConfig.Provider.OLLAMA, "llama3.1", null);
        } catch (Exception e) {
            log.warn("Ollama测试失败: {}", e.getMessage());
        }
    }
    
    private void testLlmProvider(LlmConfig.Provider provider, String model, String apiKey) {
        try {
            MemoryConfig config = MemoryConfig.builder()
                .llm(LlmConfig.builder()
                    .provider(provider)
                    .model(model)
                    .apiKey(apiKey)
                    .baseUrl(provider == LlmConfig.Provider.OLLAMA ? "http://localhost:11434" : null)
                    .build())
                .embedder(EmbedderConfig.builder()
                    .provider(EmbedderConfig.Provider.OPENAI)
                    .model("text-embedding-3-small")
                    .build())
                .vectorStore(VectorStoreConfig.qdrantDefault())
                .build();
            
            Memory memory = new Memory(config);
            String userId = "test_user_" + provider.name().toLowerCase();
            
            Map<String, Object> result = memory.add(
                "这是一个测试记忆，用于验证" + provider.name() + "提供商", 
                userId, null, null, null, true, null, null
            );
            
            log.info("{} 提供商测试成功: {}", provider.name(), result.get("results"));
            memory.close();
            
        } catch (Exception e) {
            log.error("{} 提供商测试失败", provider.name(), e);
            throw e;
        }
    }
    
    /**
     * 记忆管理示例
     */
    public void memoryManagementExample() {
        log.info("=== 记忆管理示例 ===");
        
        try {
            Memory memory = new Memory();
            String userId = "management_user_001";
            
            // 添加一些测试记忆
            log.info("添加测试记忆...");
            Map<String, Object> result1 = memory.add(
                "用户喜欢音乐", userId, null, null, null, true, null, null
            );
            
            Map<String, Object> result2 = memory.add(
                "用户是程序员", userId, null, null, null, true, null, null
            );
            
            // 记忆管理演示完成
            log.info("记忆添加结果1: {}", result1);
            log.info("记忆添加结果2: {}", result2);
            
            // 搜索记忆
            log.info("搜索记忆...");
            Map<String, Object> searchResult = memory.search("音乐", userId, null, null, 5, null, null);
            log.info("搜索结果: {}", searchResult);
            
            // 获取所有记忆
            log.info("获取所有记忆...");
            Map<String, Object> allMemories = memory.getAll(userId, null, null, null, 10);
            log.info("所有记忆: {}", allMemories);
            
            // 最终清理
            log.info("删除用户所有记忆...");
            memory.deleteAll(userId, null, null);
            
            memory.close();
            
        } catch (Exception e) {
            log.error("记忆管理示例失败", e);
        }
    }
}
