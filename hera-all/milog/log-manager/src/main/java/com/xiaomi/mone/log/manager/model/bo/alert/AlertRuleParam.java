package com.xiaomi.mone.log.manager.model.bo.alert;

import com.xiaomi.mone.log.manager.model.alert.AlertRule;
import lombok.Data;

import java.util.List;

@Data
public class AlertRuleParam {

    private long alertId;
    private String name;
    private String regex;

    private List<AlertConditionParam> alertConditionList;
    private long alertRuleId;
    private String type;
}
