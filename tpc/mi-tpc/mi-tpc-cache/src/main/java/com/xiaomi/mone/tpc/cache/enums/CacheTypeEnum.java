package com.xiaomi.mone.tpc.cache.enums;

import lombok.ToString;

/**
 * 类型枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum CacheTypeEnum {
    MEMORY(0, "内存"),
    REDIS(1, "REDIS"),
    ;
    private Integer code;
    private String desc;
    CacheTypeEnum(Integer mode, String desc) {
        this.code = mode;
        this.desc = desc;
    }

    public static final CacheTypeEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (CacheTypeEnum userTypeEnum : CacheTypeEnum.values()) {
            if (code.equals(userTypeEnum.code)) {
                return userTypeEnum;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
