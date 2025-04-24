package run.mone.agentx.entity;

import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_agent")
public class Agent extends BaseEntity {
    private String name;
    
    @Column("agent_group")
    private String group;
    
    private String version;
    private String description;
    private String agentUrl;
    private Long createdBy;
    private Boolean isPublic;
    private byte[] image;

    @Column("tool_map")
    private String toolMap;

    @Column("mcp_tool_map")
    private String mcpToolMap;
}