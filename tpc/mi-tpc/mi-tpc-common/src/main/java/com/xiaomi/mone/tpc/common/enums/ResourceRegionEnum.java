package com.xiaomi.mone.tpc.common.enums;

import lombok.ToString;

/**
 * 状态枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum ResourceRegionEnum implements Base {
    CZONE(0, "中国区"),
    YOUPIN(1, "有品"),
    ;
    private Integer code;
    private String desc;

    ResourceRegionEnum(Integer mode, String desc) {
        this.code = mode;
        this.desc = desc;
    }

    public static final ResourceRegionEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (ResourceRegionEnum userStatus : ResourceRegionEnum.values()) {
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
