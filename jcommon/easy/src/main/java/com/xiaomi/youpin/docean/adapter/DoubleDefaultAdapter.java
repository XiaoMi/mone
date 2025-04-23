package com.xiaomi.youpin.docean.adapter;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/10/26 14:53
 */
public class DoubleDefaultAdapter implements JsonSerializer<Double>, JsonDeserializer<Double> {

    @Override
    public Double deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if ("".equals(json.getAsString())) {
            return null;
        }
        try {
            return json.getAsDouble();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src);
    }
}
