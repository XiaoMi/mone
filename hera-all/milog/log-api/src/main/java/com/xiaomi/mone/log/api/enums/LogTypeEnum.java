/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.mone.log.api.enums;

import lombok.Getter;

/**
 * @author shanwb
 * @date 2021-07-08
 */
@Getter
public enum LogTypeEnum {
    APP_LOG_MULTI(1, "多行应用日志",
            "时间格式开始的日志可以多行采集，例如2022或者[2022]等，如果不是以时间开始,则会多行糅合在一起"),
    APP_LOG_SIGNAL(8, "单行应用日志", "单行采集，按文件的每一行采集成一列"),
    NGINX(2, "nginx日志", "单行采集，按文件的每一行采集成一列"),
    OPENTELEMETRY(3, "opentelemetry日志", "多行采集，采集到固定的topic中"),
    DOCKER(4, "docker日志", ""),
    MIS_APP_LOG(5, "MIS服务应用日志", "单行采集，按文件的每一行采集成一列"),
    LOKI_APP_LOG(6, "loki应用日志", ""),
    MATRIX_ES_LOG(7, "matrix es日志", ""),
    FREE(0, "自定义日志", "单行采集，按文件的每一行采集成一列");

    private final Integer type;
    private final String typeName;
    private final String describe;


    LogTypeEnum(Integer type, String typeName, String describe) {
        this.type = type;
        this.typeName = typeName;
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
        return "";
    }
}
