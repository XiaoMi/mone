package com.xiaomi.mone.monitor.service.rocketmq.model;

import lombok.Data;

@Data
public class RocketMqReceiver<T> {
    private Integer code;

    private String message;

    private T data;
}
