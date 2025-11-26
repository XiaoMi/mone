package com.xiaomi.youpin.docean.adapter;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/10/26 14:53
 */
public class IntegerDefaultAdapter implements JsonSerializer<Integer>, JsonDeserializer<Integer> {

    @Override
    public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if ("".equals(json.getAsString()) || "null".equals(json.getAsString())) {
            return null;
        }
        try {
            return json.getAsInt();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public JsonElement serialize(Integer src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src);
    }
}
