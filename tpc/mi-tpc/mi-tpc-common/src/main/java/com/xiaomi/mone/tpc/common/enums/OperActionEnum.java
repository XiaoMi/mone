package com.xiaomi.mone.tpc.common.enums;

import lombok.ToString;

/**
 * 操作行为
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum OperActionEnum {
    ADD(0, "添加"),
    EDIT(1, "编辑"),
    DELETE(2, "删除"),
    ;
    private Integer code;
    private String desc;
    OperActionEnum(Integer mode, String desc) {
        this.code = mode;
        this.desc = desc;
    }

    public static final OperActionEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (OperActionEnum userTypeEnum : OperActionEnum.values()) {
            if (code.equals(userTypeEnum.code)) {
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
