package com.xiaomi.mone.log.api.model.meta;

import lombok.Getter;

@Getter
public enum FilterName {
    RATELIMITER("RATELIMITER");

    private String name;

    FilterName(String name) {
        this.name = name;
    }
}