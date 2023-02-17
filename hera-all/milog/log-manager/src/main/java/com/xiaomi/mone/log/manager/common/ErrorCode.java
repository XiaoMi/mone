package com.xiaomi.mone.log.manager.common;

public enum ErrorCode {
    success(0, "success"),
    unknownError(1, "unknown error"),
    CREATE_ALERT_FAILURE(2, "failed to create alert"),
    SUBMIT_FLINK_JOB(3, "failed to submit flink job"),
    ALERT_NOT_FOUND(4, "Alert not found"),
    ALERT_REMOVE_FAILED(5, "failed to remove alert"),
    FAIL_PARAM(6, "参数异常");

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
