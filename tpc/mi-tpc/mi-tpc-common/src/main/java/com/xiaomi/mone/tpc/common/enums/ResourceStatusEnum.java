package com.xiaomi.mone.tpc.common.enums;

import lombok.ToString;

/**
 * 状态枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum ResourceStatusEnum implements Base {
    ENABLE(0, "启用"),
    DISABLE(1, "停用"),
    ;
    private Integer code;
    private String desc;
    ResourceStatusEnum(Integer mode, String desc) {
        this.code = mode;
        this.desc = desc;
    }

    public static final ResourceStatusEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (ResourceStatusEnum userStatus : ResourceStatusEnum.values()) {
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
