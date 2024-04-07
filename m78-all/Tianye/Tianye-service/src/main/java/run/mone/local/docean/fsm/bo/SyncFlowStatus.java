package run.mone.local.docean.fsm.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
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
    private String flowRecordId;
    //节点入参
    @Builder.Default
    private Map<Integer, SyncNodeInput> nodeInputsMap = new ConcurrentHashMap<>();
    //节点出参
    @Builder.Default
    private Map<Integer, SyncNodeOutput> nodeOutputsMap = new ConcurrentHashMap<>();
    //todo 2成功 3失败
    private int endFlowStatus;
    private EndFlowOutput endFlowOutput;
    private long timestamp;
    private long durationTime;

    @Data
    @Builder
    public static class SyncNodeInput implements Serializable {
        private int nodeId;
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
    }

    @Data
    @Builder
    public static class SyncNodeOutput implements Serializable {
        private int nodeId;
        //todo 1开始 2成功 3失败
        private int status;
        private List<SyncNodeOutputDetail> outputDetails;
        private String errorInfo;
        private long durationTime;
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
