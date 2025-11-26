package run.mone.hive.context;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.FluxSink;
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
    private int maxMessagesBeforeCompression = 25;

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
                                                                        FocusChainSettings focusChainSettings, FluxSink sink) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 添加新消息到列表
                List<Message> updatedMessages = new ArrayList<>(currentMessages);
                updatedMessages.add(newMessage);
                
                // 检查是否需要压缩
                boolean shouldCompress = shouldCompressContext(updatedMessages, taskState);
                
                if (shouldCompress && enableAiCompression && !isCompressing.get()) {
                    log.info("触发上下文压缩，当前消息数: {}", updatedMessages.size());
                    sink.next("<chat>触发上下文压缩，当前消息数: " + updatedMessages.size()+"</chat>");
                    return performContextCompression(updatedMessages, taskState, focusChainSettings);
                } else if (enableRuleBasedOptimization) {
                    // 应用规则基础的优化
                    return performRuleBasedOptimization(updatedMessages, taskState);
                } else {
                    // 不需要处理，直接返回
                    return new ContextProcessingResult(updatedMessages, 0, false, false, null);
                }
                
            } catch (Exception e) {
                log.error("处理新消息时发生异常", e);
                List<Message> fallbackMessages = new ArrayList<>(currentMessages);
                fallbackMessages.add(newMessage);
                return new ContextProcessingResult(fallbackMessages, 0, false, false, "处理异常: " + e.getMessage());
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
            return new ContextProcessingResult(messages, 0, false, false, "压缩正在进行中");
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

                int compressedTokenNum = calCompressedMessagesToken(messages, compressedMessages);
                log.info("AI上下文压缩成功: {} -> {} 消息, 节省了约 {} tokens", 
                    messages.size(), compressedMessages.size(), compressedTokenNum);
                return new ContextProcessingResult(compressedMessages, compressedTokenNum, true, true, null);
                
            } else {
                log.warn("AI压缩失败，回退到规则优化: {}", compressionResult.getErrorMessage());
                return new ContextProcessingResult(optimizedMessages, 0, optimization.hasUpdates(), false,
                    "AI压缩失败: " + compressionResult.getErrorMessage());
            }
            
        } catch (Exception e) {
            log.error("AI上下文压缩过程中发生异常", e);
            return new ContextProcessingResult(messages, 0, false, false, "压缩异常: " + e.getMessage());
        } finally {
            isCompressing.set(false);
        }
    }

    /**
     * 计算压缩前后消息的token数量变化
     * @param messages 原始消息列表
     * @param compressedMessages 压缩后的消息列表
     * @return 节省的token数量（正值表示减少了token）
     */
    private int calCompressedMessagesToken(List<Message> messages, List<Message> compressedMessages) {
        if (messages == null || compressedMessages == null) {
            return 0;
        }
        
        // 计算原始消息的总字符数
        int originalCharCount = messages.stream()
            .mapToInt(msg -> msg.getContent() != null ? msg.getContent().length() : 0)
            .sum();
            
        // 计算压缩后消息的总字符数
        int compressedCharCount = compressedMessages.stream()
            .mapToInt(msg -> msg.getContent() != null ? msg.getContent().length() : 0)
            .sum();
            
        // 使用与其他地方相同的token估算方法（字符数/3.5）
        int originalTokens = (int) Math.ceil(originalCharCount / 3.5);
        int compressedTokens = (int) Math.ceil(compressedCharCount / 3.5);
        
        // 返回节省的token数量
        return originalTokens - compressedTokens;
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
                
                // 计算优化后节省的token数量
                int optimizedTokenNum = calCompressedMessagesToken(messages, optimizedMessages);
                return new ContextProcessingResult(optimizedMessages, optimizedTokenNum, true, false, null);
            } else {
                return new ContextProcessingResult(messages, 0, false, false, null);
            }
            
        } catch (Exception e) {
            log.error("规则优化过程中发生异常", e);
            return new ContextProcessingResult(messages, 0, false, false, "优化异常: " + e.getMessage());
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
        private final int compressedTokenNum;
        private final boolean wasOptimized;
        private final boolean wasCompressed;
        private final String errorMessage;

        public ContextProcessingResult(List<Message> processedMessages, int compressedTokenNum, boolean wasOptimized,
                                     boolean wasCompressed, String errorMessage) {
            this.processedMessages = processedMessages;
            this.compressedTokenNum = compressedTokenNum;
            this.wasOptimized = wasOptimized;
            this.wasCompressed = wasCompressed;
            this.errorMessage = errorMessage;
        }
        
        public List<Message> getProcessedMessages() { return processedMessages; }
        public int getCompressedTokenNum() { return compressedTokenNum; }
        public boolean wasOptimized() { return wasOptimized; }
        public boolean wasCompressed() { return wasCompressed; }
        public String getErrorMessage() { return errorMessage; }
        public boolean hasError() { return errorMessage != null; }
        
        @Override
        public String toString() {
            return "ContextProcessingResult{" +
                    "messageCount=" + (processedMessages != null ? processedMessages.size() : 0) +
                    ", compressedTokenNum=" + compressedTokenNum +
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
