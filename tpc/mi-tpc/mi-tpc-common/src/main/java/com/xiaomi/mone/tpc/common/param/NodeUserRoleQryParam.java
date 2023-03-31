package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.NodeUserRoleRelTypeEnum;
import lombok.Data;
import lombok.ToString;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class NodeUserRoleQryParam extends BaseParam {

    private Long id;
    private Long systemId;
    private Long nodeId;
    private Long roleId;
    private Long memberId;
    private Integer type;

    @Override
    public boolean argCheck() {
        if (nodeId == null) {
            return false;
        }
        if(type != null && NodeUserRoleRelTypeEnum.getEnum(type) == null) {
            return false;
        }
        return true;
    }
}
