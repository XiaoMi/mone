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

package com.xiaomi.youpin.docean.plugin.json;

import com.xiaomi.youpin.docean.plugin.json.antlr4.JsonArray;
import com.xiaomi.youpin.docean.plugin.json.antlr4.JsonObject;
import net.sf.cglib.beans.BeanMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author goodjava@qq.com
 */
public class Json {


    public String toJson(Object obj) {
        if (obj instanceof Collection) {
            Collection coll = (Collection) obj;
            return "[" + coll.stream().map(it -> toJson(it)).collect(Collectors.joining(",")) + "]";
        }
        Map bm = (obj instanceof Map) ? (Map) obj : BeanMap.create(obj);
        String json = bm.keySet().stream().map(k -> key(k.toString()) + ":" + value(bm.get(k))).collect(Collectors.joining(",")).toString();
        return "{" + json + "}";
    }


    public List<Object> fromJsonArray(String jsonStr) {
        return JsonArray.parseArray(jsonStr).getList().stream().map(it -> it.value()).collect(Collectors.toList());
    }

    public Object fromJsonObject(String jsonStr) {
        return Optional.of(JsonObject.parseObject(jsonStr)).map(it -> it.value()).get();
    }



    private String key(String key) {
        return Stream.of("\"", key, "\"").collect(Collectors.joining());
    }


    private String value(Object value) {
        if (value instanceof Number) {
            return String.valueOf(value);
        }
        if (value instanceof String) {
            return Stream.of("\"", value.toString(), "\"").collect(Collectors.joining());
        }
        if (value instanceof Collection) {
            Collection coll = (Collection) value;
            return "[" + coll.stream().map(it -> toJson(it)).collect(Collectors.joining(",")) + "]";
        }
        return toJson(value);
    }

}
