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
import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/10/18 15:41
 */
@Getter
public enum MachineRegionEnum {

    CN_MACHINE("cn", "Continental machine", Arrays.asList("c1", "c2", "c3"));

    private final String en;

    private final String cn;

    private List<String> childZone;

    MachineRegionEnum(String en, String cn, List<String> childZone) {
        this.en = en;
        this.cn = cn;
        this.childZone = childZone;
    }

    public static String queryMchineInfoByZone(String zone) {
        for (MachineRegionEnum machineRegionEnum : MachineRegionEnum.values()) {
            if (machineRegionEnum.getChildZone().contains(zone)) {
                return String.format("%s%s", machineRegionEnum.getEn(), machineRegionEnum.getCn());
            }
        }
        return "";
    }

    public static MachineRegionEnum queryMchineRegionByZone(String zone) {
        for (MachineRegionEnum machineRegionEnum : MachineRegionEnum.values()) {
            if (machineRegionEnum.getChildZone().contains(zone)) {
                return machineRegionEnum;
            }
        }
        return null;
    }

    public static String queryCnByEn(String en) {
        MachineRegionEnum machineRegionEnum = queryRegionByEn(en);
        if (null != machineRegionEnum) {
            return machineRegionEnum.getCn();
        }
        return "";
    }

    public static MachineRegionEnum queryRegionByEn(String en) {
        for (MachineRegionEnum machineRegionEnum : MachineRegionEnum.values()) {
            if (machineRegionEnum.getEn().equals(en)) {
                return machineRegionEnum;
            }
        }
        return null;
    }

}
