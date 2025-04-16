package run.mone.agentx.entity;

import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_agent")
public class Agent extends BaseEntity {
    private String name;
    private String group;
    private String version;
    private String description;
    private String agentUrl;
    private Long createdBy;
    private Boolean isPublic;
    private Long ctime;
    private Long utime;
}