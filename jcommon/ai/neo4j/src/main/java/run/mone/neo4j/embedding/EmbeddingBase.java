package run.mone.neo4j.embedding;

import java.util.List;

/**
 * 嵌入模型基础接口
 * 定义嵌入模型的基本功能
 */
public interface EmbeddingBase {
    
    /**
     * 生成文本的嵌入向量
     * 
     * @param text 要嵌入的文本
     * @param memoryAction 内存操作类型（如"add", "search"等）
     * @return 嵌入向量
     */
    List<Double> embed(String text, String memoryAction);
    
    /**
     * 批量生成文本的嵌入向量
     * 
     * @param texts 要嵌入的文本列表
     * @param memoryAction 内存操作类型
     * @return 嵌入向量列表
     */
    List<List<Double>> embedBatch(List<String> texts, String memoryAction);
    
    /**
     * 获取嵌入向量的维度
     * 
     * @return 向量维度
     */
    int getDimensions();
}
