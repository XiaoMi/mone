package run.mone.agentx.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_agent")
public class Agent extends BaseEntity {
    private String name;
    private String description;
    private String agentUrl;
    private Long createdBy;
}