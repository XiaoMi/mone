package run.mone.local.docean.fsm;

import lombok.Builder;
import lombok.Data;
import run.mone.local.docean.fsm.bo.FlowData;
import run.mone.local.docean.fsm.bo.NodeEdge;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class BotReq {

    private String flowRecordId;

    private boolean syncFlowStatusToM78;

    private List<FlowData> flowDataList;

    private List<NodeEdge> nodeEdges;

    private String userName;

    //sourceId <-> targetIds
    private Map<Integer, List<Integer>> ifEdgeMap;

    private Map<Integer, List<Integer>> elseEdgeMap;

    private String id;

    private boolean debug;

}
