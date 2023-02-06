package com.xiaomi.mone.tpc.common.enums;

import lombok.ToString;

/**
 * 账号状态枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum AccountStatusEnum implements Base {
    ENABLE(0, "启用"),
    DISABLE(1, "停用"),
    ;
    private Integer code;
    private String desc;
    AccountStatusEnum(Integer mode, String desc) {
        this.code = mode;
        this.desc = desc;
    }

    public static final AccountStatusEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (AccountStatusEnum userStatus : AccountStatusEnum.values()) {
            if (code.equals(userStatus.code)) {
                return userStatus;
            }
        }
        return null;
    }


    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
