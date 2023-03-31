package com.xiaomi.mone.tpc.common.enums;

import lombok.ToString;

/**
 * 用户状态枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum ProjectRoleType implements Base {
    Owner(0, "owner", NodeUserRelTypeEnum.MANAGER),
    Member(1, "member", NodeUserRelTypeEnum.MEMBER),
    Tester(2, "测试审核人员", null),
    Alarm_Receiver(4, "告警人员", null),
    ;
    private Integer code;
    private String desc;
    private NodeUserRelTypeEnum nodeUserRelType;
    ProjectRoleType(Integer mode, String desc, NodeUserRelTypeEnum nodeUserRelType) {
        this.code = mode;
        this.desc = desc;
        this.nodeUserRelType = nodeUserRelType;
    }

    public static final ProjectRoleType getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (ProjectRoleType roleType : ProjectRoleType.values()) {
            if (code.equals(roleType.code)) {
                return roleType;
            }
        }
        return null;
    }

    public NodeUserRelTypeEnum getNodeUserRelType() {
        return nodeUserRelType;
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
