package com.xiaomi.mone.app.common;

import com.xiaomi.mone.app.enums.CommonError;
import com.xiaomi.mone.app.exception.AppException;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/10/29 13:21
 */
@Data
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

    public static <T> Result<T> fail(CommonError error) {
        return new Result<>(error.getCode(), error.getMessage());
    }

    public boolean isSuccess(){
        return this.getCode() == CommonError.Success.getCode();
    }

    public static <T> Result<T> failParam(String errorMsg) {
        return new Result<>(CommonError.ParamsError.getCode(), errorMsg);
    }

    public static <T> Result<T> fail(AppException ex) {
        return new Result<>(ex.getCode(), ex.getMessage());
    }

    public static <T> Result<T> success(T t) {
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), t);
    }

    public static <T> Result<T> success() {
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage());
    }

    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<>(code, msg);
    }

    public static Result error(String msg) {
        return new Result<>(CommonError.SERVER_ERROR.getCode(), msg);
    }

    public static <T> Result error(Integer code, String msg, T data) {
        return new Result<>(code, msg, data);
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
