package run.mone.agentx.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TaskDTO {
    private String taskUuid;
    private Long clientAgentId;
    private Long serverAgentId;
    private Long skillId;
    private String title;
    private String description;
    private String taskContent;
    private String metadata; // json
    private String status;
    private String result;
    private String username;
    private String token;
}
