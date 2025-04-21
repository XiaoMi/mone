package run.mone.agentx.dto;

import lombok.Data;
import run.mone.agentx.entity.Agent;
import run.mone.agentx.entity.AgentInstance;

import java.util.List;

/**
 * 包含AgentInstance列表的Agent DTO
 */
@Data
public class AgentWithInstancesDTO {
    private Agent agent;
    private List<AgentInstance> instances;
} 