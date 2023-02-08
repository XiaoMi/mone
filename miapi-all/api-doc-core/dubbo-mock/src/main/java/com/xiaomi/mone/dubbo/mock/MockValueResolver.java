/*
 * Copyright 2020 XiaoMi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the following link.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaomi.mone.dubbo.mock;

import com.google.gson.Gson;
import com.xiaomi.mone.dubbo.mock.util.ClassHelper;

import java.lang.reflect.Type;

public class MockValueResolver {

    public static Gson gson = new Gson();

    public static Object resolve(String str, Type[] types) {
        Type type = types[0];
        Type type1 = types.length > 1 ? types[1] : types[0];
        if ("java.lang.Void".equals(type.getTypeName())) {
            return null;
        }

        Object value = null;
        if (ClassHelper.isPrimitive((Class<?>) type)) {
            //deal with inner type
            //deal with the problem that easymock do not support res of basic type
            value = resolvePrimitive(type.getTypeName(), str);
        } else if (str.startsWith("{") || str.startsWith("[")) {
            //deal with normal obj
            value = gson.fromJson(str, type1);
        } else {
            value = str;
        }

        return value;
    }

    private static Object resolvePrimitive(String paramType, String value) {
        switch (paramType) {
            case "byte":
            case "java.lang.Byte":
                return Byte.parseByte(value);
            case "short":
                return Short.parseShort(value);
            case "int":
            case "java.lang.Integer":
                return Integer.parseInt(value);
            case "long":
            case "java.lang.Long":
                return Long.parseLong(value);
            case "float":
            case "java.lang.Float":
                return Float.parseFloat(value);
            case "double":
            case "java.lang.Double":
                return Double.parseDouble(value);
            case "boolean":
            case "java.lang.Boolean":
                return Boolean.parseBoolean(value);
            default:
                return value;
        }
    }

}
