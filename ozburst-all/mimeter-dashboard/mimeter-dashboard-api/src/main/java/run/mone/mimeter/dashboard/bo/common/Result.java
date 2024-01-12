package run.mone.mimeter.dashboard.bo.common;


import run.mone.mimeter.dashboard.exception.CommonError;

import java.io.Serializable;

/**
 * http json 返回结果
 */
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

    public static <T> Result<T> success(T t) {
        return new Result<>(0, "ok", t);
    }

    public static Result fail(int code, String msg) {
        return new Result(code, msg, null);
    }

    public static Result fail(CommonError error) {
        return new Result(error.code, error.message);
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
