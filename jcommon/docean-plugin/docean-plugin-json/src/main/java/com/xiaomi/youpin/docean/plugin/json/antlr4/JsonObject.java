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

package com.xiaomi.youpin.docean.plugin.json.antlr4;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
public class JsonObject {

    private Map<String, Object> map;

    private Object val;

    private List<Object> list;


    public Object value() {
        if (null != map) {
            return map;
        }

        if (null != list) {
            return list.stream().map(it -> {
                if (val instanceof JsonObject) {
                    return ((JsonObject) val).val;
                }
                return it;
            }).collect(Collectors.toList());
        }

        if (val instanceof JsonObject) {
            return ((JsonObject) val).value();
        }
        return val;
    }


    public JsonObject() {
        this.map = new HashMap<>();
    }

    protected JsonObject(JSONParser.ObjContext objCtx) {
        this.map = new HashMap<>();
        for (JSONParser.PairContext pairCtx : objCtx.pair()) {
            String key = pairCtx.STRING().getText();
            map.put(key.substring(1, key.length() - 1), new JsonObject(pairCtx.value()).value());
        }
    }


    protected JsonObject(JSONParser.ValueContext valueContext) {
        if (null != valueContext.array()) {
            this.list = new ArrayList<>();
            valueContext.array().value().forEach(it -> {
                this.list.add(new JsonObject(it).value());
            });
            return;
        }

        if (valueContext.STRING() != null) {
            String v = valueContext.STRING().getText();
            this.val = v.substring(1, v.length() - 1);
            return;
        }

        if (valueContext.obj() == null) {
            this.val = valueContext.getText();
            return;
        }
        JSONParser.ObjContext objCtx = valueContext.obj();
        this.map = new HashMap<>();
        for (JSONParser.PairContext pairCtx : objCtx.pair()) {
            String key = pairCtx.STRING().getText();
            map.put(key.substring(1, key.length() - 1), new JsonObject(pairCtx.value()).value());
        }
    }


    public JsonObject getJSONObject(String key) {
        JSONParser.ValueContext value = (JSONParser.ValueContext) map.get(key);
        if (value == null) {
            return null;
        }
        return new JsonObject(value.obj());
    }

    public String getString(String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (JSONParser.ValueContext.class.isInstance(value)) {
            JSONParser.ValueContext ctx = (JSONParser.ValueContext) value;
            String newValue = ctx.STRING().getText();
            map.put(key, newValue.substring(1, newValue.length() - 1));
        }
        return (String) map.get(key);
    }

    public int getInt(String key) {
        String value = getString(key);
        if (value == null || "".equals(value)) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    public long getLong(String key) {
        String value = getString(key);
        if (value == null || "".equals(value)) {
            return 0L;
        }
        return Long.parseLong(value);
    }

    public double getDouble(String key) {
        String value = getString(key);
        if (value == null || "".equals(value)) {
            return 0.0;
        }
        return Double.parseDouble(value);
    }

    public JsonArray getJSONArray(String key) {
        JSONParser.ValueContext value = (JSONParser.ValueContext) map.get(key);
        if (value == null) {
            return null;
        }
        return new JsonArray(value.array());
    }

    public void put(String key, Object object) {
        map.put(key, object);
    }

    public static JsonObject parseObject(String text) {
        JSONLexer lexer = new JSONLexer(CharStreams.fromString(text));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JSONParser parser = new JSONParser(tokens);
        JSONParser.ObjContext objCtx = parser.obj();
        return new JsonObject(objCtx);
    }

    public static JsonArray parseArray(String text) {
        if (text == null) {
            return null;
        }
        JsonArray array = JsonArray.parseArray(text);
        return array;
    }

}
