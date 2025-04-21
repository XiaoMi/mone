package run.mone.agentx.dto;

import lombok.Data;
import run.mone.agentx.entity.Agent;
import run.mone.agentx.entity.AgentInstance;

/**
 * MCP请求对象
 */
@Data
public class McpRequest {
    /**
     * 外部标签
     */
    private String outerTag;
    
    /**
     * 内容
     */
    private McpContent content;

    private String data;


    private String clientId;

    private Agent agent;

    private AgentInstance agentInstance;
    
    /**
     * MCP内容
     */
    @Data
    public static class McpContent {
        /**
         * 服务器名称
         */
        private String server_name;
        
        /**
         * 工具名称
         */
        private String tool_name;
        
        /**
         * 参数
         */
        private String arguments;
    }
} 