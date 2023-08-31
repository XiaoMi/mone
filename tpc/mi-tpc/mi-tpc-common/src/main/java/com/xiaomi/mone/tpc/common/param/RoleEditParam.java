package com.xiaomi.mone.tpc.common.param;

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
public class RoleEditParam extends BaseParam {

    private Long id;
    private String roleName;
    private Integer status;
    private Integer type;
    private String desc;
    private List<Long> permissionIds;
    private Long nodeId;

    @Override
    public boolean argCheck() {
        if (id == null) {
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
