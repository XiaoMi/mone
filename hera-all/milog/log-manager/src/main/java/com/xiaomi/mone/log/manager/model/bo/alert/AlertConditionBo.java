package com.xiaomi.mone.log.manager.model.bo.alert;

import lombok.Data;

@Data
public class AlertConditionBo {

    private long conditionId;
    private String operation;
    private int value;
    private String alertLevel;
    private long period;
    private String creator;
    private long ctime;

}
