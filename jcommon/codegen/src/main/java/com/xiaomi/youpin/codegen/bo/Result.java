package com.xiaomi.youpin.codegen.bo;


/**
 * @author goodjava@qq.com
 */
public class Result<T> {

    private int code;
    private String message;
    private T data;

    public Result() {
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
