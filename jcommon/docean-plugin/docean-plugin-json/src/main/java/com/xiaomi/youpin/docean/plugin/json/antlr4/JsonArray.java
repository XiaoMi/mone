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

import lombok.Getter;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
public class JsonArray {

    @Getter
    private final List<JsonObject> list;

    public JsonArray() {
        this.list = new ArrayList<>();
    }


    public JsonArray(List<JsonObject> list) {
        this.list = new ArrayList<>(list.size());
        this.list.addAll(list);
    }

    protected JsonArray(JSONParser.ArrayContext arrayCtx) {
        this.list = arrayCtx.value()
                .stream()
                .map(valueContext -> new JsonObject(valueContext))
                .collect(Collectors.toList());
    }

    public static JsonArray parseArray(String text) {
        JSONLexer lexer = new JSONLexer(CharStreams.fromString(text));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JSONParser parser = new JSONParser(tokens);
        JSONParser.ArrayContext arrayCtx = parser.array();
        return new JsonArray(arrayCtx);
    }

    public JsonObject getJSONObject(int index) {
        return list.get(index);
    }

    public void add(JsonObject jsonObject) {
        list.add(jsonObject);
    }

    @Override
    public String toString() {
        return toJSONString();
    }

    public String toJSONString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        List<String> strList = list.stream().map(JsonObject::toString).collect(Collectors.toList());
        sb.append(String.join(",", strList));
        sb.append("]");
        return sb.toString();
    }
}
