package com.xiaomi.mone.log.manager.model.bo.alert;

public enum AlertRuleUpdate {

    CREATE("create"),
    DELETE("delete"),
    UPDATE("update");

    private String type;

    private AlertRuleUpdate(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
