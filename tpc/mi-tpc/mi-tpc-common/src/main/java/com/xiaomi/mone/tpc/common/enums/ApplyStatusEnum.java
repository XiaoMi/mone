package com.xiaomi.mone.tpc.common.enums;

import lombok.ToString;

/**
 * 状态枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum ApplyStatusEnum implements Base {
    CLOSE(4, "已关闭"),
    FINSH(1, "已完成"),
    GOING(0, "进行中"),
    RECALL(3, "已撤回"),
    REJECT(2, "已驳回"),
    ;
    private Integer code;
    private String desc;

    ApplyStatusEnum(Integer mode, String desc) {
        this.code = mode;
        this.desc = desc;
    }

    public static final ApplyStatusEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (ApplyStatusEnum userStatus : ApplyStatusEnum.values()) {
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
