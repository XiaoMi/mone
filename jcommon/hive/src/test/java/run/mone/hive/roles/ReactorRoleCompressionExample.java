package run.mone.hive.roles;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.schema.Message;
import run.mone.hive.context.ConversationContextManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * ReactorRole上下文压缩功能使用示例
 */
@Slf4j
public class ReactorRoleCompressionExample {
    
    public static void main(String[] args) {
        try {
            ReactorRoleCompressionExample example = new ReactorRoleCompressionExample();
            example.runExample();
        } catch (Exception e) {
            log.error("运行示例失败", e);
        }
    }
    
    public void runExample() {
        log.info("=== ReactorRole上下文压缩功能示例 ===");
        
        // 1. 创建LLM配置
        LLMConfig llmConfig = createLLMConfig();
        LLM llm = new LLM(llmConfig);
        
        // 2. 创建ReactorRole
        CountDownLatch latch = new CountDownLatch(1);
        ReactorRole role = new ReactorRole("TestAgent", latch, llm);
        
        // 3. 配置压缩参数
        configureCompressionSettings(role);
        
        // 4. 模拟长对话
        simulateLongConversation(role);
        
        // 5. 测试手动压缩
        testManualCompression(role);
        
        // 6. 显示上下文统计
        showContextStats(role);
        
        log.info("=== 示例完成 ===");
    }
    
    /**
     * 创建LLM配置
     */
    private LLMConfig createLLMConfig() {
        LLMConfig config = new LLMConfig();
        // config.setLlmProvider(LLMProvider.OPENAI);
        // config.setApiKey("your-api-key-here"); // 需要实际的API密钥
        config.setModel("gpt-3.5-turbo");
        return config;
    }
    
    /**
     * 配置压缩设置
     */
    private void configureCompressionSettings(ReactorRole role) {
        Map<String, String> config = new HashMap<>();
        config.put("enableAiCompression", "true");
        config.put("enableRuleBasedOptimization", "true");
        config.put("maxMessagesBeforeCompression", "10"); // 10条消息后开始压缩
        config.put("workspacePath", System.getProperty("user.dir"));
        
        role.setRoleConfig(config);
        role.initConfig();
        
        log.info("压缩配置已设置");
    }
    
    /**
     * 模拟长对话
     */
    private void simulateLongConversation(ReactorRole role) {
        log.info("开始模拟长对话...");
        
        String[] userQuestions = {
            "你好，我需要帮助开发一个Java项目",
            "这个项目需要支持分布式架构",
            "如何设计数据库表结构？",
            "需要考虑缓存策略吗？",
            "如何处理高并发请求？",
            "系统监控应该怎么做？",
            "如何进行性能优化？",
            "部署策略有什么建议？",
            "安全方面需要注意什么？",
            "测试策略应该如何制定？",
            "代码质量如何保证？",
            "团队协作流程怎么规范？"
        };
        
        for (int i = 0; i < userQuestions.length; i++) {
            Message userMessage = Message.builder()
                .content(userQuestions[i])
                .role("user")
                .createTime(System.currentTimeMillis())
                .build();
                
            // 添加到角色记忆中
            role.putMessage(userMessage);
            
            // 模拟助手回复
            Message assistantMessage = Message.builder()
                .content("这是关于 '" + userQuestions[i] + "' 的详细回答。包含技术细节、最佳实践和具体实现方案...")
                .role("assistant")
                .createTime(System.currentTimeMillis() + 100)
                .build();
                
            role.putMessage(assistantMessage);
            
            log.info("添加对话 {}: {}", i + 1, userQuestions[i]);
            
            // 检查上下文统计
            if ((i + 1) % 5 == 0) {
                ConversationContextManager.ContextStats stats = role.getContextStats();
                if (stats != null) {
                    log.info("当前统计 - 消息数: {}, 字符数: {}, 估算tokens: {}, 需要压缩: {}", 
                        stats.getMessageCount(), stats.getTotalCharacters(), 
                        stats.getEstimatedTokens(), stats.needsCompression());
                }
            }
        }
        
        log.info("长对话模拟完成");
    }
    
    /**
     * 测试手动压缩
     */
    private void testManualCompression(ReactorRole role) {
        log.info("测试手动压缩功能...");
        
        // 显示压缩前状态
        ConversationContextManager.ContextStats beforeStats = role.getContextStats();
        if (beforeStats != null) {
            log.info("压缩前 - 消息数: {}, 字符数: {}, 估算tokens: {}", 
                beforeStats.getMessageCount(), beforeStats.getTotalCharacters(), beforeStats.getEstimatedTokens());
        }
        
        // 执行手动压缩
        role.manualCompressContext().thenAccept(success -> {
            if (success) {
                log.info("✅ 手动压缩成功!");
                
                // 显示压缩后状态
                ConversationContextManager.ContextStats afterStats = role.getContextStats();
                if (afterStats != null) {
                    log.info("压缩后 - 消息数: {}, 字符数: {}, 估算tokens: {}", 
                        afterStats.getMessageCount(), afterStats.getTotalCharacters(), afterStats.getEstimatedTokens());
                    
                    if (beforeStats != null) {
                        double compressionRatio = 1.0 - (double) afterStats.getMessageCount() / beforeStats.getMessageCount();
                        log.info("压缩比例: {:.2%}", compressionRatio);
                    }
                }
            } else {
                log.warn("❌ 手动压缩失败");
            }
        }).join(); // 等待完成
    }
    
    /**
     * 显示上下文统计
     */
    private void showContextStats(ReactorRole role) {
        log.info("=== 最终上下文统计 ===");
        
        ConversationContextManager.ContextStats stats = role.getContextStats();
        if (stats != null) {
            log.info("消息数量: {}", stats.getMessageCount());
            log.info("总字符数: {}", stats.getTotalCharacters());
            log.info("估算tokens: {}", stats.getEstimatedTokens());
            log.info("需要压缩: {}", stats.needsCompression());
        } else {
            log.info("无法获取上下文统计信息");
        }
        
        log.info("是否正在压缩: {}", role.isContextCompressing());
    }
    
    /**
     * 测试压缩命令
     */
    public void testCompressionCommands(ReactorRole role) {
        log.info("=== 测试压缩命令 ===");
        
        String[] commands = {
            "/compress",
            "/compact", 
            "/summarize",
            "/smol",
            "请压缩对话",
            "总结对话历史"
        };
        
        for (String command : commands) {
            Message commandMessage = Message.builder()
                .content(command)
                .role("user")
                .createTime(System.currentTimeMillis())
                .build();
                
            log.info("测试命令: {}", command);
            
            // 这里可以测试命令识别
            // 在实际使用中，这些命令会在act方法中被处理
        }
    }
}
