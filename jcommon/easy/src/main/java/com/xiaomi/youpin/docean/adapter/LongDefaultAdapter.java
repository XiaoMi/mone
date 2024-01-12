package com.xiaomi.youpin.docean.adapter;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/10/26 14:53
 */
@Slf4j
public class LongDefaultAdapter implements JsonSerializer<Long>, JsonDeserializer<Long> {

    private Gson gson = new Gson();

    @Override
    public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.getAsString().equals("")) {
            return null;
        }
        try {
            return json.getAsLong();
        } catch (NumberFormatException e) {
            log.error("Long LongDefaultAdapter error,param:{}", gson.toJson(json), e);
            throw new JsonSyntaxException(e);
        }
    }

    @Override
    public JsonElement serialize(Long src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src);
    }
}
