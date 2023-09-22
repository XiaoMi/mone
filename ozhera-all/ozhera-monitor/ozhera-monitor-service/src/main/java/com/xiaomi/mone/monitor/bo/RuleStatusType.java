package com.xiaomi.mone.monitor.bo;

/**
 * @author gaoxihui
 * @date 2021/12/7 1:03 下午
 */
public enum RuleStatusType {

    active(1,"生效"),
    pause(0,"暂停");

    private Integer code;
    private String message;

    RuleStatusType(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
