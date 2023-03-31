package com.xiaomi.mone.tpc.common.param;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class RocketMqTopic implements Serializable  {
    private String topic;
    private String subExpr;
}
