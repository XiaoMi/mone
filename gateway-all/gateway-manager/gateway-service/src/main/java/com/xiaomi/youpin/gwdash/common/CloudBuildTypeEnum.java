package com.xiaomi.youpin.gwdash.common;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author tsingfu
 */
public enum CloudBuildTypeEnum {
    JAR(1, "jar文件"),
    MIRROR(2, "dockerfile镜像");

    private int id;
    private String desc;

    public int getId () {return id;}

    public String getStatus() { return desc; }

    public static boolean isValidity(int type) {
        Optional<CloudBuildTypeEnum> optional = Arrays.stream(CloudBuildTypeEnum.values())
                .filter(it -> it.getId() == type)
                .findFirst();
        if (optional.isPresent()) {
            return true;
        }
        return false;
    }

    CloudBuildTypeEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }
}
