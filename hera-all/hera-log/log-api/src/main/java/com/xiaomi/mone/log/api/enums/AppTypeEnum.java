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
