package com.xiaomi.youpin.prometheus.agent.enums;

public enum ErrorCode {
    success(0, "success"),
    unknownError(1, "unknown error"),

    // Parameter issue
    invalidParamError(1001, "无效的参数"),
    OperationFailed(1014,"操作失败"),
    INVALID_USER(4001,"用户身份无效"),
    NO_DATA_FOUND(4004,"数据未找到");

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
