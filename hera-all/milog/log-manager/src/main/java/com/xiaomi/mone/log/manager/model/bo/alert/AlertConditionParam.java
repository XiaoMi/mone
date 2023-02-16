package com.xiaomi.mone.log.manager.model.bo.alert;

import lombok.Data;

@Data
public class AlertConditionParam {

    private long alertRuleId;
    private int value;
    private String operation;
    private String alertLevel;
    private long period;
    private int order;
}
