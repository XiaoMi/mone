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

package run.mone.m78.service.utils;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import lombok.extern.slf4j.Slf4j;
import run.mone.m78.api.enums.InputValueTypeEnum;
import run.mone.m78.service.common.GsonUtils;


@Slf4j
public class ValueTypeUtils {

    public static JsonElement convertValueByTypeToJsonElement(String value, InputValueTypeEnum type) {
        log.info("convertValueByTypeToJsonElement value:{} type:{}", value, type);
        switch (type) {
            case STRING:
                return new JsonPrimitive(value);
            case OBJECT:
                return JsonParser.parseString(value);
            case ARRAY_STRING:
                return GsonUtils.gson.fromJson(value, JsonArray.class);
            case ARRAY_OBJECT:
                return GsonUtils.gson.fromJson(value, JsonArray.class);
            case IMAGE:
                return new JsonPrimitive(value);
            case INTEGER:
                return new JsonPrimitive(Integer.parseInt(value));
            case BOOLEAN:
                return new JsonPrimitive(Boolean.parseBoolean(value));
            case ARRAY_INTEGER:
                return GsonUtils.gson.fromJson(value, JsonArray.class);
            case ARRAY_BOOLEAN:
                return GsonUtils.gson.fromJson(value, JsonArray.class);
            default:
                return new JsonPrimitive(value);
        }
    }

    public static Object convertValueByTypeToObject(String value, InputValueTypeEnum type) {
        log.info("convertValueByTypeToJsonElement value:{} type:{}", value, type);
        switch (type) {
            case STRING:
                return value;
            case OBJECT:
                return value;
            case ARRAY_STRING:
                return JSON.parseArray(value);
            case ARRAY_OBJECT:
                return JSON.parseArray(value);
//                return GsonUtils.gson.fromJson(value, JsonArray.class);
            case IMAGE:
                return value;
            case INTEGER:
                return Integer.parseInt(value);
            case BOOLEAN:
                return Boolean.parseBoolean(value);
            case ARRAY_INTEGER:
                return JSON.parseArray(value);
            case ARRAY_BOOLEAN:
                return JSON.parseArray(value);
            default:
                return value;
        }
    }

    public static Object convertValueByType(String value, InputValueTypeEnum type) {
        switch (type) {
            case STRING:
                return new JsonPrimitive(value);
            case OBJECT:
                return value;
            case ARRAY_STRING:
                return GsonUtils.gson.fromJson(value, String[].class);
            case ARRAY_OBJECT:
                return GsonUtils.gson.fromJson(value, Object[].class);
            case IMAGE:
                return new JsonPrimitive(value);
            case INTEGER:
                return Integer.parseInt(value);
            case BOOLEAN:
                return Boolean.parseBoolean(value);
            case ARRAY_INTEGER:
                return GsonUtils.gson.fromJson(value, Integer[].class);
            case ARRAY_BOOLEAN:
                return GsonUtils.gson.fromJson(value, Boolean[].class);
            default:
                return new JsonPrimitive(value);
        }
    }


}
