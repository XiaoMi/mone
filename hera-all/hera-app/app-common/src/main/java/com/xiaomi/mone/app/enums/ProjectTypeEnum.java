package com.xiaomi.mone.app.enums;

import lombok.Getter;

/**
 * @author wtt
 * @version 1.0
 * @description milog对应的应用类型
 * @date 2021/10/14 10:28
 */
@Getter
public enum ProjectTypeEnum {

    MIONE_TYPE(0, "mione", PlatFormTypeEnum.CHINA.getCode());

    private final Integer code;
    private final String type;
    private final Integer platFormTypeCode;

    ProjectTypeEnum(Integer code, String type, Integer platFormTypeCode) {
        this.code = code;
        this.type = type;
        this.platFormTypeCode = platFormTypeCode;
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
