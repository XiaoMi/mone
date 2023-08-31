package com.xiaomi.mone.tpc.login.common.enums;

import lombok.ToString;

/**
 * 系统状态枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum SystemStatusEnum implements Base {
    ENABLE(0, "启用"),
    DISABLE(1, "停用"),
    ;
    private Integer code;
    private String desc;
    SystemStatusEnum(Integer mode, String desc) {
        this.code = mode;
        this.desc = desc;
    }

    public static final SystemStatusEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (SystemStatusEnum systemStatus : SystemStatusEnum.values()) {
            if (code.equals(systemStatus.code)) {
                return systemStatus;
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
