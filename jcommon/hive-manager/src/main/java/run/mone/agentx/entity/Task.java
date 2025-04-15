package run.mone.agentx.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_task")
public class Task extends BaseEntity {
    private String taskUuid;
    private Long clientAgentId;
    private Long serverAgentId;
    private Long skillId;
    private String title;
    private String description;
    private String status;
    private String result;
}