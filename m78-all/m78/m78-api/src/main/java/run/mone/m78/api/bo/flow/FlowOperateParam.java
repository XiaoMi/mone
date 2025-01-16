package run.mone.m78.api.bo.flow;

import lombok.Builder;
import lombok.Data;
import run.mone.m78.api.enums.FlowOperateCmdEnum;

import java.io.Serializable;
import java.util.Map;

/**
 * @author wmin
 * @date 2024/2/29
 */
@Data
public class FlowOperateParam implements Serializable {
    private Integer flowId;

    private Integer flowRecordId;

    //当前操作的node
    private Integer nodeId;

    private String userName;

    /**
     * @see FlowOperateCmdEnum
     */
    private String cmd;

    private Map<String, String> meta;


    @Builder
    @Data
    public static class ManualConfirmReq implements Serializable {

        private Integer nodeId;

        private Map<String,String> meta;

    }

}
