package com.xiaomi.mone.log.api.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/17 15:57
 */
@Getter
public enum MiddlewareEnum {

    ROCKETMQ(1, "rocketmq"),
    NCOS(3, "nacos"),
    LOKI(6, "loki"),
    ELASTICSEARCH(4, "elasticsearch"),
    HDFS(5, "hdfs"),
    ;

    private final Integer code;
    private final String name;

    MiddlewareEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static MiddlewareEnum queryByCode(Integer code) {
        if (null == code) {
            return null;
        }
        return Arrays.stream(MiddlewareEnum.values()).sequential().filter(middlewareEnum -> {
            if (middlewareEnum.getCode().intValue() == code.intValue()) {
                return true;
            }
            return false;
        }).findFirst().orElse(null);
    }

    public static String queryNameByCode(Integer code) {
        String name = "";
        for (MiddlewareEnum middlewareEnum : MiddlewareEnum.values()) {
            if (middlewareEnum.getCode().equals(code)) {
                name = middlewareEnum.getName();
            }
        }
        return name;
    }

    public static MiddlewareEnum queryByName(String name) {
        if (name == null || "".equals(name)) {
            return null;
        }
        return Arrays.stream(MiddlewareEnum.values()).sequential().filter(middlewareEnum -> {
            if (middlewareEnum.getName().equals(name)) {
                return true;
            }
            return false;
        }).findFirst().orElse(null);
    }
}
