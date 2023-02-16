package com.xiaomi.mone.log.api.enums;

import lombok.Getter;

@Getter
public enum DeployWayEnum {
    MIONE(1, "mione");

    private final Integer code;
    private final String name;

    DeployWayEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
