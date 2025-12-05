package run.mone.mcp.milinenew.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RunPipelineParams {

    private long pipelineId;
    private long projectId;
    private String gitBranch;
    private String commitId;
    private String imageTag;
    private String remark;
    private String flowParam;
    private String runType;
    private List<Integer> changeIds;
}
