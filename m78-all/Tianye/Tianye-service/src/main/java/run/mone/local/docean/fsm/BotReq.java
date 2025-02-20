package run.mone.local.docean.fsm;

import lombok.Builder;
import lombok.Data;
import run.mone.local.docean.fsm.bo.FlowData;
import run.mone.local.docean.fsm.bo.NodeEdge;
import run.mone.local.docean.fsm.bo.OutputData;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class BotReq {

    //m78发过来的服务器地址(因为有多个m78服务器)
    private String m78RpcAddr;

    private String flowId;

    private String flowRecordId;

    private int executeType;

    private boolean syncFlowStatusToM78;

    private String history;

    private List<FlowData> flowDataList;

    private List<NodeEdge> nodeEdges;

    private String userName;

    //sourceId <-> targetIds
    private Map<Integer, List<Integer>> ifEdgeMap;

    /**
     * 选择器if
     * sourceId <-> targetIds
     */
    private Map<Integer, List<Integer>> elseEdgeMap;

    /**
     * 一个node有多条outgoing edge，比如 意图识别 场景
     * sourceNodeId <-> NodeEdge(source子id <-> target id)
     */
    private Map<Integer, List<NodeEdge>> outgoingEdgesMap;

    private String id;

    private boolean debug;

    //单节点测试
    private boolean singleNodeTest;

    //指定从该节点开始运行
    private Integer specifiedStartNodeId;

    private String cmd;

    private String message;

    private Map<String, String> meta;

    private Map<Integer, Map<String, OutputData>> referenceData;

    // 子图 e.g. 循环节点
    // parentNodeId <-> flowData
    private Map<Integer, List<FlowData>> subFlowDataMap;

}
