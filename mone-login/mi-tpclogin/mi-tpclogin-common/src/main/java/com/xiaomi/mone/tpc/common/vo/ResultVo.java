package com.xiaomi.mone.tpc.common.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/4 10:48
 */
@Data
@ToString
public class ResultVo<T> implements Serializable {
    private int code;
    private String message;
    private T data;

    public boolean success() {
        return ResponseCode.SUCCESS.getCode() == code;
    }
}
