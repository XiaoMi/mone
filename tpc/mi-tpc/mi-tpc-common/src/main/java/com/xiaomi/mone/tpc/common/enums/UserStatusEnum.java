package com.xiaomi.mone.tpc.common.enums;

import lombok.ToString;

/**
 * 用户状态枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum UserStatusEnum implements Base {
    ENABLE(0, "启用"),
    DISABLE(1, "停用"),
    ;
    private Integer code;
    private String desc;
    UserStatusEnum(Integer mode, String desc) {
        this.code = mode;
        this.desc = desc;
    }

    public static final UserStatusEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (UserStatusEnum userStatus : UserStatusEnum.values()) {
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
