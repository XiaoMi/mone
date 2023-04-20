package com.xiaomi.mone.log.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/11/18 12:42
 */
@Getter
public enum MachineTypeEnum {

    CONTAINER_MACHINE(0, "容器"),
    PHYSICAL_MACHINE(1, "物理机");

    private final Integer type;
    private final String name;

    MachineTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public static MachineTypeEnum queryEnumByType(int type) {
        return Arrays.stream(MachineTypeEnum.values()).filter(machineTypeEnum -> {
            if (Objects.equals(machineTypeEnum.type, type)) {
                return true;
            }
            return false;
        }).findFirst().orElse(null);
    }

    public static String queryNameByType(int type) {
        MachineTypeEnum machineTypeEnum = queryEnumByType(type);
        if (null != machineTypeEnum) {
            return machineTypeEnum.getName();
        }
        return "";
    }
}
