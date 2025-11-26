package com.xiaomi.mone.tpc.common.enums;

import lombok.ToString;

/**
 * 类型枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum OutIdTypeEnum implements Base {
    PROJECT(1, "项目"),
    FUNCTION(2, "函数"),
    FUNC_ENV(3, "函数环境"),
    GATE_GROUP(4, "分组"),
    MILINE(5, "miline发布线"),
    SPACE(6, "日志SPACE"),
    STORE(7, "日志STORE"),
    GATE_REGION(8, "区域"),
    HERA_BIZ_SCENE(13,"OzHera业务场景"),
    HERA_BIZ_METRIC(14,"OzHera业务指标"),
    HERA_BIZ_TEAM(15,"OzHera业务团队")
    ;
    private Integer code;
    private String desc;
    OutIdTypeEnum(Integer mode, String desc) {
        this.code = mode;
        this.desc = desc;
    }

    public static final OutIdTypeEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (OutIdTypeEnum userStatus : OutIdTypeEnum.values()) {
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
