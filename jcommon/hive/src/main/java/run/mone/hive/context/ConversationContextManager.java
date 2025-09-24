package run.mone.hive.context;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.Message;
import run.mone.hive.task.FocusChainSettings;
import run.mone.hive.task.TaskState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 对话上下文管理器
 * 整合ContextManager和AiContextCompressor，提供统一的上下文管理接口
 */
@Slf4j
public class ConversationContextManager {
    
    private final ContextManager contextManager;
    private final AiContextCompressor aiCompressor;
    private final AtomicBoolean isCompressing = new AtomicBoolean(false);
    
    // 配置参数
    private boolean enableAiCompression = true;
    private boolean enableRuleBasedOptimization = true;
    private int maxMessagesBeforeCompression = 20;
    
    public ConversationContextManager(LLM llm) {
        this.contextManager = new ContextManager();
        this.aiCompressor = new AiContextCompressor(llm);
    }
    
    public ConversationContextManager(LLM llm, int maxTokens, int compressionThreshold, double compressionRatioThreshold) {
        this.contextManager = new ContextManager(maxTokens, compressionThreshold, compressionRatioThreshold);
        this.aiCompressor = new AiContextCompressor(llm);
    }
    
    /**
     * 处理新消息并管理上下文
     * 这是主要的入口方法
     */
    public CompletableFuture<ContextProcessingResult> processNewMessage(List<Message> currentMessages, 
                                                                       Message newMessage,
                                                                       TaskState taskState,
                                                                       FocusChainSettings focusChainSettings) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 添加新消息到列表
                List<Message> updatedMessages = new ArrayList<>(currentMessages);
                updatedMessages.add(newMessage);
                
                // 检查是否需要压缩
                boolean shouldCompress = shouldCompressContext(updatedMessages, taskState);
                
                if (shouldCompress && enableAiCompression && !isCompressing.get()) {
                    log.info("触发上下文压缩，当前消息数: {}", updatedMessages.size());
                    return performContextCompression(updatedMessages, taskState, focusChainSettings);
                } else if (enableRuleBasedOptimization) {
                    // 应用规则基础的优化
                    return performRuleBasedOptimization(updatedMessages, taskState);
                } else {
                    // 不需要处理，直接返回
                    return new ContextProcessingResult(updatedMessages, false, false, null);
                }
                
            } catch (Exception e) {
                log.error("处理新消息时发生异常", e);
                List<Message> fallbackMessages = new ArrayList<>(currentMessages);
                fallbackMessages.add(newMessage);
                return new ContextProcessingResult(fallbackMessages, false, false, "处理异常: " + e.getMessage());
            }
        });
    }
    
    /**
     * 判断是否需要压缩上下文
     */
    private boolean shouldCompressContext(List<Message> messages, TaskState taskState) {
        // 1. 检查消息数量
        if (messages.size() >= maxMessagesBeforeCompression) {
            log.debug("消息数量达到压缩阈值: {} >= {}", messages.size(), maxMessagesBeforeCompression);
            return true;
        }
        
        // 2. 使用ContextManager的token估算
        if (contextManager.shouldCompactContextWindow(messages, taskState)) {
            log.debug("Token数量达到压缩阈值");
            return true;
        }
        
        return false;
    }
    
    /**
     * 执行AI驱动的上下文压缩
     */
    private ContextProcessingResult performContextCompression(List<Message> messages, 
                                                            TaskState taskState,
                                                            FocusChainSettings focusChainSettings) {
        if (!isCompressing.compareAndSet(false, true)) {
            log.warn("上下文压缩正在进行中，跳过本次压缩");
            return new ContextProcessingResult(messages, false, false, "压缩正在进行中");
        }
        
        try {
            log.info("开始AI上下文压缩，原始消息数: {}", messages.size());
            
            // 1. 首先应用规则基础的优化
            ContextManager.OptimizationResult optimization = contextManager.applyContextOptimizations(
                messages, 2, System.currentTimeMillis());
            
            List<Message> optimizedMessages = messages;
            if (optimization.hasUpdates()) {
                optimizedMessages = contextManager.applyContextHistoryUpdates(messages, 2);
                log.info("规则优化完成，优化了 {} 个消息", optimization.getUniqueFileReadIndices().size());
            }
            
            // 2. 进行AI压缩
            AiContextCompressor.CompressionResult compressionResult = 
                aiCompressor.compressContext(optimizedMessages, focusChainSettings);
            
            if (compressionResult.isSuccess()) {
                // 3. 创建压缩后的消息列表
                List<Message> compressedMessages = aiCompressor.createCompressedMessages(
                    optimizedMessages, compressionResult);
                
                // 4. 更新任务状态
                if (taskState != null) {
                    taskState.incrementApiRequestCount();
                }
                
                log.info("AI上下文压缩成功: {} -> {} 消息", messages.size(), compressedMessages.size());
                return new ContextProcessingResult(compressedMessages, true, true, null);
                
            } else {
                log.warn("AI压缩失败，回退到规则优化: {}", compressionResult.getErrorMessage());
                return new ContextProcessingResult(optimizedMessages, optimization.hasUpdates(), false, 
                    "AI压缩失败: " + compressionResult.getErrorMessage());
            }
            
        } catch (Exception e) {
            log.error("AI上下文压缩过程中发生异常", e);
            return new ContextProcessingResult(messages, false, false, "压缩异常: " + e.getMessage());
        } finally {
            isCompressing.set(false);
        }
    }
    
    /**
     * 执行规则基础的优化
     */
    private ContextProcessingResult performRuleBasedOptimization(List<Message> messages, TaskState taskState) {
        try {
            long timestamp = System.currentTimeMillis();
            
            // 应用上下文优化
            ContextManager.OptimizationResult optimization = contextManager.applyContextOptimizations(
                messages, 2, timestamp);
            
            if (optimization.hasUpdates()) {
                List<Message> optimizedMessages = contextManager.applyContextHistoryUpdates(messages, 2);
                
                // 计算优化效果
                double optimizationRatio = contextManager.calculateContextOptimizationMetrics(
                    messages, null, optimization.getUniqueFileReadIndices());
                
                log.info("规则优化完成，优化比例: {:.2%}, 处理消息数: {}", 
                    optimizationRatio, optimization.getUniqueFileReadIndices().size());
                
                return new ContextProcessingResult(optimizedMessages, true, false, null);
            } else {
                return new ContextProcessingResult(messages, false, false, null);
            }
            
        } catch (Exception e) {
            log.error("规则优化过程中发生异常", e);
            return new ContextProcessingResult(messages, false, false, "优化异常: " + e.getMessage());
        }
    }
    
    /**
     * 手动触发上下文压缩
     */
    public CompletableFuture<ContextProcessingResult> manualCompression(List<Message> messages,
                                                                       TaskState taskState,
                                                                       FocusChainSettings focusChainSettings) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("手动触发上下文压缩");
            return performContextCompression(messages, taskState, focusChainSettings);
        });
    }
    
    /**
     * 获取上下文统计信息
     */
    public ContextStats getContextStats(List<Message> messages) {
        int messageCount = messages.size();
        int totalCharacters = messages.stream()
            .mapToInt(msg -> msg.getContent() != null ? msg.getContent().length() : 0)
            .sum();
        int estimatedTokens = (int) Math.ceil(totalCharacters / 3.5); // 简单估算
        
        boolean needsCompression = shouldCompressContext(messages, null);
        
        return new ContextStats(messageCount, totalCharacters, estimatedTokens, needsCompression);
    }
    
    // 配置方法
    public void setEnableAiCompression(boolean enableAiCompression) {
        this.enableAiCompression = enableAiCompression;
    }
    
    public void setEnableRuleBasedOptimization(boolean enableRuleBasedOptimization) {
        this.enableRuleBasedOptimization = enableRuleBasedOptimization;
    }
    
    public void setMaxMessagesBeforeCompression(int maxMessagesBeforeCompression) {
        this.maxMessagesBeforeCompression = maxMessagesBeforeCompression;
    }
    
    public boolean isCompressing() {
        return isCompressing.get();
    }
    
    // 内部类定义
    
    /**
     * 上下文处理结果
     */
    public static class ContextProcessingResult {
        private final List<Message> processedMessages;
        private final boolean wasOptimized;
        private final boolean wasCompressed;
        private final String errorMessage;
        
        public ContextProcessingResult(List<Message> processedMessages, boolean wasOptimized, 
                                     boolean wasCompressed, String errorMessage) {
            this.processedMessages = processedMessages;
            this.wasOptimized = wasOptimized;
            this.wasCompressed = wasCompressed;
            this.errorMessage = errorMessage;
        }
        
        public List<Message> getProcessedMessages() { return processedMessages; }
        public boolean wasOptimized() { return wasOptimized; }
        public boolean wasCompressed() { return wasCompressed; }
        public String getErrorMessage() { return errorMessage; }
        public boolean hasError() { return errorMessage != null; }
        
        @Override
        public String toString() {
            return "ContextProcessingResult{" +
                   "messageCount=" + (processedMessages != null ? processedMessages.size() : 0) +
                   ", wasOptimized=" + wasOptimized +
                   ", wasCompressed=" + wasCompressed +
                   ", hasError=" + hasError() +
                   '}';
        }
    }
    
    /**
     * 上下文统计信息
     */
    public static class ContextStats {
        private final int messageCount;
        private final int totalCharacters;
        private final int estimatedTokens;
        private final boolean needsCompression;
        
        public ContextStats(int messageCount, int totalCharacters, int estimatedTokens, boolean needsCompression) {
            this.messageCount = messageCount;
            this.totalCharacters = totalCharacters;
            this.estimatedTokens = estimatedTokens;
            this.needsCompression = needsCompression;
        }
        
        public int getMessageCount() { return messageCount; }
        public int getTotalCharacters() { return totalCharacters; }
        public int getEstimatedTokens() { return estimatedTokens; }
        public boolean needsCompression() { return needsCompression; }
        
        @Override
        public String toString() {
            return "ContextStats{" +
                   "messageCount=" + messageCount +
                   ", totalCharacters=" + totalCharacters +
                   ", estimatedTokens=" + estimatedTokens +
                   ", needsCompression=" + needsCompression +
                   '}';
        }
    }
}
