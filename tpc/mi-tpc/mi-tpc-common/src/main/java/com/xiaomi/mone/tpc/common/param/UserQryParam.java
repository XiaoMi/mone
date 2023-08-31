package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.UserStatusEnum;
import com.xiaomi.mone.tpc.common.enums.UserTypeEnum;
import lombok.Data;
import lombok.ToString;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class UserQryParam extends BaseParam {

    private Long id;
    private Integer type;
    private Integer status;
    private String userAcc;

    @Override
    public boolean argCheck() {
        if (type != null && UserTypeEnum.getEnum(type) == null) {
            return false;
        }
        if (status != null && UserStatusEnum.getEnum(status) == null) {
            return false;
        }
        return true;
    }
}
