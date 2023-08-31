package com.xiaomi.mone.tpc.common.enums;

import com.xiaomi.mone.tpc.common.param.*;
import lombok.ToString;

/**
 * 类型枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum RoleTypeEnum implements Base {
    NORMAL(0, "正常"),
    EXTENDS(1, "可继承"),
    ;
    private Integer code;
    private String desc;
    RoleTypeEnum(Integer mode, String desc) {
        this.code = mode;
        this.desc = desc;
    }

    public static final RoleTypeEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (RoleTypeEnum userTypeEnum : RoleTypeEnum.values()) {
            if (code.equals(userTypeEnum.code)) {
                return userTypeEnum;
            }
        }
        return null;
    }

    public static final RoleTypeEnum getEnumByString(String type) {
        if (type == null) {
            return null;
        }
        for (RoleTypeEnum userTypeEnum : RoleTypeEnum.values()) {
            if (type.equals(userTypeEnum.desc)) {
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

}
