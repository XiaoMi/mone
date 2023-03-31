package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.NodeStatusEnum;
import com.xiaomi.mone.tpc.common.enums.NodeTypeEnum;
import com.xiaomi.mone.tpc.common.enums.NodeUserRelTypeEnum;
import com.xiaomi.mone.tpc.common.enums.OutIdTypeEnum;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class NodeQryParam extends BaseParam implements Serializable {

    private Long id;
    private Long outId;
    private Integer outIdType;
    private Long parentId;
    private Long parentOutId;
    private Integer parentOutIdType;
    private Integer type;
    private String nodeName;
    private Integer status;
    private boolean myNode;
    private String orgId;
    private Integer relType;
    private boolean needParent = false;

    private String nodeCode;

    @Override
    public boolean argCheck() {
        if (type != null && NodeTypeEnum.getEnum(type) == null) {
            return false;
        }
        if (status != null && NodeStatusEnum.getEnum(status) == null) {
            return false;
        }
        if (outIdType != null && OutIdTypeEnum.getEnum(outIdType) == null) {
            return false;
        }
        if (parentOutIdType != null && OutIdTypeEnum.getEnum(parentOutIdType) == null) {
            return false;
        }
        if (myNode && relType != null && NodeUserRelTypeEnum.getEnum(relType) == null) {
            return false;
        }
        return true;
    }
}
