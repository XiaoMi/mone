package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.NodeEnvFlagEnum;
import com.xiaomi.mone.tpc.common.enums.NodeTypeEnum;
import com.xiaomi.mone.tpc.common.enums.OperActionEnum;
import com.xiaomi.mone.tpc.common.enums.OutIdTypeEnum;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 环境节点同步
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class NodeResourceSyncParam extends BaseParam implements Serializable {

    private Long parentNodeId;
    private Long parentOutId;
    private Integer parentOutIdType;
    private String nodeName;
    private String desc;
    private Integer type;
    private Long nodeId;
    private Long outId;
    private Integer outIdType;
    private Integer envFlag;
    private List<Long> resourceIds;
    private Integer operAction;
    private Map<String,String> env;



    @Override
    public boolean argCheck() {
        OperActionEnum actionEnum = OperActionEnum.getEnum(operAction);
        if (OperActionEnum.ADD.equals(actionEnum)) {
            if (parentNodeId == null && (parentOutId == null || parentOutId.equals(0L) || parentOutIdType == null)) {
                return false;
            }
            if (parentOutIdType != null && OutIdTypeEnum.getEnum(parentOutIdType) == null) {
                return false;
            }
            //环境节点必须有环境标记
            if (NodeEnvFlagEnum.getEnum(envFlag) == null) {
                return false;
            }
            if (StringUtils.isBlank(nodeName)){
                return false;
            }
            if (outIdType != null && OutIdTypeEnum.getEnum(outIdType) == null) {
                return false;
            }
            if (NodeTypeEnum.getEnum(type) == null) {
                return false;
            }
        } else if (OperActionEnum.EDIT.equals(actionEnum)) {
            if (nodeId == null && (outId == null || outId.equals(0L) || outIdType == null)) {
                return false;
            }
            if (outIdType != null && OutIdTypeEnum.getEnum(outIdType) == null) {
                return false;
            }
            if (StringUtils.isBlank(nodeName)){
                return false;
            }
        } else if (OperActionEnum.DELETE.equals(actionEnum)) {
            if (nodeId == null && (outId == null || outId.equals(0L) || outIdType == null)) {
                return false;
            }
            if (outIdType != null && OutIdTypeEnum.getEnum(outIdType) == null) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }
}
