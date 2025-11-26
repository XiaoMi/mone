package run.mone.agentx.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_skill")
public class Skill extends BaseEntity {
    private Long agentId;
    private String name;
    private String skillId;
    private String description;
    private String tags;
    private String examples;
    private String outputSchema;
}