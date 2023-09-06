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
import org.apache.commons.lang3.StringUtils;

/**
 * @author shanwb
 * @date 2021-07-08
 */
@Getter
public enum LogTypeEnum {
    APP_LOG_MULTI(1, "Multi-line application log",
            1, 1,
            "Logs starting in time format can be collected in multiple lines, such as 2022 or [2022], etc., if they do not start with time, multiple lines will be grouped together"),
    NGINX(2, "nginx log", 3, 1, "Single-line acquisition, which is collected into a column by each line of the file"),
    OPENTELEMETRY(3, "opentelemetry log", 4, 0, "Multi-line acquisition, collected into a fixed topic"),
    DOCKER(4, "docker log", 5, 1, ""),
    APP_LOG_SIGNAL(8, "Single-line application log", 2, 1, "Single-line acquisition, which is collected into a column by each line of the file"),
    ORIGIN_LOG(9, "Raw format log", 6, 1, "Raw data is written to a fixed topic, and HERA is not consumed"),
    FREE(0, "Custom log", 7, 1, "Single-line acquisition, which is collected into a column by each line of the file");

    private final Integer type;
    private final String typeName;
    private final Integer sort;
    /**
     * Support self-production and self-consumption
     */
    private final Integer supportedConsume;
    private final String describe;


    LogTypeEnum(Integer type, String typeName, Integer sort, Integer supportedConsume, String describe) {
        this.type = type;
        this.typeName = typeName;
        this.sort = sort;
        this.supportedConsume = supportedConsume;
        this.describe = describe;
    }

    public Integer getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

    public static LogTypeEnum name2enum(String enumName) {
        for (LogTypeEnum ltEnum : LogTypeEnum.values()) {
            if (ltEnum.name().equals(enumName)) {
                return ltEnum;
            }
        }

        return null;
    }

    public static LogTypeEnum type2enum(Integer typeCode) {
        for (LogTypeEnum ltEnum : LogTypeEnum.values()) {
            if (ltEnum.getType().equals(typeCode)) {
                return ltEnum;
            }
        }
        return null;
    }

    public static String queryNameByType(Integer typeCode) {
        LogTypeEnum logTypeEnum = type2enum(typeCode);
        if (null != logTypeEnum) {
            return logTypeEnum.getTypeName();
        }
        return StringUtils.EMPTY;
    }
}
