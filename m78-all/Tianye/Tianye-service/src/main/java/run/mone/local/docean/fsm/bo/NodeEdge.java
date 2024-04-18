package run.mone.local.docean.fsm.bo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wmin
 * @date 2024/3/12
 */
@Data
@Builder
public class NodeEdge implements Serializable {
    private int sourceNodeId;
    private int targetNodeId;
}
