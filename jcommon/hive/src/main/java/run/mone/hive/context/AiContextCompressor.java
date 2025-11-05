package run.mone.hive.context;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.Message;
import run.mone.hive.task.FocusChainSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI驱动的上下文压缩器
 * 使用AI模型智能总结对话历史
 */
@Slf4j
public class AiContextCompressor {
    
    private final LLM llm;
    private final Gson gson = new Gson();
    
    // 用于提取AI总结结果的正则表达式
    private static final Pattern SUMMARIZE_TASK_PATTERN = Pattern.compile(
        "<summarize_task>\\s*<context>([\\s\\S]*?)</context>(?:\\s*<task_progress>([\\s\\S]*?)</task_progress>)?\\s*</summarize_task>", 
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    
    public AiContextCompressor(LLM llm) {
        this.llm = llm;
    }
    
    /**
     * 异步压缩对话历史
     * 
     * @param messages 对话历史消息列表
     * @param focusChainSettings Focus Chain设置
     * @return 压缩结果的Future
     */
    public CompletableFuture<CompressionResult> compressContextAsync(List<Message> messages, 
                                                                    FocusChainSettings focusChainSettings) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return compressContext(messages, focusChainSettings);
            } catch (Exception e) {
                log.error("AI上下文压缩失败", e);
                return new CompressionResult(false, null, null, e.getMessage());
            }
        });
    }
    
    /**
     * 同步压缩对话历史
     */
    public CompressionResult compressContext(List<Message> messages, FocusChainSettings focusChainSettings) {
        try {
            // 1. 构建压缩请求消息
            List<Message> compressionMessages = buildCompressionMessages(messages, focusChainSettings);
            
            // 2. 调用AI进行压缩
            log.info("开始AI上下文压缩，消息数量: {}", messages.size());
            String aiResponse = callAiForCompression(compressionMessages);
            
            // 3. 解析AI响应
            CompressionResult result = parseCompressionResponse(aiResponse);
            
            if (result.isSuccess()) {
                log.info("AI上下文压缩成功，原始消息数: {}, 压缩后摘要长度: {} 字符", 
                    messages.size(), result.getSummary().length());
            } else {
                log.warn("AI上下文压缩失败: {}", result.getErrorMessage());
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("AI上下文压缩过程中发生异常", e);
            return new CompressionResult(false, null, null, "压缩过程异常: " + e.getMessage());
        }
    }
    
    /**
     * 构建用于压缩的消息列表
     */
    private List<Message> buildCompressionMessages(List<Message> originalMessages, FocusChainSettings focusChainSettings) {
        List<Message> compressionMessages = new ArrayList<>(originalMessages);
        
        // 添加压缩指令消息
        String compressionPrompt = ContextPrompts.summarizeTask(focusChainSettings);
        Message compressionInstruction = Message.builder()
            .content(compressionPrompt)
            .role("user")
            .causeBy("system")
            .createTime(System.currentTimeMillis())
            .build();
        
        compressionMessages.add(compressionInstruction);
        
        return compressionMessages;
    }
    
    /**
     * 调用AI进行压缩
     */
    private String callAiForCompression(List<Message> messages) {
        try {
            // 将消息转换为LLM可以理解的格式
            StringBuilder conversationContext = new StringBuilder();
            
            for (Message message : messages) {
                conversationContext.append("Role: ").append(message.getRole()).append("\n");
                conversationContext.append("Content: ").append(message.getContent()).append("\n");
                if (message.getCauseBy() != null) {
                    conversationContext.append("CauseBy: ").append(message.getCauseBy()).append("\n");
                }
                conversationContext.append("---\n");
            }
            
            // 调用LLM
            String response = llm.chat(conversationContext.toString());
            
            log.debug("AI压缩响应: {}", response);
            return response;
            
        } catch (Exception e) {
            log.error("调用AI进行上下文压缩时发生错误", e);
            throw new RuntimeException("AI调用失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 解析AI的压缩响应
     */
    private CompressionResult parseCompressionResponse(String aiResponse) {
        if (aiResponse == null || aiResponse.trim().isEmpty()) {
            return new CompressionResult(false, null, null, "AI响应为空");
        }
        
        // 尝试提取summarize_task标签中的内容
        Matcher matcher = SUMMARIZE_TASK_PATTERN.matcher(aiResponse);
        
        if (matcher.find()) {
            String summary = matcher.group(1);
            String taskProgress = matcher.group(2);
            
            if (summary != null && !summary.trim().isEmpty()) {
                return new CompressionResult(true, summary.trim(), taskProgress != null ? taskProgress.trim() : null, null);
            }
        }
        
        // 如果没有找到标准格式，尝试直接使用响应内容
        if (aiResponse.length() > 100) { // 确保有足够的内容作为摘要
            log.warn("AI响应不符合预期格式，使用原始响应作为摘要");
            return new CompressionResult(true, aiResponse, null, null);
        }
        
        return new CompressionResult(false, null, null, "无法从AI响应中提取有效摘要");
    }
    
    /**
     * 创建压缩后的消息列表
     */
    public List<Message> createCompressedMessages(List<Message> originalMessages, 
                                                 CompressionResult compressionResult) {
        if (!compressionResult.isSuccess()) {
            log.warn("压缩结果无效，返回原始消息");
            return originalMessages;
        }
        
        List<Message> compressedMessages = new ArrayList<>();
        
        // 保留第一条用户消息（通常是任务描述）
        if (!originalMessages.isEmpty()) {
            Message firstMessage = originalMessages.get(0);
            // 可能需要截断过长的第一条消息
            String processedContent = ContextPrompts.processFirstUserMessageForTruncation(firstMessage.getContent());
            
            Message processedFirstMessage = Message.builder()
                .id(firstMessage.getId())
                .content(processedContent)
                .role(firstMessage.getRole())
                .causeBy(firstMessage.getCauseBy())
                .instructContent(firstMessage.getInstructContent())
                .sentFrom(firstMessage.getSentFrom())
                .sendTo(firstMessage.getSendTo())
                .images(firstMessage.getImages())
                .data(firstMessage.getData())
                .type(firstMessage.getType())
                .meta(firstMessage.getMeta())
                .createTime(firstMessage.getCreateTime())
                .clientId(firstMessage.getClientId())
                .voiceBase64(firstMessage.getVoiceBase64())
                .userId(firstMessage.getUserId())
                .agentId(firstMessage.getAgentId())
                .build();
                
            compressedMessages.add(processedFirstMessage);
        }
        
        // 添加压缩摘要作为助手消息
        Message summaryMessage = Message.builder()
            .content(ContextPrompts.contextTruncationNotice() + "\n\n" + 
                    ContextPrompts.continuationPrompt(compressionResult.getSummary()))
            .role("assistant")
            .causeBy("context_compression")
            .createTime(System.currentTimeMillis())
            .build();
            
        compressedMessages.add(summaryMessage);
        
        // 保留最后几条消息以维持对话连续性
        int keepLastCount = Math.min(5, originalMessages.size() - 1); // 保留最后4条消息
        if (keepLastCount > 0) {
            List<Message> lastMessages = originalMessages.subList(
                originalMessages.size() - keepLastCount, 
                originalMessages.size()
            );
            compressedMessages.addAll(lastMessages);
        }
        
        log.info("消息压缩完成: {} -> {}", originalMessages.size(), compressedMessages.size());
        return compressedMessages;
    }
    
    /**
     * 压缩结果类
     */
    public static class CompressionResult {
        private final boolean success;
        private final String summary;
        private final String taskProgress;
        private final String errorMessage;
        
        public CompressionResult(boolean success, String summary, String taskProgress, String errorMessage) {
            this.success = success;
            this.summary = summary;
            this.taskProgress = taskProgress;
            this.errorMessage = errorMessage;
        }
        
        public boolean isSuccess() { return success; }
        public String getSummary() { return summary; }
        public String getTaskProgress() { return taskProgress; }
        public String getErrorMessage() { return errorMessage; }
        
        @Override
        public String toString() {
            return "CompressionResult{" +
                   "success=" + success +
                   ", summaryLength=" + (summary != null ? summary.length() : 0) +
                   ", hasTaskProgress=" + (taskProgress != null) +
                   ", errorMessage='" + errorMessage + '\'' +
                   '}';
        }
    }
}
