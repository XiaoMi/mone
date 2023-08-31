package com.xiaomi.mone.tpc.common.enums;

import lombok.ToString;

/**
 * 状态枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum NodeEnvFlagEnum implements Base {

    DEV(0, "dev"),
    ST(2, "staging"),
    PRE(3, "pre"),
    ONLINE(4, "online"),
    ;
    private Integer code;
    private String desc;

    NodeEnvFlagEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static final NodeEnvFlagEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (NodeEnvFlagEnum envFlagEnum : NodeEnvFlagEnum.values()) {
            if (code.equals(envFlagEnum.code)) {
                return envFlagEnum;
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
