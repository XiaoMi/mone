package com.xiaomi.mone.log.exception;

/**
 * @author milog
 */
public class CommonException extends RuntimeException {
    private int code;
    private String message;

    public CommonException(int code, String message) {
        this.code = code;
        this.message = message;
    }


    public CommonException(CommonError error) {
        this.code = error.getCode();
        this.message = error.getMessage();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
