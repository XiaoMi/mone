package run.mone.hive.memory.longterm.embeddings;

import run.mone.hive.memory.longterm.config.EmbedderConfig;

import java.util.List;

/**
 * 嵌入向量基础接口
 * 定义所有嵌入模型提供商的通用接口
 */
public interface EmbeddingBase {
    
    /**
     * 嵌入动作类型
     */
    enum MemoryAction {
        ADD("add"),
        SEARCH("search"), 
        UPDATE("update");
        
        private final String value;
        
        MemoryAction(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static MemoryAction fromString(String value) {
            for (MemoryAction action : MemoryAction.values()) {
                if (action.value.equalsIgnoreCase(value)) {
                    return action;
                }
            }
            return ADD; // 默认为ADD
        }
    }
    
    /**
     * 生成文本的嵌入向量
     * 
     * @param text 要嵌入的文本
     * @param memoryAction 记忆动作类型 (add/search/update)
     * @return 嵌入向量
     */
    List<Double> embed(String text, String memoryAction);
    
    /**
     * 生成文本的嵌入向量
     * 
     * @param text 要嵌入的文本
     * @param memoryAction 记忆动作类型
     * @return 嵌入向量
     */
    default List<Double> embed(String text, MemoryAction memoryAction) {
        return embed(text, memoryAction.getValue());
    }
    
    /**
     * 批量生成嵌入向量
     * 
     * @param texts 文本列表
     * @param memoryAction 记忆动作类型
     * @return 嵌入向量列表
     */
    default List<List<Double>> embedBatch(List<String> texts, String memoryAction) {
        return texts.stream()
            .map(text -> embed(text, memoryAction))
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 获取嵌入向量维度
     * 
     * @return 向量维度
     */
    int getDimensions();
    
    /**
     * 获取配置
     * 
     * @return 嵌入器配置
     */
    EmbedderConfig getConfig();
    
    /**
     * 验证配置
     * 
     * @throws IllegalArgumentException 如果配置无效
     */
    default void validateConfig() {
        EmbedderConfig config = getConfig();
        if (config == null) {
            throw new IllegalArgumentException("Embedder config cannot be null");
        }
        
        if (config.getModel() == null || config.getModel().trim().isEmpty()) {
            throw new IllegalArgumentException("Embedding model name cannot be null or empty");
        }
        
        if (config.getEmbeddingDims() <= 0) {
            throw new IllegalArgumentException("Embedding dimensions must be positive");
        }
    }
    
    /**
     * 测试嵌入功能
     * 
     * @return 是否测试成功
     */
    default boolean test() {
        try {
            List<Double> embedding = embed("test", "add");
            return embedding != null && !embedding.isEmpty() && embedding.size() == getDimensions();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 关闭资源
     */
    default void close() {
        // 默认实现为空，子类可以重写
    }
}
