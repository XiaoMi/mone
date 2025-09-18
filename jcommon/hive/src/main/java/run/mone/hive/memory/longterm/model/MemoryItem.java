package run.mone.hive.memory.longterm.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.time.LocalDateTime;

/**
 * 记忆项数据模型
 * 对应mem0的MemoryItem
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoryItem {
    
    /**
     * 记忆项唯一标识符
     */
    private String id;
    
    /**
     * 记忆内容
     */
    private String memory;
    
    /**
     * 记忆内容的哈希值
     */
    private String hash;
    
    /**
     * 相似度得分
     */
    private Double score;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 代理ID
     */
    private String agentId;
    
    /**
     * 运行ID
     */
    private String runId;
    
    /**
     * 角色ID
     */
    private String actorId;
    
    /**
     * 角色类型
     */
    private String role;
    
    /**
     * 额外元数据
     */
    private Map<String, Object> metadata;
    
    /**
     * 事件类型 (ADD, UPDATE, DELETE, NONE)
     */
    private String event;
    
    /**
     * 记忆类型 (procedural_memory等)
     */
    private String memoryType;
    
    /**
     * 转换为简化的Map格式
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", id != null ? id : "");
        map.put("memory", memory != null ? memory : "");
        map.put("score", score != null ? score : 0.0);
        map.put("hash", hash != null ? hash : "");
        map.put("created_at", createdAt != null ? createdAt.toString() : "");
        map.put("updated_at", updatedAt != null ? updatedAt.toString() : "");
        map.put("user_id", userId != null ? userId : "");
        map.put("agent_id", agentId != null ? agentId : "");
        map.put("run_id", runId != null ? runId : "");
        map.put("actor_id", actorId != null ? actorId : "");
        map.put("role", role != null ? role : "");
        map.put("metadata", metadata != null ? metadata : new java.util.HashMap<>());
        return map;
    }
}
