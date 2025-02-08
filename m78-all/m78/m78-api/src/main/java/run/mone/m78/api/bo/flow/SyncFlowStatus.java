package run.mone.m78.api.bo.flow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wmin
 * @date 2024/3/4
 */
@Data
@Builder
public class SyncFlowStatus implements Serializable {
    private String flowId;
    private String flowRecordId;
    private int executeType;
    //节点入参
    @Builder.Default
    private Map<String, SyncNodeInput> nodeInputsMap = new ConcurrentHashMap<>();
    //节点出参
    @Builder.Default
    private Map<String, SyncNodeOutput> nodeOutputsMap = new ConcurrentHashMap<>();
    //todo -1开始 2成功 3失败 4取消 5暂停
    private int endFlowStatus;
    private boolean end;
    private EndFlowOutput endFlowOutput;
    private long timestamp;
    private long durationTime;

    @Builder.Default
    private String messageType = "FLOW_EXECUTE_STATUS";

    @Builder.Default
    private Map<String,String> meta = new HashMap<>();

    @Data
    @Builder
    public static class SyncNodeInput implements Serializable {
        private int nodeId;
        private String nodeType;
        private List<SyncNodeInputDetail> inputDetails;
    }

    @Data
    @Builder
    public static class SyncNodeInputDetail implements Serializable {
        private String name;
        private String value;
        //string、array，用于前端展示区分
        private String valueType;
        private String operator;
        private String name2;
        private String value2;
        private String type2;
        //新版condition,标识该input归属于哪个分支
        private String conditionIndex;
        private String conditionRelationship;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SyncNodeOutput implements Serializable {
        private int nodeId;
        private String nodeName;
        //todo 1开始 2成功 3失败 5暂停
        private int status;
        private String errorInfo;
        private Long durationTime;
        private List<SyncNodeOutputDetail> outputDetails;
    }

    @Data
    @Builder
    public static class SyncNodeOutputDetail implements Serializable {
        private String name;
        private String value;
        //string、array，用于前端展示区分
        private String valueType;
    }

    @Data
    @Builder
    public static class EndFlowOutput implements Serializable {
        private String answerContent;
        private List<EndFlowOutputDetail> endFlowOutputDetails;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EndFlowOutputDetail {
        private String name;
        private String value;
        //string、array，用于前端展示区分
        private String valueType;
    }
}
