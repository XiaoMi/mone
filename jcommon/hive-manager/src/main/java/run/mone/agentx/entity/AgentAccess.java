package run.mone.agentx.entity;

import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_agent_access")
public class AgentAccess extends BaseEntity {
    private Long agentId;
    private String accessApp;
    private String accessKey;
    private String description;
    // 0: 禁用, 1: 启用
} 