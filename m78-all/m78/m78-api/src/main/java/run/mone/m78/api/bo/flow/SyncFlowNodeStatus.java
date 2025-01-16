package run.mone.m78.api.bo.flow;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wmin
 * @date 2024/4/16
 */
@Data
@Builder
public class SyncFlowNodeStatus implements Serializable {

    private long timestamp;

    private String flowRecordId;

    private int nodeId;

    private List<SyncFlowStatus.SyncNodeInputDetail> nodeInput;

    private List<SyncFlowStatus.SyncNodeOutputDetail> nodeOutput;

    private int status;

    private String errorInfo;

    private long durationTime;
}
