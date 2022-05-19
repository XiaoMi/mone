package com.xiaomi.youpin.docean.mvc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.youpin.docean.anno.RequestParam;

import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * @author goodjava@qq.com
 */
public abstract class Get {


    public static JsonArray getParams(HttpRequestMethod method, JsonElement arguments) {

        JsonArray array = new JsonArray();
        Class<?>[] types = method.getMethod().getParameterTypes();
        if (types.length > 0 && types[0] == MvcContext.class) {
            array.add(new Gson().fromJson("{}", JsonObject.class));
        }


        Annotation[][] anns = method.getMethod().getParameterAnnotations();
        Arrays.stream(anns).forEach(it -> {
            if (it.length > 0) {
                RequestParam param = (RequestParam) (it[0]);
                String name = param.value();
                array.add(arguments.getAsJsonObject().get(name));
            }
        });
        return array;
    }
}
