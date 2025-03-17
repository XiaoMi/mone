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
public class AccountSyncParam extends BaseParam {

    private Integer type;
    private String password;
    private String name;
    private String sign;
    private long reqTime;



    @Override
    public boolean argCheck() {
        if (StringUtils.isBlank(getAccount())) {
            return false;
        }
        if (StringUtils.isBlank(password)) {
            return false;
        }
        if (StringUtils.isBlank(name)) {
            return false;
        }
        if (StringUtils.isBlank(sign)) {
            return false;
        }
        if (type == null || AccountTypeEnum.getEnum(type) == null) {
            return false;
        }
        //1分钟有效期
        long now = System.currentTimeMillis();
        if (reqTime + 1 * 60 * 1000l <= now) {
            return false;
        }
        return true;
    }
}
