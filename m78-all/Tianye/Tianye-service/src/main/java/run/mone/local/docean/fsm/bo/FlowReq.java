package run.mone.local.docean.fsm.bo;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class FlowReq {

    private String userName;

    // TODO add history
    private String history;

    private boolean syncFlowStatusToM78;

    private boolean singleNodeTest;

    private Map<Integer, List<Integer>> ifEdgeMap ;

    private Map<Integer, List<Integer>> elseEdgeMap;

    private Map<Integer, List<NodeEdge>> outgoingEdgesMap;

    private String m78RpcAddr;

    private Map<String, String> meta;

    private Map<Integer, List<FlowData>> subFlowDataMap;

    private List<NodeEdge> nodeEdges;
}
