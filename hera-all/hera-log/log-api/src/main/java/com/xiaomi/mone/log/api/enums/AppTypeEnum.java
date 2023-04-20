package com.xiaomi.mone.log.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/16 11:35
 */
@Getter
public enum AppTypeEnum {

    LOG_MILOG(0, "milog"),
    LOG_AGENT(1, "milog-agent"),
    LOG_STREAM(2, "milog_stream_server_open"),
    LOG_MANAGER(3, "log-manager");

    private final Integer type;
    private final String name;

    AppTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public static AppTypeEnum queryEnumByType(int type) {
        return Arrays.stream(AppTypeEnum.values()).filter(machineTypeEnum -> {
            if (Objects.equals(machineTypeEnum.type, type)) {
                return true;
            }
            return false;
        }).findFirst().orElse(null);
    }

}
