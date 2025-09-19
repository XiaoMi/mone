package run.mone.agentx.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_agent_config")
public class AgentConfig extends BaseEntity {
    @Column("agent_id")
    private Long agentId;
    
    @Column("user_id")
    private Long userId;
    
    @Column("config_key")
    private String key;
    
    @Column("config_value")
    private String value;

} 