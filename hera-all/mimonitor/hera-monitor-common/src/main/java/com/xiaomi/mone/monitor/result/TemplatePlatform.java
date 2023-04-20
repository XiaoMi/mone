package com.xiaomi.mone.monitor.result;
/**
 * @author zhangxiaowei6
 * @date 2022/3/30
 */
public enum TemplatePlatform {
    mione(0, "mione"),
    cloud(1, "cloud");
    private int code;
    private String message;

    TemplatePlatform(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static final TemplatePlatform getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (TemplatePlatform userTypeEnum : TemplatePlatform.values()) {
            if (code.equals(userTypeEnum.code)) {
                return userTypeEnum;
            }
        }
        return null;
    }
}
