package com.xiaomi.mone.tpc.common.enums;

import lombok.ToString;

/**
 * 类型枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum NodeChangeEnum {
    ADD(0, "添加"),
    DEL(1, "删除"),
    UPDATE(2, "更新"),
    ;
    private Integer code;
    private String desc;
    NodeChangeEnum(Integer mode, String desc) {
        this.code = mode;
        this.desc = desc;
    }

    public static final NodeChangeEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (NodeChangeEnum userTypeEnum : NodeChangeEnum.values()) {
            if (code.equals(userTypeEnum.code)) {
                return userTypeEnum;
            }
        }
        return null;
    }

    public static final NodeChangeEnum getEnumByString(String type) {
        if (type == null) {
            return null;
        }
        for (NodeChangeEnum userTypeEnum : NodeChangeEnum.values()) {
            if (type.equals(userTypeEnum.desc)) {
                return userTypeEnum;
            }
        }
        return null;
    }


    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
