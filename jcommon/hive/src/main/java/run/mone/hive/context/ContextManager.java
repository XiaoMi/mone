package run.mone.hive.context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.Message;
import run.mone.hive.task.TaskState;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 上下文管理器 - 负责管理对话历史和prompt压缩
 * 移植自Cline的ContextManager功能
 */
@Slf4j
public class ContextManager {
    
    private static final int DEFAULT_MAX_TOKENS = 1000000;
    private static final int DEFAULT_COMPRESSION_THRESHOLD = 800000;
    private static final double COMPRESSION_RATIO_THRESHOLD = 0.8;
    
    private final Gson gson = new Gson();
    private final AtomicLong messageIdCounter = new AtomicLong(0);
    
    // 上下文历史更新映射
    // 格式: { messageIndex => [updateType, { blockIndex => [[timestamp, updateType, update, metadata]] }] }
    private final Map<Integer, ContextUpdate> contextHistoryUpdates = new ConcurrentHashMap<>();
    
    // 配置参数
    private int maxTokens;
    private int compressionThreshold;
    private double compressionRatioThreshold;
    
    public ContextManager() {
        this(DEFAULT_MAX_TOKENS, DEFAULT_COMPRESSION_THRESHOLD, COMPRESSION_RATIO_THRESHOLD);
    }
    
    public ContextManager(int maxTokens, int compressionThreshold, double compressionRatioThreshold) {
        this.maxTokens = maxTokens;
        this.compressionThreshold = compressionThreshold;
        this.compressionRatioThreshold = compressionRatioThreshold;
    }
    
    /**
     * 判断是否需要压缩上下文窗口
     */
    public boolean shouldCompactContextWindow(List<Message> messages, TaskState taskState) {
        if (messages == null || messages.isEmpty()) {
            return false;
        }
        
        // 估算总token数
        int totalTokens = estimateTokenCount(messages);
        
        log.debug("当前消息总token数: {}, 压缩阈值: {}", totalTokens, compressionThreshold);
        
        return totalTokens >= compressionThreshold;
    }
    
    /**
     * 估算消息列表的token数量
     */
    private int estimateTokenCount(List<Message> messages) {
        int totalTokens = 0;
        for (Message message : messages) {
            if (message.getContent() != null) {
                // 简单估算: 1个token约等于4个字符(对于中文)，3个字符(对于英文)
                // 这里使用平均值3.5
                totalTokens += Math.ceil(message.getContent().length() / 3.5);
            }
        }
        return totalTokens;
    }
    
    /**
     * 应用上下文优化（去重文件读取等）
     */
    public OptimizationResult applyContextOptimizations(List<Message> messages, int startFromIndex, long timestamp) {
        Set<Integer> uniqueFileReadIndices = new HashSet<>();
        boolean hasUpdates = false;
        
        // 查找重复的文件读取
        Map<String, List<Integer>> fileReadMap = findDuplicateFileReads(messages, startFromIndex);
        
        for (Map.Entry<String, List<Integer>> entry : fileReadMap.entrySet()) {
            String filePath = entry.getKey();
            List<Integer> indices = entry.getValue();
            
            // 如果同一个文件被读取多次，保留最后一次，其他的用简短通知替换
            if (indices.size() > 1) {
                for (int i = 0; i < indices.size() - 1; i++) {
                    int messageIndex = indices.get(i);
                    addContextUpdate(messageIndex, 0, timestamp, "text", 
                        "[[NOTE] This file read has been removed to save space in the context window. " +
                        "Refer to the latest file read for the most up to date version of this file.]");
                    uniqueFileReadIndices.add(messageIndex);
                    hasUpdates = true;
                }
            }
        }
        
        return new OptimizationResult(hasUpdates, uniqueFileReadIndices);
    }
    
    /**
     * 查找重复的文件读取
     */
    private Map<String, List<Integer>> findDuplicateFileReads(List<Message> messages, int startFromIndex) {
        Map<String, List<Integer>> fileReadMap = new HashMap<>();
        
        for (int i = startFromIndex; i < messages.size(); i++) {
            Message message = messages.get(i);
            if (message.getContent() != null) {
                // 查找文件读取模式
                String content = message.getContent();
                if (content.contains("read_file") || content.contains("<file_content")) {
                    String filePath = extractFilePathFromContent(content);
                    if (filePath != null) {
                        fileReadMap.computeIfAbsent(filePath, k -> new ArrayList<>()).add(i);
                    }
                }
            }
        }
        
        return fileReadMap;
    }
    
    /**
     * 从内容中提取文件路径
     */
    private String extractFilePathFromContent(String content) {
        // 简单的文件路径提取逻辑
        if (content.contains("path=\"")) {
            int start = content.indexOf("path=\"") + 6;
            int end = content.indexOf("\"", start);
            if (end > start) {
                return content.substring(start, end);
            }
        }
        return null;
    }
    
    /**
     * 获取截断范围
     */
    public TruncationRange getNextTruncationRange(List<Message> messages, 
                                                  TruncationRange currentDeletedRange, 
                                                  TruncationStrategy strategy) {
        // 保留第一对用户-助手消息
        int rangeStartIndex = 2;
        int startOfRest = currentDeletedRange != null ? currentDeletedRange.getEndIndex() + 1 : 2;
        
        int messagesToRemove;
        switch (strategy) {
            case NONE:
                messagesToRemove = Math.max(messages.size() - startOfRest, 0);
                break;
            case LAST_TWO:
                messagesToRemove = Math.max(messages.size() - startOfRest - 2, 0);
                break;
            case HALF:
                messagesToRemove = Math.max((messages.size() - startOfRest) / 2, 0);
                break;
            case QUARTER:
                messagesToRemove = Math.max((messages.size() - startOfRest) * 3 / 4, 0);
                break;
            default:
                messagesToRemove = Math.max((messages.size() - startOfRest) / 2, 0);
        }
        
        int rangeEndIndex = startOfRest + messagesToRemove - 1;
        
        // 确保最后移除的是助手消息，保持用户-助手-用户-助手的结构
        if (rangeEndIndex < messages.size() && 
            !"assistant".equals(messages.get(rangeEndIndex).getRole())) {
            rangeEndIndex -= 1;
        }
        
        return new TruncationRange(rangeStartIndex, rangeEndIndex);
    }
    
    /**
     * 应用上下文历史更新
     */
    public List<Message> applyContextHistoryUpdates(List<Message> messages, int startFromIndex) {
        List<Message> updatedMessages = new ArrayList<>();
        
        // 添加前两个消息（第一对用户-助手）
        for (int i = 0; i < Math.min(2, messages.size()); i++) {
            updatedMessages.add(applyUpdatesToMessage(messages.get(i), i));
        }
        
        // 添加剩余的消息
        for (int i = startFromIndex; i < messages.size(); i++) {
            updatedMessages.add(applyUpdatesToMessage(messages.get(i), i));
        }
        
        return updatedMessages;
    }
    
    /**
     * 将更新应用到单个消息
     */
    private Message applyUpdatesToMessage(Message originalMessage, int messageIndex) {
        ContextUpdate update = contextHistoryUpdates.get(messageIndex);
        if (update == null) {
            return originalMessage;
        }
        
        // 创建消息副本并应用更新
        Message updatedMessage = Message.builder()
            .id(originalMessage.getId())
            .content(update.getUpdatedContent())
            .role(originalMessage.getRole())
            .causeBy(originalMessage.getCauseBy())
            .instructContent(originalMessage.getInstructContent())
            .sentFrom(originalMessage.getSentFrom())
            .sendTo(originalMessage.getSendTo())
            .images(originalMessage.getImages())
            .data(originalMessage.getData())
            .type(originalMessage.getType())
            .meta(originalMessage.getMeta())
            .createTime(originalMessage.getCreateTime())
            .clientId(originalMessage.getClientId())
            .voiceBase64(originalMessage.getVoiceBase64())
            .userId(originalMessage.getUserId())
            .agentId(originalMessage.getAgentId())
            .build();
            
        return updatedMessage;
    }
    
    /**
     * 添加上下文更新
     */
    private void addContextUpdate(int messageIndex, int blockIndex, long timestamp, 
                                 String updateType, String updatedContent) {
        ContextUpdate update = new ContextUpdate(updateType, updatedContent, timestamp, new HashMap<>());
        contextHistoryUpdates.put(messageIndex, update);
    }
    
    /**
     * 应用标准上下文截断通知
     */
    public boolean applyStandardContextTruncationNotice(long timestamp) {
        if (!contextHistoryUpdates.containsKey(1)) {
            // 在第一个助手消息（索引1）添加截断通知
            String truncationNotice = "[NOTE] Some previous conversation history with the user has been removed " +
                "to maintain optimal context window length. The initial user task has been retained for continuity, " +
                "while intermediate conversation history has been removed. Keep this in mind as you continue " +
                "assisting the user. Pay special attention to the user's latest messages.";
            
            addContextUpdate(1, 0, timestamp, "text", truncationNotice);
            return true;
        }
        return false;
    }
    
    /**
     * 计算上下文优化指标
     */
    public double calculateContextOptimizationMetrics(List<Message> messages, 
                                                     TruncationRange deletedRange,
                                                     Set<Integer> uniqueFileReadIndices) {
        int totalCharacters = 0;
        int charactersSaved = 0;
        
        // 计算第一对消息的字符数
        for (int i = 0; i < Math.min(2, messages.size()); i++) {
            totalCharacters += getMessageCharacterCount(messages.get(i), i, uniqueFileReadIndices);
        }
        
        // 计算剩余消息的字符数
        int startIndex = deletedRange != null ? deletedRange.getEndIndex() + 1 : 2;
        for (int i = startIndex; i < messages.size(); i++) {
            int originalCount = messages.get(i).getContent() != null ? messages.get(i).getContent().length() : 0;
            int currentCount = getMessageCharacterCount(messages.get(i), i, uniqueFileReadIndices);
            
            totalCharacters += originalCount;
            if (uniqueFileReadIndices.contains(i)) {
                charactersSaved += originalCount - currentCount;
            }
        }
        
        return totalCharacters > 0 ? (double) charactersSaved / totalCharacters : 0.0;
    }
    
    private int getMessageCharacterCount(Message message, int index, Set<Integer> uniqueFileReadIndices) {
        if (uniqueFileReadIndices.contains(index)) {
            ContextUpdate update = contextHistoryUpdates.get(index);
            return update != null ? update.getUpdatedContent().length() : 
                   (message.getContent() != null ? message.getContent().length() : 0);
        }
        return message.getContent() != null ? message.getContent().length() : 0;
    }
    
    // 内部类定义
    
    /**
     * 上下文更新信息
     */
    public static class ContextUpdate {
        private String updateType;
        private String updatedContent;
        private long timestamp;
        private Map<String, Object> metadata;
        
        public ContextUpdate(String updateType, String updatedContent, long timestamp, Map<String, Object> metadata) {
            this.updateType = updateType;
            this.updatedContent = updatedContent;
            this.timestamp = timestamp;
            this.metadata = metadata;
        }
        
        // Getters
        public String getUpdateType() { return updateType; }
        public String getUpdatedContent() { return updatedContent; }
        public long getTimestamp() { return timestamp; }
        public Map<String, Object> getMetadata() { return metadata; }
    }
    
    /**
     * 优化结果
     */
    public static class OptimizationResult {
        private boolean hasUpdates;
        private Set<Integer> uniqueFileReadIndices;
        
        public OptimizationResult(boolean hasUpdates, Set<Integer> uniqueFileReadIndices) {
            this.hasUpdates = hasUpdates;
            this.uniqueFileReadIndices = uniqueFileReadIndices;
        }
        
        public boolean hasUpdates() { return hasUpdates; }
        public Set<Integer> getUniqueFileReadIndices() { return uniqueFileReadIndices; }
    }
    
    /**
     * 截断范围
     */
    public static class TruncationRange {
        private int startIndex;
        private int endIndex;
        
        public TruncationRange(int startIndex, int endIndex) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }
        
        public int getStartIndex() { return startIndex; }
        public int getEndIndex() { return endIndex; }
    }
    
    /**
     * 截断策略
     */
    public enum TruncationStrategy {
        NONE,      // 移除所有中间消息
        LAST_TWO,  // 保留最后两条消息
        HALF,      // 保留一半消息
        QUARTER    // 保留1/4消息
    }
}
