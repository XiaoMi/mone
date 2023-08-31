package com.xiaomi.mone.tpc.common.enums;

import lombok.ToString;

/**
 * 标签类型枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum FlagTypeEnum implements Base {
    NODE(0, "节点标签"),
    ORG(1, "组织信息"),
    FULL_ORG(2, "全组织信息"),
    IAM(3, "IAM_ID"),
    SYS_MGR(4, "系统管理员"),
    META_DATA(5, "原信息存储"),
    ;
    private Integer code;
    private String desc;
    FlagTypeEnum(Integer mode, String desc) {
        this.code = mode;
        this.desc = desc;
    }

    public static final FlagTypeEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (FlagTypeEnum userTypeEnum : FlagTypeEnum.values()) {
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
