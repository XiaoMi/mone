package com.xiaomi.mone.log.manager.model.vo;

import lombok.Data;

@Data
public class RocketMQStatisCommand {
    private String topic;
    private Long begin;
    private Long end;
    private String aggreator;
    private String metric;
}
