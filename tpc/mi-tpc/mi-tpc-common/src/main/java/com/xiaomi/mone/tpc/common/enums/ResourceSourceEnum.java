package com.xiaomi.mone.tpc.common.enums;

import lombok.ToString;

/**
 * 枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum ResourceSourceEnum implements Base {
    CLOSE(0, "中国区资源"),
    FINSH(1, "自有资源"),
    ;
    private Integer code;
    private String desc;

    ResourceSourceEnum(Integer mode, String desc) {
        this.code = mode;
        this.desc = desc;
    }

    public static final ResourceSourceEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (ResourceSourceEnum userStatus : ResourceSourceEnum.values()) {
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
