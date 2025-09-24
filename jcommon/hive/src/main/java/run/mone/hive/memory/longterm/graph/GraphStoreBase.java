package run.mone.hive.memory.longterm.graph;

import run.mone.hive.memory.longterm.config.GraphStoreConfig;

import java.util.List;
import java.util.Map;

/**
 * 图数据库基础接口
 * 定义所有图数据库提供商的通用接口
 * 基于mem0的图存储设计
 */
public interface GraphStoreBase {
    
    /**
     * 图实体关系结构
     */
    class GraphEntity {
        private String source;
        private String destination;
        private String relationship;
        private String sourceType;
        private String destinationType;
        
        public GraphEntity(String source, String destination, String relationship, 
                          String sourceType, String destinationType) {
            this.source = source;
            this.destination = destination;
            this.relationship = relationship;
            this.sourceType = sourceType;
            this.destinationType = destinationType;
        }
        
        // Getters and setters
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        
        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
        
        public String getRelationship() { return relationship; }
        public void setRelationship(String relationship) { this.relationship = relationship; }
        
        public String getSourceType() { return sourceType; }
        public void setSourceType(String sourceType) { this.sourceType = sourceType; }
        
        public String getDestinationType() { return destinationType; }
        public void setDestinationType(String destinationType) { this.destinationType = destinationType; }
    }
    
    /**
     * 图记忆操作类型
     */
    enum GraphAction {
        ADD("add_graph_memory"),
        UPDATE("update_graph_memory"),
        DELETE("delete_graph_memory"),
        NOOP("noop");
        
        private final String value;
        
        GraphAction(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static GraphAction fromString(String value) {
            for (GraphAction action : GraphAction.values()) {
                if (action.value.equalsIgnoreCase(value)) {
                    return action;
                }
            }
            return NOOP;
        }
    }
    
    /**
     * 添加图记忆 - 创建新的关系
     * 
     * @param source 源节点
     * @param destination 目标节点
     * @param relationship 关系类型
     * @param sourceType 源节点类型
     * @param destinationType 目标节点类型
     * @return 操作结果
     */
    Map<String, Object> addMemory(String source, String destination, String relationship, 
                                 String sourceType, String destinationType);
    
    /**
     * 更新图记忆 - 更新现有关系
     * 
     * @param source 源节点
     * @param destination 目标节点
     * @param relationship 新的关系类型
     * @return 操作结果
     */
    Map<String, Object> updateMemory(String source, String destination, String relationship);
    
    /**
     * 删除图记忆 - 删除关系
     * 
     * @param source 源节点
     * @param destination 目标节点
     * @param relationship 要删除的关系
     * @return 操作结果
     */
    Map<String, Object> deleteMemory(String source, String destination, String relationship);
    
    /**
     * 搜索相关的图记忆
     * 
     * @param query 查询字符串
     * @param limit 结果限制数量
     * @return 搜索结果列表
     */
    List<Map<String, Object>> search(String query, int limit);
    
    /**
     * 获取所有图记忆
     * 
     * @param limit 结果限制数量
     * @return 图记忆列表
     */
    List<Map<String, Object>> getAll(int limit);
    
    /**
     * 从文本中提取实体
     * 
     * @param text 输入文本
     * @return 实体列表，每个实体包含name和type
     */
    List<Map<String, Object>> extractEntities(String text);
    
    /**
     * 从文本中建立实体关系
     * 
     * @param text 输入文本
     * @return 关系列表，每个关系包含source、destination、relationship
     */
    List<GraphEntity> establishRelations(String text);
    
    /**
     * 检查关系是否存在
     * 
     * @param source 源节点
     * @param destination 目标节点
     * @param relationship 关系类型
     * @return 是否存在该关系
     */
    boolean relationshipExists(String source, String destination, String relationship);
    
    /**
     * 获取节点的所有关系
     * 
     * @param nodeName 节点名称
     * @return 相关关系列表
     */
    List<Map<String, Object>> getNodeRelationships(String nodeName);
    
    /**
     * 删除所有图数据
     */
    void deleteAll();
    
    /**
     * 获取配置
     * 
     * @return 图存储配置
     */
    GraphStoreConfig getConfig();
    
    /**
     * 验证连接
     * 
     * @return 是否连接成功
     */
    default boolean validateConnection() {
        try {
            getAll(1);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取图统计信息
     * 
     * @return 统计信息
     */
    default Map<String, Object> getStats() {
        Map<String, Object> stats = new java.util.HashMap<>();
        try {
            List<Map<String, Object>> allMemories = getAll(Integer.MAX_VALUE);
            stats.put("total_memories", allMemories.size());
            stats.put("provider", getConfig().getProvider().getValue());
            stats.put("enabled", getConfig().isEnabled());
        } catch (Exception e) {
            stats.put("total_memories", 0);
            stats.put("error", e.getMessage());
        }
        return stats;
    }
    
    /**
     * 批量添加图记忆
     * 
     * @param entities 实体关系列表
     * @return 批量操作结果
     */
    default List<Map<String, Object>> addMemories(List<GraphEntity> entities) {
        List<Map<String, Object>> results = new java.util.ArrayList<>();
        for (GraphEntity entity : entities) {
            Map<String, Object> result = addMemory(
                entity.getSource(), 
                entity.getDestination(), 
                entity.getRelationship(),
                entity.getSourceType(), 
                entity.getDestinationType()
            );
            results.add(result);
        }
        return results;
    }
    
    /**
     * 关闭连接
     */
    default void close() {
        // 默认实现为空，子类可以重写
    }
}