package run.mone.hive.task;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.context.ConversationContextManager;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.Message;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * SummarizeTask命令处理器
 * 对应Cline中的summarize_task工具
 */
@Slf4j
public class SummarizeTaskCommand implements SlashCommand {
    
    private final ConversationContextManager contextManager;
    
    public SummarizeTaskCommand(LLM llm) {
        this.contextManager = new ConversationContextManager(llm);
    }
    
    public SummarizeTaskCommand(ConversationContextManager contextManager) {
        this.contextManager = contextManager;
    }
    
    @Override
    public String getName() {
        return "summarize_task";
    }
    
    @Override
    public String getDescription() {
        return "Creates a comprehensive detailed summary of the conversation to compact the context window";
    }
    
    @Override
    public boolean matches(String input) {
        String trimmed = input.trim();
        return trimmed.startsWith("/summarize_task") || 
               trimmed.startsWith("/summary") ||
               trimmed.startsWith("/compress");
    }
    
    @Override
    public String execute(String input, FocusChainSettings focusChainSettings) {
        // 这个方法主要用于生成prompt，实际的压缩逻辑在ConversationContextManager中
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("<explicit_instructions type=\"summarize_task\">\n");
        prompt.append("The user has explicitly requested to create a detailed summary of the conversation so far, ");
        prompt.append("which will be used to compact the current context window while retaining key information.\n\n");
        
        prompt.append("You are required to analyze the entire conversation history and create a comprehensive summary ");
        prompt.append("that captures all important details, technical decisions, code changes, and the current state of work.\n\n");
        
        prompt.append("Your response should follow the structured format defined in the summarize_task tool specification.\n");
        
        if (focusChainSettings != null && focusChainSettings.isEnabled()) {
            prompt.append("Make sure to include the current task_progress list in your summary.\n");
        }
        
        prompt.append("</explicit_instructions>\n");
        
        return prompt.toString();
    }
    
    /**
     * 执行上下文压缩
     */
    public CompletableFuture<SummarizeResult> executeSummarization(List<Message> messages,
                                                                  TaskState taskState,
                                                                  FocusChainSettings focusChainSettings) {
        return contextManager.manualCompression(messages, taskState, focusChainSettings)
            .thenApply(result -> {
                if (result.hasError()) {
                    log.error("上下文压缩失败: {}", result.getErrorMessage());
                    return new SummarizeResult(false, messages, result.getErrorMessage());
                } else {
                    log.info("上下文压缩成功: 压缩={}, 优化={}", result.wasCompressed(), result.wasOptimized());
                    return new SummarizeResult(true, result.getProcessedMessages(), null);
                }
            })
            .exceptionally(throwable -> {
                log.error("上下文压缩过程中发生异常", throwable);
                return new SummarizeResult(false, messages, "压缩异常: " + throwable.getMessage());
            });
    }
    
    /**
     * 检查是否需要压缩
     */
    public boolean shouldSummarize(List<Message> messages, TaskState taskState) {
        if (messages == null || messages.isEmpty()) {
            return false;
        }
        
        ConversationContextManager.ContextStats stats = contextManager.getContextStats(messages);
        return stats.needsCompression();
    }
    
    /**
     * 获取上下文统计信息
     */
    public ConversationContextManager.ContextStats getContextStats(List<Message> messages) {
        return contextManager.getContextStats(messages);
    }
    
    /**
     * 总结结果类
     */
    public static class SummarizeResult {
        private final boolean success;
        private final List<Message> processedMessages;
        private final String errorMessage;
        
        public SummarizeResult(boolean success, List<Message> processedMessages, String errorMessage) {
            this.success = success;
            this.processedMessages = processedMessages;
            this.errorMessage = errorMessage;
        }
        
        public boolean isSuccess() { return success; }
        public List<Message> getProcessedMessages() { return processedMessages; }
        public String getErrorMessage() { return errorMessage; }
        
        @Override
        public String toString() {
            return "SummarizeResult{" +
                   "success=" + success +
                   ", messageCount=" + (processedMessages != null ? processedMessages.size() : 0) +
                   ", hasError=" + (errorMessage != null) +
                   '}';
        }
    }
}
