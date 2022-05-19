package com.xiaomi.youpin.docean.common;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * @Author goodjava@qq.com
 * @Date 2021/6/3 13:58
 */
public class ClassDeserializer  implements JsonDeserializer<Class> {
    @Override
    public Class deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        if (obj.has("clazz")) {
            String str = obj.get("clazz").getAsString();
            try {
                return Class.forName(str);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
