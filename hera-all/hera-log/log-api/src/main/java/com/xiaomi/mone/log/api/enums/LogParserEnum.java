package com.xiaomi.mone.log.api.enums;

import lombok.Getter;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/12/29 15:36
 */
@Getter
public enum LogParserEnum {

    SEPARATOR_PARSE(2, "分割符"),
    CUSTOM_PARSE(5, "自定义脚本");

    private final Integer code;
    private final String name;

    LogParserEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }


}
