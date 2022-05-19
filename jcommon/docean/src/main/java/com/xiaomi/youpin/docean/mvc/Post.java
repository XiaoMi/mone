package com.xiaomi.youpin.docean.mvc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author goodjava@qq.com
 */
public abstract class Post {


    public static JsonArray getParams(HttpRequestMethod method, JsonElement arguments) {
        JsonArray array = new JsonArray();
        Class<?>[] types = method.getMethod().getParameterTypes();
        if (types.length > 0 && types[0] == MvcContext.class) {
            array.add(new Gson().fromJson("{}", JsonObject.class));
        }

        if (null == arguments) {
            return array;
        }

        if (arguments.isJsonObject()) {
            array.add(arguments);
        }

        if (arguments.isJsonArray()) {
            arguments.getAsJsonArray().forEach(it -> {
                array.add(it);
            });
        }
        return array;
    }

}
