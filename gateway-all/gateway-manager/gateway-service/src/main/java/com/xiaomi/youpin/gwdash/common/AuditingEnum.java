package com.xiaomi.youpin.gwdash.common;

import java.util.Arrays;
import java.util.Optional;

public enum AuditingEnum {
    ROLE(1, "role","用户角色申请类型"),
    APIGROUP(2, "api_group","api分组申请类型"),
    COMMIT(3,"commit","提交审核类型");

    private int id;
    private String type;
    private String desc;

    public int getId () {return id;}

    public String getType () { return  type; }

    public String getStatus() { return desc; }

    public static boolean isValidity(String type) {
        Optional<AuditingEnum> optional = Arrays.stream(AuditingEnum.values())
                .filter(it -> it.getType().equals(type))
                .findFirst();
        if (optional.isPresent()) {
            return true;
        }
        return false;
    }

    AuditingEnum(int id, String type, String desc) {
        this.id = id;
        this.type = type;
        this.desc = desc;
    }
}
