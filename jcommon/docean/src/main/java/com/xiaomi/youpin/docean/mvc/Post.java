/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.docean.mvc;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.xiaomi.youpin.docean.anno.ModelAttribute;
import com.xiaomi.youpin.docean.mvc.httpmethod.HttpMethodUtils;
import com.xiaomi.youpin.docean.mvc.util.GsonUtils;
import com.xiaomi.youpin.docean.mvc.util.RequestUtils;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author goodjava@qq.com
 */
public abstract class Post {


    public static JsonArray getParams(HttpRequestMethod method, byte[] data, MvcContext context) {
        JsonArray arrayRes = new JsonArray();

        JsonElement arguments = (null == data || data.length == 0) ? null : GsonUtils.gson.fromJson(new String(data), JsonElement.class);
        context.setParams(arguments);

        Parameter[] methodParameters = method.getMethod().getParameters();
        boolean hasModelAttribute = Arrays.stream(methodParameters).filter(it -> it.getAnnotation(ModelAttribute.class) != null).findAny().isPresent();

        if (hasModelAttribute) {
            //没有传递任何参数
            if (null == arguments) {
                Arrays.stream(methodParameters).forEach(it -> {
                    if (it.getAnnotation(ModelAttribute.class) != null) {
                        arrayRes.add(RequestUtils.createSessionJsonObject(it.getAnnotation(ModelAttribute.class).value()));
                    }
                });
                return arrayRes;
            }
            if (arguments.isJsonArray()) {
                JsonArray array = arguments.getAsJsonArray();
                ArrayList<JsonElement> list = Lists.newArrayList(array.iterator());
                AtomicInteger i = new AtomicInteger(0);
                Arrays.stream(methodParameters).forEach(it -> {
                    if (it.getAnnotation(ModelAttribute.class) != null) {
                        arrayRes.add(RequestUtils.createSessionJsonObject(it.getAnnotation(ModelAttribute.class).value()));
                    } else {
                        arrayRes.add(list.get(i.get()));
                        i.incrementAndGet();
                    }
                });
                return arrayRes;
            }
            //只传递过来一个参数
            if (arguments.isJsonObject()) {
                Arrays.stream(methodParameters).forEach(it -> {
                    if (it.getAnnotation(ModelAttribute.class) != null) {
                        arrayRes.add(RequestUtils.createSessionJsonObject(it.getAnnotation(ModelAttribute.class).value()));
                    } else {
                        arrayRes.add(arguments.getAsJsonObject());
                    }
                });
                return arrayRes;
            }

            if (arguments.isJsonPrimitive()) {
                Arrays.stream(methodParameters).forEach(it -> {
                    if (it.getAnnotation(ModelAttribute.class) != null) {
                        arrayRes.add(RequestUtils.createSessionJsonObject(it.getAnnotation(ModelAttribute.class).value()));
                    } else {
                        arrayRes.add(arguments.getAsJsonPrimitive());
                    }
                });
                return arrayRes;
            }
        }

        HttpMethodUtils.addMvcContext(method, arrayRes);

        if (null == arguments) {
            context.setParams(arrayRes);
            return arrayRes;
        }

        if (arguments.isJsonObject()) {
            arrayRes.add(arguments);
        }

        if (arguments.isJsonArray()) {
            arguments.getAsJsonArray().forEach(it -> arrayRes.add(it));
        }

        if (arguments.isJsonPrimitive()) {
            arrayRes.add(arguments.getAsJsonPrimitive());
        }

        return arrayRes;

    }

    private static JsonObject obj(String name) {
        JsonObject obj = new JsonObject();
        obj.addProperty("__type__", "session");
        obj.addProperty("__name__", name);
        return obj;
    }



}
