package com.xiaomi.youpin.docean.mvc.httpmethod;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xiaomi.youpin.docean.mvc.HttpRequestMethod;
import com.xiaomi.youpin.docean.mvc.MvcContext;

/**
 * @author goodjava@qq.com
 * @date 2023/8/28 15:14
 */
public class HttpMethodUtils {

    public static void addMvcContext(HttpRequestMethod method, JsonArray array) {
        Class<?>[] types = method.getMethod().getParameterTypes();
        if (types.length > 0 && types[0] == MvcContext.class) {
            array.add(new JsonObject());
        }
    }


}
