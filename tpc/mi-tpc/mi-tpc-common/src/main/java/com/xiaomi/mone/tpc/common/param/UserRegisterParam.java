package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.UserStatusEnum;
import com.xiaomi.mone.tpc.common.enums.UserTypeEnum;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class UserRegisterParam extends BaseParam {

    @Override
    public boolean argCheck() {
        if (StringUtils.isBlank(getAccount())) {
            return false;
        }
        UserTypeEnum type = UserTypeEnum.getEnum(getUserType());
        if (type == null) {
            return false;
        }
        return true;
    }
}
