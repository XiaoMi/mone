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
