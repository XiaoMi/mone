package run.mone.m78.api.bo.flow;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wmin
 * @date 2024/2/29
 */
@Data
public class Edge implements Serializable {
    private int sourceNodeId;

    private int sourceSubNodeId;

    private int targetNodeId;

    //前端回显需要
    private String extraInfo;

    /**
     * null(默认值,需要传sourceNodeId、targetNodeId)
     * if(需要传sourceNodeId、targetNodeId)
     * else(需要传sourceNodeId、targetNodeId)
     * subSourceNodeToTargetNode(需要传sourceNodeId、targetNodeId、sourceSubNodeId，e.g. 意图识别场景、新版条件选择器)
     */
    private String conditionFlag;
}
