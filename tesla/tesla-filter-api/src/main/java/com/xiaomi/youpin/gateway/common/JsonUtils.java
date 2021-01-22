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

package com.xiaomi.youpin.gateway.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.Map;
import java.util.Set;

/**
 * @author goodjava@qq.com
 */
public abstract class JsonUtils {


    public static String parser(String json, Set<String> filterKey) {
        JsonParser p = new JsonParser();
        JsonElement e = p.parse(json);
        StringBuilder sb = new StringBuilder();
        jsonTree("", e, sb, filterKey);
        return sb.toString();
    }


    private static final String mark = "\"";

    private static final String key(String key) {
        return mark + key + mark;
    }


    private static boolean jsonTree(String key, JsonElement e, StringBuilder sb, Set<String> filter) {
        if (e.isJsonNull()) {
            sb.append(key(key) + ":" + e.toString());
            return true;
        }

        if (e.isJsonPrimitive()) {
            //array
            if (key.equals(",")) {
                sb.append(e.toString());
            } else {
                if (!filter.contains(key)) {
                    sb.append(key(key) + ":" + e.toString());
                } else {
                    return false;
                }
            }
            return true;
        }

        if (e.isJsonArray()) {
            JsonArray ja = e.getAsJsonArray();
            if (null != ja) {
                if (!filter.contains(key)) {
                    sb.append(key(key) + ":[");
                    for (int i = 0; i < ja.size(); i++) {
                        JsonElement ae = ja.get(i);
                        jsonTree(",", ae, sb, filter);
                        if (i < ja.size() - 1) {
                            sb.append(",");
                        }
                    }
                    sb.append("]");
                }
            }
            return true;
        }

        if (e.isJsonObject()) {
            if (!key.equals("")) {
                if (!filter.contains(key)) {
                    sb.append(key(key) + ":{");
                } else {
                    return false;
                }
            } else {
                sb.append("{");
            }
            Set<Map.Entry<String, JsonElement>> es = e.getAsJsonObject().entrySet();

            int i = 0;
            int size = es.size();

            for (Map.Entry<String, JsonElement> en : es) {
                boolean res = jsonTree(en.getKey(), en.getValue(), sb, filter);
                if (i < size - 1 && res) {
                    //不是逗号就添加
                    if (!(sb.substring(sb.length() - 1, sb.length()).equals(","))) {
                        sb.append(",");
                    }
                }
                i++;
            }

            while (true) {
                if (sb.substring(sb.length() - 1, sb.length()).equals(",")) {
                    sb.deleteCharAt(sb.length() - 1);
                } else {
                    break;
                }
            }

            sb.append("}");
        }
        return true;
    }
}
