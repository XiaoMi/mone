package com.xiaomi.mone.tpc.login.common.param;

import com.xiaomi.mone.tpc.login.common.enums.AccountTypeEnum;
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
public class LoginFindParam extends BaseParam {

    private Integer type;

    @Override
    public boolean argCheck() {
        if (StringUtils.isBlank(getAccount())) {
            return false;
        }
        if (type == null || AccountTypeEnum.getEnum(type) == null) {
            return false;
        }
        return true;
    }
}
