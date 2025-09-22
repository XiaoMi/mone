package run.mone.hive.mcp.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 记忆查询配置
 * 用于配置长期记忆搜索功能
 * 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemoryQuery {

    /**
     * 是否自动进行记忆搜索
     */
    @Builder.Default
    private boolean autoMemoryQuery = false;
    
    /**
     * 记忆配置版本
     */
    private String version;
    
    /**
     * 模型类型
     */
    private String modelType;
    
    /**
     * 发布服务名
     */
    private String releaseServiceName;
    
    /**
     * 记忆搜索的最大结果数量
     */
    @Builder.Default
    private int maxResults = 5;
    
    /**
     * 相似度阈值（0.0-1.0）
     */
    @Builder.Default
    private double threshold = 0.7;
    
    /**
     * 用户ID（用于用户级别的记忆搜索）
     */
    private String userId;
    
    /**
     * 代理ID（用于代理级别的记忆搜索）
     */
    private String agentId;
    
    /**
     * 会话ID（用于会话级别的记忆搜索）
     */
    private String sessionId;
    
    /**
     * 记忆类型过滤
     */
    private String memoryType;
}