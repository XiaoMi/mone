package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.NodeTypeEnum;
import com.xiaomi.mone.tpc.common.enums.RoleStatusEnum;
import lombok.Data;
import lombok.ToString;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class RoleQryParam extends BaseParam {

    private Long id;
    private Long systemId;
    private String roleName;
    private Integer status;
    private Integer nodeId;

    @Override
    public boolean argCheck() {
        if (status != null && RoleStatusEnum.getEnum(status) == null) {
            return false;
        }
        return true;
    }
}
