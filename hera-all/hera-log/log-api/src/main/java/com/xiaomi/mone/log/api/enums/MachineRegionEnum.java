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

    CN_MACHINE("cn", "大陆机房", Arrays.asList("c1", "c2", "c3"));

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
