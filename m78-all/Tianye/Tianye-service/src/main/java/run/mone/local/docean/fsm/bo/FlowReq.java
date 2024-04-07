package run.mone.local.docean.fsm.bo;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class FlowReq {

    private String userName;

    private boolean syncFlowStatusToM78;

    private Map<Integer, List<Integer>> ifEdgeMap ;

    private Map<Integer, List<Integer>> elseEdgeMap;
}
