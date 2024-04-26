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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.youpin.docean.anno.ModelAttribute;
import com.xiaomi.youpin.docean.anno.RequestParam;
import com.xiaomi.youpin.docean.mvc.util.RequestUtils;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
public abstract class Get {

    public static JsonElement getParams(HttpRequestMethod method, String uri, MvcContext context) {
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        Map<String, String> params = decoder.parameters().entrySet().stream().collect(Collectors.toMap(it -> it.getKey(), it -> it.getValue().get(0)));
        JsonObject res = new JsonObject();
        params.entrySet().forEach(it -> res.addProperty(it.getKey(), it.getValue()));
        context.setParams(res);
        JsonArray resArray = new JsonArray();

        //get 只允许第一个参数是session user
        Parameter[] methodParameters = method.getMethod().getParameters();
        if (methodParameters.length > 0 && (methodParameters[0].getAnnotation(ModelAttribute.class) != null)) {
            resArray.add(RequestUtils.createSessionJsonObject(methodParameters[0].getAnnotation(ModelAttribute.class).value()));
        }

        Annotation[][] anns = method.getMethod().getParameterAnnotations();
        Arrays.stream(anns).forEach(it -> {
            if (it.length > 0) {
                RequestParam param = getRequestParam(it);
                String name = param.value();
                if (!params.containsKey(name)) {
                    resArray.add("");
                } else {
                    resArray.add(params.get(name));
                }
            }
        });

        return resArray;
    }

    private static RequestParam getRequestParam(Annotation[] annos) {
        return (RequestParam) Arrays.stream(annos).filter(it -> it instanceof RequestParam).findFirst().get();
    }
}
