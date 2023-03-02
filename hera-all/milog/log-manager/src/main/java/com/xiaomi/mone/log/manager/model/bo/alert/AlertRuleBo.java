package com.xiaomi.mone.log.manager.model.bo.alert;

import lombok.Data;

import java.util.List;

@Data
public class AlertRuleBo {

    private long alertRuleId;
    private String name;
    private String regex;
    private long ctime;
    private String creator;

    private List<AlertConditionBo> alertConditions;

}
