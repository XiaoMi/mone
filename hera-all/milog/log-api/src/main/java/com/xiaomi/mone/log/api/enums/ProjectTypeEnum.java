package com.xiaomi.mone.log.api.enums;

import lombok.Getter;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/10/14 10:28
 */
@Getter
public enum ProjectTypeEnum {

    MIONE_TYPE(0, "hera");
    private final Integer code;
    private final String type;

    ProjectTypeEnum(Integer code, String type) {
        this.code = code;
        this.type = type;
    }

    public static String queryTypeByCode(Integer code) {
        for (ProjectTypeEnum value : ProjectTypeEnum.values()) {
            if (value.getCode().equals(code)) {
                return value.getType();
            }
        }
        return "";
    }
}
