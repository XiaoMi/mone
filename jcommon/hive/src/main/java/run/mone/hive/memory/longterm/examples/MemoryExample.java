package run.mone.hive.memory.longterm.examples;

import run.mone.hive.memory.longterm.core.Memory;
import run.mone.hive.memory.longterm.config.MemoryConfig;
import run.mone.hive.memory.longterm.config.LlmConfig;
import run.mone.hive.memory.longterm.config.EmbedderConfig;
import run.mone.hive.memory.longterm.config.VectorStoreConfig;

import java.util.List;
import java.util.Map;
import java.util.Arrays;

/**
 * Memory使用示例
 * 展示如何使用长期记忆功能
 */
public class MemoryExample {
    
    public static void main(String[] args) {
        // 创建基础示例
        basicUsageExample();
        
        // 创建自定义配置示例
        customConfigExample();
    }
    
    /**
     * 基础使用示例
     */
    public static void basicUsageExample() {
        System.out.println("=== 基础使用示例 ===");
        
        try {
            // 使用默认配置创建Memory实例
            Memory memory = new Memory();
            
            String userId = "user_123";
            
            // 添加一些对话记忆
            List<Map<String, Object>> messages = Arrays.asList(
                Map.of("role", "user", "content", "我喜欢喝咖啡"),
                Map.of("role", "assistant", "content", "好的，我记住了您喜欢喝咖啡")
            );
            
            Map<String, Object> result = memory.add(messages, userId, null, null, null, true, null, null);
            System.out.println("添加记忆结果: " + result);
            
            // 搜索相关记忆
            Map<String, Object> searchResult = memory.search("用户的饮品偏好", userId, null, null, 5, null, null);
            System.out.println("搜索结果: " + searchResult);
            
            // 获取所有记忆
            Map<String, Object> allMemories = memory.getAll(userId, null, null, null, 10);
            System.out.println("所有记忆: " + allMemories);
            
            // 关闭资源
            memory.close();
            
        } catch (Exception e) {
            System.err.println("基础示例执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 自定义配置示例
     */
    public static void customConfigExample() {
        System.out.println("\n=== 自定义配置示例 ===");
        
        try {
            // 创建自定义配置
            LlmConfig llmConfig = LlmConfig.builder()
                .provider(LlmConfig.Provider.OPENAI)
                .model("gpt-4o-mini")
                .temperature(0.1)
                .maxTokens(2000)
                .build();
            
            EmbedderConfig embedderConfig = EmbedderConfig.builder()
                .provider(EmbedderConfig.Provider.OPENAI)
                .model("text-embedding-3-small")
                .embeddingDims(1536)
                .build();
            
            VectorStoreConfig vectorStoreConfig = VectorStoreConfig.builder()
                .provider(VectorStoreConfig.Provider.QDRANT)
                .collectionName("custom_memory")
                .host("localhost")
                .port(6333)
                .build();
            
            MemoryConfig memoryConfig = MemoryConfig.builder()
                .llm(llmConfig)
                .embedder(embedderConfig)
                .vectorStore(vectorStoreConfig)
                .version("v1.1")
                .build();
            
            // 使用自定义配置创建Memory实例
            Memory memory = new Memory(memoryConfig);
            
            String agentId = "agent_456";
            
            // 添加代理记忆
            String message = "用户提到他们住在北京，工作是软件工程师";
            
            Map<String, Object> result = memory.add(message, null, agentId, null, 
                Map.of("category", "user_info"), true, null, null);
            System.out.println("添加代理记忆结果: " + result);
            
            // 添加过程记忆
            List<Map<String, Object>> conversationMessages = Arrays.asList(
                Map.of("role", "user", "content", "如何配置Java项目的依赖？"),
                Map.of("role", "assistant", "content", "可以使用Maven或Gradle来管理依赖"),
                Map.of("role", "user", "content", "请详细说明Maven的配置步骤"),
                Map.of("role", "assistant", "content", "首先创建pom.xml文件，然后添加依赖...")
            );
            
            Map<String, Object> proceduralResult = memory.add(conversationMessages, null, agentId, null,
                Map.of("topic", "java_development"), true, "procedural_memory", null);
            System.out.println("过程记忆结果: " + proceduralResult);
            
            // 关闭资源
            memory.close();
            
        } catch (Exception e) {
            System.err.println("自定义配置示例执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 异步操作示例
     */
    public static void asyncExample() {
        System.out.println("\n=== 异步操作示例 ===");
        
        try {
            Memory memory = new Memory();
            String userId = "async_user";
            
            // 异步添加记忆
            memory.addAsync("这是一个异步添加的记忆", userId, null, null, null, true, null, null)
                .thenAccept(result -> {
                    System.out.println("异步添加完成: " + result);
                })
                .exceptionally(throwable -> {
                    System.err.println("异步添加失败: " + throwable.getMessage());
                    return null;
                });
            
            // 异步搜索
            memory.searchAsync("异步", userId, null, null, 5, null, null)
                .thenAccept(result -> {
                    System.out.println("异步搜索完成: " + result);
                })
                .exceptionally(throwable -> {
                    System.err.println("异步搜索失败: " + throwable.getMessage());
                    return null;
                });
            
            // 等待异步操作完成
            Thread.sleep(2000);
            
            memory.close();
            
        } catch (Exception e) {
            System.err.println("异步示例执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 记忆管理示例
     */
    public static void memoryManagementExample() {
        System.out.println("\n=== 记忆管理示例 ===");
        
        try {
            Memory memory = new Memory();
            String userId = "mgmt_user";
            
            // 添加记忆
            Map<String, Object> addResult = memory.add("用户喜欢蓝色", userId, null, null, null, true, null, null);
            System.out.println("添加结果: " + addResult);
            
            // 假设获取到记忆ID (实际使用中从addResult中获取)
            String memoryId = "test_memory_id";
            
            // 更新记忆
            Map<String, Object> updateResult = memory.update(memoryId, "用户喜欢蓝色和绿色");
            System.out.println("更新结果: " + updateResult);
            
            // 获取记忆历史
            List<Map<String, Object>> history = memory.history(memoryId);
            System.out.println("记忆历史: " + history);
            
            // 删除记忆
            Map<String, Object> deleteResult = memory.delete(memoryId);
            System.out.println("删除结果: " + deleteResult);
            
            memory.close();
            
        } catch (Exception e) {
            System.err.println("记忆管理示例执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
