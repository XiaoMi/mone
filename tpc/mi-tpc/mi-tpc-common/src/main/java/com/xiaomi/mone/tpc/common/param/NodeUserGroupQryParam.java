package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.NodeUserRelTypeEnum;
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
public class NodeUserGroupQryParam extends BaseParam implements Serializable {

    private Long nodeId;
    private Long userGroupId;
    @Override
    public boolean argCheck() {
        if (nodeId == null) {
            return false;
        }
        return true;
    }
}
