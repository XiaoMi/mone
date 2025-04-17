package run.mone.agentx.entity;

import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 代理实例实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_agent_instance")
public class AgentInstance extends BaseEntity {
    /**
     * 代理ID
     */
    private Long agentId;

    /**
     * 实例IP地址
     */
    private String ip;

    /**
     * 实例端口
     */
    private Integer port;

    /**
     * 最后心跳时间
     */
    private Long lastHeartbeatTime;

    /**
     * 是否活跃
     */
    private Boolean isActive;
} 