package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.NodeEnvFlagEnum;
import com.xiaomi.mone.tpc.common.enums.NodeTypeEnum;
import com.xiaomi.mone.tpc.common.enums.OutIdTypeEnum;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class NodeAddParam extends BaseParam implements Serializable {

    private Long parentNodeId;
    private Long parentOutId;
    private Integer parentOutIdType;
    private Integer type;
    private String nodeName;
    private String desc;
    private Long mgrUserId;
    private OrgInfoParam orgParam;
    private Long outId;
    private Integer outIdType;
    private Integer envFlag;
    private boolean createDefEnv;
    private String code;

    @Override
    public boolean argCheck() {
        if (parentNodeId == null && (parentOutId == null || parentOutId.equals(0L) || parentOutIdType == null)) {
            return false;
        }
        if (parentOutIdType != null && OutIdTypeEnum.getEnum(parentOutIdType) == null) {
            return false;
        }
        NodeTypeEnum nodeTypeEnum = NodeTypeEnum.getEnum(type);
        if (nodeTypeEnum == null || NodeTypeEnum.TOP_TYPE.equals(nodeTypeEnum)) {
            return false;
        }
        if (NodeTypeEnum.PRO_GROUP_TYPE.equals(nodeTypeEnum) && StringUtils.isBlank(code)) {
            return false;
        }
        //环境节点必须有环境标记
        if (NodeTypeEnum.RES_GROUP_TYPE.equals(nodeTypeEnum) && NodeEnvFlagEnum.getEnum(envFlag) == null) {
            return false;
        }
        if (outIdType != null && OutIdTypeEnum.getEnum(outIdType) == null) {
            return false;
        }
        if (StringUtils.isBlank(nodeName)){
            return false;
        }
        if (orgParam != null && !orgParam.argCheck()) {
            return false;
        }
        return true;
    }
}
