package com.xiaomi.youpin.gwdash.common;

import com.xiaomi.youpin.hermes.bo.Result;
import com.xiaomi.youpin.hermes.exception.CommonError;
import com.xiaomi.youpin.hermes.exception.CommonException;

import java.io.Serializable;

public class GroupInfoResult<T> implements Serializable {

    private int code;
    private String message;
    private T data;

    public GroupInfoResult() {

    }

    public GroupInfoResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public GroupInfoResult(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public GroupInfoResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> com.xiaomi.youpin.hermes.bo.Result<T> fail(CommonError error) {
        return new com.xiaomi.youpin.hermes.bo.Result<>(error.code, error.message);
    }

    public static <T> com.xiaomi.youpin.hermes.bo.Result<T> fail(CommonException ex) {
        return new com.xiaomi.youpin.hermes.bo.Result<>(ex.getCode(), ex.getMessage());
    }

    public static <T> com.xiaomi.youpin.hermes.bo.Result<T> success(T t) {
        return new Result<>(CommonError.Success.code, CommonError.Success.message, t);
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
