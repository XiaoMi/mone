package com.xiaomi.mone.monitor.result;
/**
 * @author zhangxiaowei6
 * @date 2022/3/30
 */
public enum TemplateLanguage {
    java(0, "java"),
    go(1, "go");
    private int code;
    private String message;

    TemplateLanguage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static final TemplateLanguage getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (TemplateLanguage userTypeEnum : TemplateLanguage.values()) {
            if (code.equals(userTypeEnum.code)) {
                return userTypeEnum;
            }
        }
        return null;
    }
}
