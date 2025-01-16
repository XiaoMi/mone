package run.mone.m78.api.bo.flow;

import lombok.Data;
import run.mone.m78.api.enums.FlowNodeTypeEnum;

import java.io.Serializable;
import java.util.List;

/**
 * @author wmin
 * @date 2024/2/29
 */
@Data
public class NodeInfo implements Serializable {
    private int id;
    /**
     * @see FlowNodeTypeEnum
     */
    private String nodeType;

    private NodeMetaInfo nodeMetaInfo;

    private List<NodeInputInfo> inputs;

    private List<NodeOutputInfo> outputs;

    private List<NodeInputInfo> batchInfo;

    private String coreSetting;

    //single batch
    private String batchType;

    @Data
    public static class NodeMetaInfo implements Serializable{
        private String nodeName;
        private String desc;
        private NodePosition nodePosition;
        //前端回显需要
        private String extraInfo;

    }

    @Data
    public static class NodePosition implements Serializable {
        private String x;
        private String y;
    }
}


