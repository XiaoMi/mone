package com.xiaomi.mone.tpc.login.enums;

/**
 * 用户类型枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
public enum UserTypeEnum {
    CAS_TYPE(0, "CAS"),
    GITHUB_TYPE(1, "GITHUB"),
    EMAIL(2, "邮箱"),
    GITLAB_TYPE(3, "GITLAB"),
    GITEE_TYPE(4, "GITEE"),
    SERVICE_TYPE(5, "服务用户"),
    FEISHU_TYPE(6, "飞书"),
    DINGDING_TYPE(7, "钉钉"),
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

    public static final UserTypeEnum getEnum(String desc) {
        if (desc == null) {
            return null;
        }
        for (UserTypeEnum userTypeEnum : UserTypeEnum.values()) {
            if (desc.equals(userTypeEnum.desc)) {
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
