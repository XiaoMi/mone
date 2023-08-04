package com.xiaomi.youpin.docean.plugin.es.antlr4.common.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/8/3 17:16
 */
public enum ValueTypeEnum {
    IPV4("ipv4", "IPV4"),
    IPV6("ipv6", "IPV6"),
    STRING("string", "STRING"),
    NUMBER("number", "NUMBER"),
    TIME("time", "TIME"),
    TRUE("true", "TRUE"),
    FALSE("false", "FALSE"),
    NULL("null", "NULL"),
    ARRAY("array", "ARRAY"),
    EQUAL(":", "equal"),
    REGEX("regex", "REGEX");

    @Getter
    private String key;
    @Getter
    private String value;

    ValueTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    ValueTypeEnum getByKey(String key) {
        for (ValueTypeEnum e : values()) {
            if (Objects.equals(key, e.getKey())) {
                return e;
            }
        }
        return null;
    }

    ValueTypeEnum getByValue(String value) {
        for (ValueTypeEnum e : values()) {
            if (Objects.equals(value, e.getValue())) {
                return e;
            }
        }
        return null;
    }
}
