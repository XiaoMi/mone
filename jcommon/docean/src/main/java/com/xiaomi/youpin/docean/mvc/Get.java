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
import com.xiaomi.youpin.docean.anno.RequestParam;
import com.xiaomi.youpin.docean.exception.DoceanException;
import com.xiaomi.youpin.docean.mvc.httpmethod.HttpMethodUtils;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
public abstract class Get {

    public static JsonArray getParams(HttpRequestMethod method, String uri) {
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        Map<String, String> params = decoder.parameters().entrySet().stream().collect(Collectors.toMap(it -> it.getKey(), it -> it.getValue().get(0)));
        JsonArray array = new JsonArray();
        HttpMethodUtils.addMvcContext(method, array);
        if (null == params) {
            return array;
        }
        Annotation[][] anns = method.getMethod().getParameterAnnotations();
        Arrays.stream(anns).forEach(it -> {
            if (it.length > 0) {
                RequestParam param = getRequestParam(it);
                String name = param.value();
                if (!params.containsKey(name)) {
                    throw new DoceanException("Missing parameter:" + name);
                }
                array.add(params.get(name));
            }
        });
        return array;
    }

    private static RequestParam getRequestParam(Annotation[] annos) {
        return (RequestParam) Arrays.stream(annos).filter(it -> it instanceof RequestParam).findFirst().get();
    }
}
