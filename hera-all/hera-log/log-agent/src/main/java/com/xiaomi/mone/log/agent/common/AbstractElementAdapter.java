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
package com.xiaomi.mone.log.agent.common;

import com.google.gson.*;
import com.xiaomi.mone.log.agent.output.RmqOutput;
import com.xiaomi.mone.log.agent.input.*;
import lombok.SneakyThrows;

import java.lang.reflect.Type;

/**
 * @author shanwb
 * @date 2021-08-02
 */
public class AbstractElementAdapter implements
        JsonSerializer<Object>, JsonDeserializer<Object> {

    private static final String KEY_TYPE = "type";

    @Override
    public Object deserialize(JsonElement jsonElement, Type type,
                              JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        JsonObject jsonObj = jsonElement.getAsJsonObject();
        String implType = jsonObj.get(KEY_TYPE).getAsString();

        Class<?> clz = type.getClass();
        switch (implType) {
            /**以下是log type**/
            case "APP_LOG":
            case "APP_LOG_MULTI":
            case "APP_LOG_SIGNAL":
                clz = AppLogInput.class;
                break;
            case "MIS_APP_LOG":
                clz = MisAppLogInput.class;
                break;
            case "NGINX":
                clz = NginxInput.class;
                break;
            case "OPENTELEMETRY":
                clz = OpentelemetryInput.class;
                break;
            case "DOCKER":
                //todo
                break;
            case "FREE":
                clz = FreeLogInput.class;
                break;
            case "ORIGIN_LOG":
                clz = OriginLogInput.class;
                break;
            /**以下是mq type**/
            case "rocketmq":
                clz = RmqOutput.class;
                break;
            case "talos":
                clz = getClassForName();
                break;
            default:
                break;
        }

        return jsonDeserializationContext.deserialize(jsonElement, clz);
    }

    public Class getClassForName() {
        try {
            return Class.forName("com.xiaomi.mone.log.agent.output.TalosOutput");
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public JsonElement serialize(Object object, Type type,
                                 JsonSerializationContext jsonSerializationContext) {
        JsonElement jsonEle = jsonSerializationContext.serialize(object, object.getClass());
        return jsonEle;
    }
}