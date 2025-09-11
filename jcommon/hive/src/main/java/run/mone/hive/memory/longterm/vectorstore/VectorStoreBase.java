package run.mone.hive.memory.longterm.vectorstore;

import run.mone.hive.memory.longterm.config.VectorStoreConfig;
import run.mone.hive.memory.longterm.model.MemoryItem;

import java.util.List;
import java.util.Map;

/**
 * 向量存储基础接口
 * 定义所有向量存储提供商的通用接口
 */
public interface VectorStoreBase {
    
    /**
     * 插入向量数据
     * 
     * @param vectors 向量列表
     * @param ids 向量ID列表
     * @param payloads 载荷数据列表
     */
    void insert(List<List<Double>> vectors, List<String> ids, List<Map<String, Object>> payloads);
    
    /**
     * 搜索相似向量
     * 
     * @param query 查询文本
     * @param vectors 查询向量
     * @param limit 返回数量限制
     * @param filters 过滤条件
     * @return 搜索结果
     */
    List<MemoryItem> search(String query, List<Double> vectors, int limit, Map<String, Object> filters);
    
    /**
     * 根据ID获取向量
     * 
     * @param vectorId 向量ID
     * @return 内存项
     */
    MemoryItem get(String vectorId);
    
    /**
     * 列出所有向量
     * 
     * @param filters 过滤条件
     * @param limit 数量限制
     * @return 内存项列表
     */
    List<MemoryItem> list(Map<String, Object> filters, int limit);
    
    /**
     * 更新向量
     * 
     * @param vectorId 向量ID
     * @param vector 新向量
     * @param payload 新载荷数据
     */
    void update(String vectorId, List<Double> vector, Map<String, Object> payload);
    
    /**
     * 删除向量
     * 
     * @param vectorId 向量ID
     */
    void delete(String vectorId);
    
    /**
     * 删除集合
     */
    void deleteCol();
    
    /**
     * 重置存储
     */
    void reset();
    
    /**
     * 获取配置
     * 
     * @return 向量存储配置
     */
    VectorStoreConfig getConfig();
    
    /**
     * 验证连接
     * 
     * @return 是否连接成功
     */
    default boolean validateConnection() {
        try {
            // 尝试一个简单的操作来验证连接
            list(Map.of(), 1);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取集合信息
     * 
     * @return 集合信息
     */
    default Map<String, Object> getCollectionInfo() {
        return Map.of(
            "name", getConfig().getCollectionName(),
            "dimensions", getConfig().getEmbeddingModelDims(),
            "provider", getConfig().getProvider().getValue()
        );
    }
    
    /**
     * 检查集合是否存在
     * 
     * @return 是否存在
     */
    default boolean collectionExists() {
        try {
            getCollectionInfo();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 创建集合
     */
    default void createCollection() {
        // 默认实现为空，子类可以重写
    }
    
    /**
     * 获取向量数量
     * 
     * @return 向量数量
     */
    default long getVectorCount() {
        try {
            return list(Map.of(), Integer.MAX_VALUE).size();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * 获取存储统计信息
     * 
     * @return 统计信息
     */
    default Map<String, Object> getStats() {
        return Map.of(
            "vector_count", getVectorCount(),
            "collection_name", getConfig().getCollectionName(),
            "provider", getConfig().getProvider().getValue(),
            "dimensions", getConfig().getEmbeddingModelDims()
        );
    }
    
    /**
     * 关闭连接
     */
    default void close() {
        // 默认实现为空，子类可以重写
    }
}
