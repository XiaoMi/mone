package com.xiaomi.mone.app.exception;

import com.xiaomi.mone.app.enums.CommonError;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/10/29 13:20
 */
public class AppException extends RuntimeException {

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppException(Throwable cause) {
        super(cause);
    }

    public AppException(String message) {
        super(message);
    }

    public AppException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public AppException(CommonError error) {
        this.code = error.getCode();
        this.message = error.getMessage();
    }
}
