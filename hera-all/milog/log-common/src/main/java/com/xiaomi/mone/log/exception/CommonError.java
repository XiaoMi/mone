package com.xiaomi.mone.log.exception;

/**
 * @author milog
 */

public enum CommonError {
    Success(0, "success"),

    UnknownError(1, "unknown error"),

    ParamsError(2, "parameter error"),

    NOT_EXISTS_DATA(3, "数据不存在"),

    SERVER_ERROR(4, "服务器异常"),

    UNAUTHORIZED(5, "没有操作权限");

    private int code;
    private String message;

    CommonError(int code, String message) {
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
