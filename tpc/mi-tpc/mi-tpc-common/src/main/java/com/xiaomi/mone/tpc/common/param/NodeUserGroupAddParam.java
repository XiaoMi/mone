package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.NodeUserRelTypeEnum;
import com.xiaomi.mone.tpc.common.enums.OutIdTypeEnum;
import com.xiaomi.mone.tpc.common.enums.UserTypeEnum;
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
public class NodeUserGroupAddParam extends BaseParam implements Serializable {

    private Long nodeId;
    private Long outId;
    private Integer outIdType;
    private Long userGroupId;

    @Override
    public boolean argCheck() {
        if (nodeId == null && (outId == null || outIdType == null)) {
            return false;
        }
        if (outIdType != null && OutIdTypeEnum.getEnum(outIdType) == null) {
            return false;
        }
        if (userGroupId == null) {
            return false;
        }
        return true;
    }
}
