package com.xiaomi.mone.tpc.login.common.enums;

import lombok.ToString;

/**
 * 用户类型枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum UserTypeEnum implements Base {
    CAS_TYPE(0, "CAS"),
    GITHUB_TYPE(1, "GITHUB"),
    EMAIL(2, "邮箱"),
    GITLAB_TYPE(3, "GITLAB"),
    GITEE_TYPE(4, "GITEE"),
    SERVICE_TYPE(5, "服务账号"),
    ;
    private Integer code;
    private String desc;
    UserTypeEnum(Integer mode, String desc) {
        this.code = mode;
        this.desc = desc;
    }

    public static final UserTypeEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (UserTypeEnum userTypeEnum : UserTypeEnum.values()) {
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
