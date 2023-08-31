package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.NodeUserRoleRelTypeEnum;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class NodeUserRoleAddParam extends BaseParam {

    private Long nodeId;
    private Long memberId;
    private Integer type;
    private List<Long> roleIds;

    @Override
    public boolean argCheck() {
        if (nodeId == null) {
            return false;
        }
        if (roleIds == null || roleIds.isEmpty()) {
            return false;
        }
        if (memberId == null) {
            return false;
        }
        if(type == null || NodeUserRoleRelTypeEnum.getEnum(type) == null) {
            return false;
        }
        return true;
    }
}
