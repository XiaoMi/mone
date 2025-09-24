package run.mone.agentx.dto;

import lombok.Data;
import run.mone.agentx.entity.AgentInstance;

import java.util.Map;

/**
 * MCP请求对象
 */
@Data
public class McpRequest {

    private Long agentId;

    private AgentInstance agentInstance;

    private String data;

    private String clientId;

    private Map<String, String> mapData;

}