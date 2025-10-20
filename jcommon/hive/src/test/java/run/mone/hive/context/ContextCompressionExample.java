package run.mone.hive.context;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.UnicastProcessor;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.Message;
import run.mone.hive.task.FocusChainSettings;
import run.mone.hive.task.SummarizeTaskCommand;
import run.mone.hive.task.TaskState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 上下文压缩功能的示例和测试类
 */
@Slf4j
public class ContextCompressionExample {
    
    public static void main(String[] args) {
        try {
            // 创建示例
            ContextCompressionExample example = new ContextCompressionExample();
            
            // 运行基本示例
            example.runBasicCompressionExample();
            
            // 运行完整工作流示例
            example.runFullWorkflowExample();
            
        } catch (Exception e) {
            log.error("运行示例时发生异常", e);
        }
    }
    
    /**
     * 基本压缩示例
     */
    public void runBasicCompressionExample() {
        log.info("=== 基本上下文压缩示例 ===");
        
        // 1. 创建LLM配置和实例
        LLM llm = createTestLLM();
        
        // 2. 创建上下文管理器
        ConversationContextManager contextManager = new ConversationContextManager(llm);
        
        // 3. 创建模拟对话历史
        List<Message> messages = createSampleMessages();
        log.info("创建了 {} 条示例消息", messages.size());
        
        // 4. 检查上下文统计
        ConversationContextManager.ContextStats stats = contextManager.getContextStats(messages);
        log.info("上下文统计: {}", stats);
        
        // 5. 执行压缩（如果需要）
        if (stats.needsCompression()) {
            TaskState taskState = new TaskState();
            FocusChainSettings focusChainSettings = new FocusChainSettings();
            focusChainSettings.setEnabled(true);
            
            CompletableFuture<ConversationContextManager.ContextProcessingResult> future = 
                contextManager.processNewMessage(messages, createNewMessage("请帮我总结一下我们的对话"), taskState, focusChainSettings,  UnicastProcessor.create().sink());
            
            future.thenAccept(result -> {
                log.info("压缩结果: {}", result);
                if (result.wasCompressed()) {
                    log.info("压缩成功！消息数量: {} -> {}", 
                        messages.size(), result.getProcessedMessages().size());
                } else if (result.wasOptimized()) {
                    log.info("应用了规则优化");
                } else {
                    log.info("无需处理");
                }
            }).exceptionally(throwable -> {
                log.error("压缩过程中发生异常", throwable);
                return null;
            }).join(); // 等待完成
        }
    }
    
    /**
     * 完整工作流示例
     */
    public void runFullWorkflowExample() {
        log.info("\n=== 完整工作流示例 ===");
        
        // 1. 初始化组件
        LLM llm = createTestLLM();
        ConversationContextManager contextManager = new ConversationContextManager(llm);
        SummarizeTaskCommand summarizeCommand = new SummarizeTaskCommand(contextManager);
        
        // 2. 创建长对话历史
        List<Message> longConversation = createLongConversation();
        log.info("创建了长对话，包含 {} 条消息", longConversation.size());
        
        // 3. 检查是否需要总结
        TaskState taskState = new TaskState();
        boolean shouldSummarize = summarizeCommand.shouldSummarize(longConversation, taskState);
        log.info("是否需要总结: {}", shouldSummarize);
        
        if (shouldSummarize) {
            // 4. 执行总结
            FocusChainSettings focusChainSettings = new FocusChainSettings();
            focusChainSettings.setEnabled(true);
            
            CompletableFuture<SummarizeTaskCommand.SummarizeResult> future = 
                summarizeCommand.executeSummarization(longConversation, taskState, focusChainSettings);
            
            future.thenAccept(result -> {
                if (result.isSuccess()) {
                    log.info("总结成功！");
                    log.info("原始消息数: {}", longConversation.size());
                    log.info("压缩后消息数: {}", result.getProcessedMessages().size());
                    
                    // 显示压缩后的消息
                    for (int i = 0; i < Math.min(3, result.getProcessedMessages().size()); i++) {
                        Message msg = result.getProcessedMessages().get(i);
                        log.info("消息 {}: [{}] {}", i, msg.getRole(), 
                            msg.getContent().length() > 100 ? 
                            msg.getContent().substring(0, 100) + "..." : 
                            msg.getContent());
                    }
                } else {
                    log.error("总结失败: {}", result.getErrorMessage());
                }
            }).join();
        }
        
        // 5. 演示手动压缩命令
        demonstrateManualCompression(summarizeCommand);
    }
    
    /**
     * 演示手动压缩命令
     */
    private void demonstrateManualCompression(SummarizeTaskCommand command) {
        log.info("\n=== 手动压缩命令示例 ===");
        
        // 测试命令匹配
        String[] testInputs = {
            "/summarize_task",
            "/summary",
            "/compress",
            "/compact",  // 这个不应该匹配
            "/summarize_task 请总结对话"
        };
        
        for (String input : testInputs) {
            boolean matches = command.matches(input);
            log.info("输入: '{}' -> 匹配: {}", input, matches);
        }
        
        // 生成压缩prompt
        FocusChainSettings settings = new FocusChainSettings();
        settings.setEnabled(true);
        
        String prompt = command.execute("/summarize_task", settings);
        log.info("生成的压缩prompt长度: {} 字符", prompt.length());
        log.info("Prompt预览: {}", prompt.substring(0, Math.min(200, prompt.length())) + "...");
    }
    
    /**
     * 创建测试用的LLM实例
     */
    private LLM createTestLLM() {
        // 注意：这里需要根据实际环境配置LLM
        LLMConfig config = new LLMConfig();
        // config.setLlmProvider(LLMProvider.OPENAI); // 或其他提供商
        // config.setApiKey("your-api-key-here"); // 需要实际的API密钥
        config.setModel("gpt-3.5-turbo"); // 或其他模型
        
        return new LLM(config);
    }
    
    /**
     * 创建示例消息
     */
    private List<Message> createSampleMessages() {
        List<Message> messages = new ArrayList<>();
        
        messages.add(Message.builder()
            .content("你好，我需要帮助开发一个Java项目")
            .role("user")
            .createTime(System.currentTimeMillis() - 10000)
            .build());
            
        messages.add(Message.builder()
            .content("你好！我很乐意帮助你开发Java项目。请告诉我你的具体需求。")
            .role("assistant")
            .createTime(System.currentTimeMillis() - 9000)
            .build());
            
        messages.add(Message.builder()
            .content("我想实现一个上下文压缩功能，类似于Cline中的prompt压缩")
            .role("user")
            .createTime(System.currentTimeMillis() - 8000)
            .build());
            
        messages.add(Message.builder()
            .content("这是一个很好的想法！上下文压缩可以帮助管理长对话。我可以帮你设计和实现这个功能。")
            .role("assistant")
            .createTime(System.currentTimeMillis() - 7000)
            .build());
            
        return messages;
    }
    
    /**
     * 创建长对话用于测试压缩
     */
    private List<Message> createLongConversation() {
        List<Message> messages = new ArrayList<>();
        
        // 模拟一个关于软件开发的长对话
        String[] userMessages = {
            "我需要开发一个分布式系统",
            "系统需要支持高并发",
            "如何设计数据库架构？",
            "需要考虑缓存策略吗？",
            "如何处理分布式事务？",
            "系统监控怎么做？",
            "如何进行性能优化？",
            "部署策略是什么？",
            "如何保证系统安全？",
            "需要写哪些测试？"
        };
        
        String[] assistantMessages = {
            "分布式系统设计需要考虑多个方面，包括服务拆分、数据一致性、容错处理等...",
            "高并发处理可以通过负载均衡、连接池、异步处理等技术实现...",
            "数据库架构设计需要考虑分库分表、读写分离、主从复制等策略...",
            "缓存是分布式系统的重要组成部分，可以使用Redis、Memcached等...",
            "分布式事务可以通过2PC、TCC、Saga等模式实现...",
            "系统监控包括应用监控、基础设施监控、业务监控等多个层面...",
            "性能优化需要从代码层面、架构层面、基础设施层面综合考虑...",
            "部署可以采用容器化、微服务、CI/CD等现代化部署方式...",
            "系统安全包括认证授权、数据加密、网络安全等多个方面...",
            "测试策略包括单元测试、集成测试、性能测试、安全测试等..."
        };
        
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < userMessages.length; i++) {
            // 用户消息
            messages.add(Message.builder()
                .content(userMessages[i])
                .role("user")
                .createTime(currentTime - (userMessages.length - i) * 1000L * 2)
                .build());
                
            // 助手消息
            messages.add(Message.builder()
                .content(assistantMessages[i])
                .role("assistant")
                .createTime(currentTime - (userMessages.length - i) * 1000L * 2 + 500)
                .build());
        }
        
        return messages;
    }
    
    /**
     * 创建新消息
     */
    private Message createNewMessage(String content) {
        return Message.builder()
            .content(content)
            .role("user")
            .createTime(System.currentTimeMillis())
            .build();
    }
}
