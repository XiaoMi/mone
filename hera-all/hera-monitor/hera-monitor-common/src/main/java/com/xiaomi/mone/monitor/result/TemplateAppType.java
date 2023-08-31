package com.xiaomi.mone.monitor.result;
/**
 * @author zhangxiaowei6
 * @date 2022/3/30
 */
public enum TemplateAppType {
    businessType(0, "businessType"),
    hostType(1, "hostType");
    private int code;
    private String message;

    TemplateAppType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static final TemplateAppType getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (TemplateAppType userTypeEnum : TemplateAppType.values()) {
            if (code.equals(userTypeEnum.code)) {
                return userTypeEnum;
            }
        }
        return null;
    }
}
