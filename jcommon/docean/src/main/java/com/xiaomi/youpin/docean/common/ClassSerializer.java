package com.xiaomi.youpin.docean.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * @Author goodjava@qq.com
 * @Date 2021/6/3 13:54
 */
public class ClassSerializer implements JsonSerializer<Class> {
    @Override
    public JsonElement serialize(Class src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("clazz",src.getName());
        return object;
    }
}
