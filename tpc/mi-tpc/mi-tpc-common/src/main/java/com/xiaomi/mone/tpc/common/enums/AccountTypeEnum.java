package com.xiaomi.mone.tpc.common.enums;

import lombok.ToString;

/**
 * 类型枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum AccountTypeEnum implements Base {
    EMAIL(0, "邮箱账号", UserTypeEnum.EMAIL),
    ;
    private Integer code;
    private String desc;
    private UserTypeEnum userType;
    AccountTypeEnum(Integer code, String desc, UserTypeEnum userType) {
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

    public UserTypeEnum getUserType() {
        return userType;
    }

}
