package com.xiaomi.mone.tpc.common.enums;

import lombok.ToString;

/**
 * 类型枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum RocketMqOffsetTypeEnum implements Base {
    CONSUME_FROM_LAST_OFFSET(0, "CONSUME_FROM_LAST_OFFSET"),
    CONSUME_FROM_FIRST_OFFSET(4, "CONSUME_FROM_FIRST_OFFSET"),
    CONSUME_FROM_TIMESTAMP(5, "CONSUME_FROM_TIMESTAMP"),
    ;
    private Integer code;
    private String desc;
    RocketMqOffsetTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static final RocketMqOffsetTypeEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (RocketMqOffsetTypeEnum userTypeEnum : RocketMqOffsetTypeEnum.values()) {
            if (code.equals(userTypeEnum.code)) {
                return userTypeEnum;
            }
        }
        return null;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

}
