package com.xiaomi.mone.tpc.login.vo;

import java.io.Serializable;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/4 10:48
 */
public class ResultVo<T> implements Serializable {
    private int code;
    private String message;
    private T data;

    public boolean success() {
        return 0 == code;
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
        return "ResultVo{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
