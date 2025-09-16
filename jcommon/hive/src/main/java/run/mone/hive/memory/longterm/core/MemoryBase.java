package run.mone.hive.memory.longterm.core;

import run.mone.hive.memory.longterm.model.MemoryItem;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Memory基础接口
 * 定义长期记忆的核心操作
 */
public interface MemoryBase {
    
    /**
     * 添加记忆
     * 
     * @param messages 消息列表或单个消息
     * @param userId 用户ID
     * @param agentId 代理ID  
     * @param runId 运行ID
     * @param metadata 元数据
     * @param infer 是否进行推理 (true: 使用LLM提取关键事实, false: 直接存储)
     * @param memoryType 记忆类型
     * @param prompt 自定义提示词
     * @return 操作结果
     */
    Map<String, Object> add(Object messages, String userId, String agentId, String runId, 
                           Map<String, Object> metadata, boolean infer, String memoryType, String prompt);
    
    /**
     * 异步添加记忆
     */
    CompletableFuture<Map<String, Object>> addAsync(Object messages, String userId, String agentId, String runId,
                                                   Map<String, Object> metadata, boolean infer, String memoryType, String prompt);
    
    /**
     * 根据ID获取记忆
     * 
     * @param memoryId 记忆ID
     * @return 记忆项
     */
    MemoryItem get(String memoryId);
    
    /**
     * 异步获取记忆
     */
    CompletableFuture<MemoryItem> getAsync(String memoryId);
    
    /**
     * 获取所有记忆
     * 
     * @param userId 用户ID
     * @param agentId 代理ID
     * @param runId 运行ID
     * @param filters 过滤条件
     * @param limit 限制数量
     * @return 记忆列表
     */
    Map<String, Object> getAll(String userId, String agentId, String runId, 
                              Map<String, Object> filters, int limit);
    
    /**
     * 异步获取所有记忆
     */
    CompletableFuture<Map<String, Object>> getAllAsync(String userId, String agentId, String runId,
                                                      Map<String, Object> filters, int limit);
    
    /**
     * 搜索记忆
     * 
     * @param query 查询文本
     * @param userId 用户ID
     * @param agentId 代理ID
     * @param runId 运行ID
     * @param limit 限制数量
     * @param filters 过滤条件
     * @param threshold 相似度阈值
     * @return 搜索结果
     */
    Map<String, Object> search(String query, String userId, String agentId, String runId,
                              int limit, Map<String, Object> filters, Double threshold);
    
    /**
     * 异步搜索记忆
     */
    CompletableFuture<Map<String, Object>> searchAsync(String query, String userId, String agentId, String runId,
                                                      int limit, Map<String, Object> filters, Double threshold);
    
    /**
     * 更新记忆
     * 
     * @param memoryId 记忆ID
     * @param data 新数据
     * @return 操作结果
     */
    Map<String, Object> update(String memoryId, String data);
    
    /**
     * 异步更新记忆
     */
    CompletableFuture<Map<String, Object>> updateAsync(String memoryId, String data);
    
    /**
     * 删除记忆
     * 
     * @param memoryId 记忆ID
     * @return 操作结果
     */
    Map<String, Object> delete(String memoryId);
    
    /**
     * 异步删除记忆
     */
    CompletableFuture<Map<String, Object>> deleteAsync(String memoryId);
    
    /**
     * 删除所有记忆
     * 
     * @param userId 用户ID
     * @param agentId 代理ID
     * @param runId 运行ID
     * @return 操作结果
     */
    Map<String, Object> deleteAll(String userId, String agentId, String runId);
    
    /**
     * 异步删除所有记忆
     */
    CompletableFuture<Map<String, Object>> deleteAllAsync(String userId, String agentId, String runId);
    
    /**
     * 获取记忆历史
     * 
     * @param memoryId 记忆ID
     * @return 历史记录
     */
    List<Map<String, Object>> history(String memoryId);
    
    /**
     * 异步获取记忆历史
     */
    CompletableFuture<List<Map<String, Object>>> historyAsync(String memoryId);
    
    /**
     * 重置记忆存储
     * 
     * @return 操作结果
     */
    Map<String, Object> reset();
    
    /**
     * 异步重置记忆存储
     */
    CompletableFuture<Map<String, Object>> resetAsync();
}
