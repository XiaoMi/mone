package com.xiaomi.mone.monitor.service.user;

import lombok.Data;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/6 15:43
 */
@Data
public class IdmResponse<T> {
    private Integer code;
    private String msg;
    private T data;
}
