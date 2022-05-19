package com.xiaomi.youpin.docean.plugin.test;

import com.google.common.collect.Lists;
import com.xiaomi.youpin.docean.plugin.json.Json;
import com.xiaomi.youpin.docean.plugin.json.antlr4.JsonArray;
import com.xiaomi.youpin.docean.plugin.json.antlr4.JsonObject;
import lombok.Data;
import org.junit.Test;

import java.util.List;

public class JsonTest {

    @Data
    class A {
        private int id;
        private String name;
        private int type;
        private B b;

        private List<B> list;

        public A(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }


    @Data
    class B {
        private int b;
    }


    @Test
    public void testJson() {
        Json json = new Json();
        A a = new A(1, "zzy");
        a.setType(22);
        B b = new B();
        b.setB(111111);
        a.setB(b);
        a.setList(Lists.newArrayList(b));
        System.out.println(json.toJson(a));
    }


    @Test
    public void testListJson() {
        Json json = new Json();
        B b = new B();
        b.setB(111111);
        System.out.println(json.toJson(Lists.newArrayList(b)));
    }


    @Test
    public void testFromJsonArray() {
        String json = "[1,2,3,4]";
//        String json = "[true,true,false,true]";
//        String json = "[{\"id\":1,\"name\":\"zzy\"}]";
        JsonArray jsonArray = JsonArray.parseArray(json);
        jsonArray.getList().stream().forEach(it -> {
            System.out.println(it.value());
        });

    }


    @Test
    public void testFromJsonObject() {
//        String json = "{\"id\":1,\"name\":\"zzy\"}";
        String json = "{\"id\":1,\"name\":\"zzy\",\"l\":[1,2,3]}";
//        String json = "{\"l\":[1,2,3]}";
        JsonObject obj = JsonObject.parseObject(json);
        System.out.println(obj.value());
    }


    @Test
    public void testFromJson() {
        String json = "[1,2,3,4]";
        new Json().fromJsonArray(json).forEach(it -> System.out.println(it));

        json = "{\"id\":1,\"name\":\"zzy\",\"l\":[1,2,3]}";
        System.out.println(new Json().fromJsonObject(json));
    }
}
