package com.xiaomi.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 */
@Data
public class JResult<T> implements Serializable {

    public JResult() {
    }

    public JResult(T data) {
        this.data = data;
    }

    private int code;

    private String message;

    private T data;


}
