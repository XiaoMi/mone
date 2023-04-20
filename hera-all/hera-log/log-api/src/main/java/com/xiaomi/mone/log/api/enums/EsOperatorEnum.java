package com.xiaomi.mone.log.api.enums;

import lombok.Getter;

@Getter
public enum EsOperatorEnum {
    AND_OPERATOR(" and "),
    OR_OPERATOR(" or "),
    NOT_OPERATOR(" not "),
    ;
    private final String code;

    EsOperatorEnum(String code) {
        this.code = code;
    }


}
