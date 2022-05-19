package com.xiaomi.data.push.test;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class JsonTest {


    @Test
    public void testGson() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Exception.class,new TypeAdapter(){

            @Override
            public void write(JsonWriter out, Object value) throws IOException {
                Exception e = (Exception) value;
                System.out.println("write");
                out.beginObject();
                out.name("detailMessage").value(e.getMessage());
                out.endObject();
            }

            @Override
            public Object read(JsonReader in) throws IOException {
                return null;
            }
        }).create();


        Throwable t = new Exception("error");
        String str = gson.toJson(t);
        Object o = gson.fromJson(str,Throwable.class);
        System.out.println(o.getClass());
        System.out.println(o);
    }


    @Test
    public void testJson() {
//        JSONLexer lexer = new JSONLexer(new ANTLRInputStream("[{\"name\":{\"age\":$age},\"zzz\":$zzz},\"123\"]"));
//        JSONLexer lexer = new JSONLexer(new ANTLRInputStream("[{\"name\":{\"age\":$age},\"zzz\":$zzz},true]"));
        String str = "[{\"name\":{\"age\":$age},\"zzz\":$zzz},$num]";
        Map<String, String> m = Maps.newHashMap();
        m.put("$age", "23");
        m.put("$num", "44");
        m.put("$zzz", "gggg");
//        System.out.println(Json.json(str, m));
    }


    @Test
    public void testJson2() {
        Dog dog = new Dog();
        dog.setId(1);
        dog.setName("aaa");
        Gson gson = new Gson();
        JsonObject o = gson.fromJson(gson.toJson(dog),JsonObject.class);
        System.out.println(o.get("id").getAsInt());
        System.out.println(o.has("name"));
    }
}
