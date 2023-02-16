package com.xiaomi.mone.log.manager.model.bo.alert;

import lombok.Data;

import java.util.List;

@Data
public class AlertUpdateParam {

    private long alertId;
    private String alertName;
    private boolean updateContacts;
    private String contacts;
    private String feishuGroups;


    private List<AlertRuleParam> rules;

    public AlertParam toAlertParam() {
        AlertParam alertParam = new AlertParam();
        alertParam.setAlertId(this.alertId);
        alertParam.setName(this.alertName);
        alertParam.setType("talos");
        alertParam.setRules(this.rules);
        return alertParam;
    }


}
