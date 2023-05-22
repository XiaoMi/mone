package com.xiaomi.youpin.prometheus.agent.result;

import com.xiaomi.youpin.prometheus.agent.enums.ErrorCode;

import java.io.Serializable;

public class Result<T> implements Serializable {

    private int code;
    private String message;
    private T data;

    public Result() {
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess(){
        return ErrorCode.success.getCode() == this.code ? true : false;
    }

    public static <T> Result<T> fail(ErrorCode error) {
        return new Result<>(error.getCode(), error.getMessage());
    }

    public static <T> Result<T> fail(ErrorCode error,T t) {
        return new Result<>(error.getCode(), error.getMessage(),t);
    }

    public static <T> Result<T> success(T t) {
        return new Result<>(ErrorCode.success.getCode(), ErrorCode.success.getMessage(), t);
    }

    public static <T> Result<T> success() {
        return new Result<>(ErrorCode.success.getCode(), ErrorCode.success.getMessage(), null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
