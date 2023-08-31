package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.NodeTypeEnum;
import com.xiaomi.mone.tpc.common.enums.RoleStatusEnum;
import com.xiaomi.mone.tpc.common.enums.RoleTypeEnum;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class RoleAddParam extends BaseParam {

    private Long systemId;
    private String roleName;
    private Integer status;
    private Integer type;
    private String desc;
    private Long nodeId;
    private List<Long> permissionIds;

    @Override
    public boolean argCheck() {
        if (systemId == null) {
            return false;
        }
        if (StringUtils.isBlank(roleName)){
            return false;
        }
        if (status != null && RoleStatusEnum.getEnum(status) == null) {
            return false;
        }
        if (type != null && RoleTypeEnum.getEnum(type) == null) {
            return false;
        }
        if (nodeId == null) {
            return false;
        }
        return true;
    }
}
