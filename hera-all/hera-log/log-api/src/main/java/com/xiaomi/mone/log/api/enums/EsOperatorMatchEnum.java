package com.xiaomi.mone.log.api.enums;

import lombok.Getter;

@Getter
public enum EsOperatorMatchEnum {
    /**
     * 非 K:v匹配，不指定field
     */
    ALL_MATCH_OPERATOR,
    /**
     * 指定field字段
     */
    KV_MATCH_OPERATOR,
    ;
}
