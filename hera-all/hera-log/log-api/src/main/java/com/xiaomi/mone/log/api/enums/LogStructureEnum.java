package com.xiaomi.mone.log.api.enums;

import lombok.Getter;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/6/13 14:43
 */
@Getter
public enum LogStructureEnum {

    SPACE("log-space"),

    STORE("log-store"),

    TAIL("log-tail");

    private String code;

    LogStructureEnum(String code) {
        this.code = code;
    }
}
