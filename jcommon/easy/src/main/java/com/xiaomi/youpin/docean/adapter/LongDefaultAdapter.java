package com.xiaomi.youpin.docean.adapter;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/10/26 14:53
 */
public class LongDefaultAdapter implements JsonSerializer<Long>, JsonDeserializer<Long> {

    @Override
    public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.getAsString().equals("")) {
            return null;
        }
        try {
            return json.getAsLong();
        } catch (NumberFormatException e) {
            throw new JsonSyntaxException(e);
        }
    }

    @Override
    public JsonElement serialize(Long src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src);
    }
}
