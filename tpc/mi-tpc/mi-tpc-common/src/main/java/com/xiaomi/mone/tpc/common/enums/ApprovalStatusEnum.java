package com.xiaomi.mone.tpc.common.enums;

import lombok.ToString;

/**
 * 状态枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum ApprovalStatusEnum implements Base {
    PASS(0, "通过"),
    REJECT(1, "不通过"),
    ;
    private Integer code;
    private String desc;
    ApprovalStatusEnum(Integer mode, String desc) {
        this.code = mode;
        this.desc = desc;
    }

    public static final ApprovalStatusEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (ApprovalStatusEnum userStatus : ApprovalStatusEnum.values()) {
            if (code.equals(userStatus.code)) {
                return userStatus;
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
