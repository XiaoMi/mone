package com.xiaomi.mone.log.manager.model.bo.alert;

public enum AlertType {

    REGEX_COUNT("regexCount");

    private String type;

    private AlertType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
