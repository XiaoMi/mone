package run.mone.mcp.milinenew.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 取消流水线参数类
 *
 * @author liguanchen
 * @date 2025/12/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelPipelineParams {

    /**
     * 流水线ID
     */
    private long pipelineId;

    /**
     * 流水线运行记录ID
     */
    private long pipelineRecordId;
}
