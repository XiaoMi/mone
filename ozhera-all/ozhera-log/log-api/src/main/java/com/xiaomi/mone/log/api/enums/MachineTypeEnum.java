/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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

    CONTAINER_MACHINE(0, "Container"),
    PHYSICAL_MACHINE(1, "Physical Machines");

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
