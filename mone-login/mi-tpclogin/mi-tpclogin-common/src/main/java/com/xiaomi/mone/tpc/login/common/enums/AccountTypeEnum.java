package com.xiaomi.mone.tpc.login.common.enums;

import lombok.ToString;

/**
 * 类型枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum AccountTypeEnum implements Base {
    EMAIL(0, "邮箱账号", 2),
    ;
    private Integer code;
    private String desc;
    private Integer userType;
    AccountTypeEnum(Integer code, String desc, Integer userType) {
        this.code = code;
        this.desc = desc;
        this.userType = userType;
    }

    public static final AccountTypeEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (AccountTypeEnum userTypeEnum : AccountTypeEnum.values()) {
            if (code.equals(userTypeEnum.code)) {
                return userTypeEnum;
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

    public Integer getUserType() {
        return userType;
    }

}
