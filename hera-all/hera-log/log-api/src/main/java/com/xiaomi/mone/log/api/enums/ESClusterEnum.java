package com.xiaomi.mone.log.api.enums;

import lombok.Getter;

@Getter
public enum ESClusterEnum {
    CN();
    private String dept;
    private String name;
    private String esClusterId;

    public static ESClusterEnum name2enum(String name) {
        for (ESClusterEnum ecEnum : ESClusterEnum.values()) {
            if (ecEnum.name.equals(name)) {
                return ecEnum;
            }
        }
        return null;
    }

    public static ESClusterEnum dept2enum(String dept) {
        for (ESClusterEnum ecEnum : ESClusterEnum.values()) {
            if (ecEnum.dept.equals(dept)) {
                return ecEnum;
            }
        }
        return null;
    }
}
