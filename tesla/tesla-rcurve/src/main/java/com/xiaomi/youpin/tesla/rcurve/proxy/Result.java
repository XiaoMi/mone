package com.xiaomi.youpin.tesla.rcurve.proxy;

/**
 * @author goodjava@qq.com
 * @date 1/2/21
 */
public class Result<T> {

    private int code;

    private String message;

    private T data;


    public static <E> Result fail(int code, String message, E data) {
        Result result = new Result();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
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
}
