package run.mone.mcp.milinenew.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * K8s批次部署参数类
 *
 * @author liguanchen
 * @date 2025/12/11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class K8sBatchDeployParams {

    /**
     * 批次号
     */
    private int batchNum;

    /**
     * 流水线运行记录ID
     */
    private long pipelineRecordId;

    /**
     * 流水线部署ID
     */
    private long pipelineDeployId;
}
