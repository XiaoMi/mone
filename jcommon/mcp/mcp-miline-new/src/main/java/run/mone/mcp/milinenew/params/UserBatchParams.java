package run.mone.mcp.milinenew.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 灰度部署(堡垒批次发布)参数类
 *
 * @author liguanchen
 * @date 2025/12/11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBatchParams {

    /**
     * 项目ID
     */
    private long projectId;

    /**
     * 流水线ID
     */
    private long pipelineId;

    /**
     * 流水线运行记录ID
     */
    private long pipelineRecordId;

    /**
     * 批次号
     */
    private int batchNum;

    /**
     * 操作类型
     */
    private int operation;

    /**
     * 是否强制检查
     */
    private boolean forceCheck;
}
