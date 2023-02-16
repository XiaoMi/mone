package com.xiaomi.mone.log.api.model.meta;

public enum FilterType {
    GLOBAL("GLOBAL"),
    REGIONAL("REGIONAL");

    private String type;

    FilterType(String type) {
        this.type = type;
    }
}