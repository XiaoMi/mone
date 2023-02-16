package com.xiaomi.mone.monitor.bo;

import org.apache.commons.lang3.StringUtils;

/**
 * @author zhangxiaowei6
 * @date 2022/3/30
 */
public enum AppLanguage {
    java(0, "java"),
    go(1, "golang");
    private int code;
    private String message;

    AppLanguage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static final AppLanguage getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (AppLanguage userTypeEnum : AppLanguage.values()) {
            if (code.equals(userTypeEnum.code)) {
                return userTypeEnum;
            }
        }
        return null;
    }

    public static final Integer getCodeByMessage(String msg) {
        if (StringUtils.isBlank(msg)) {
            return null;
        }
        for (AppLanguage userTypeEnum : AppLanguage.values()) {
            if (msg.equals(userTypeEnum.getMessage())) {
                return userTypeEnum.getCode();
            }
        }
        return null;
    }
}
