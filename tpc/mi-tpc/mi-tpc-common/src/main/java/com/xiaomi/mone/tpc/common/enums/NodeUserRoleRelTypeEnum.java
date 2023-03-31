package com.xiaomi.mone.tpc.common.enums;

import lombok.ToString;

/**
 * 节点用户角色关系类型枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum NodeUserRoleRelTypeEnum implements Base {
    USER(0, "用户"),
    GROUP(1, "组"),
    ;
    private Integer code;
    private String desc;
    NodeUserRoleRelTypeEnum(Integer mode, String desc) {
        this.code = mode;
        this.desc = desc;
    }

    public static final NodeUserRoleRelTypeEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (NodeUserRoleRelTypeEnum userTypeEnum : NodeUserRoleRelTypeEnum.values()) {
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
