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
            //处理内置类型
            //解决easymock不支持基本类型返回的问题
            value = resolvePrimitive(type.getTypeName(), str);
        } else if (str.startsWith("{") || str.startsWith("[")) {
            //处理普通对象
            value = gson.fromJson(str, type1);
        } else {
            // TODO: 2020-02-08 走到这边代表出错了 考虑错误如何处理
            value = str;
        }

        return value;
    }

    private static Object resolvePrimitive(String paramType, String value) {
        switch (paramType) {
            case "java.lang.String":
                return value;
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
