package com.xiaomi.mone.app.enums;

import lombok.Getter;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/5/25 10:54
 */
@Getter
public enum AppTypeEnum {

    BUSINESS_TYPE(0, "businessType"),
    HOST_TYPE(1, "hostType");
    private Integer code;
    private String message;

    AppTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static AppTypeEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        AppTypeEnum[] values = AppTypeEnum.values();
        for (AppTypeEnum value : values) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

}
