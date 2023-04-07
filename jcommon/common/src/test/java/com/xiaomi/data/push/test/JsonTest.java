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

package com.xiaomi.data.push.test;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.Data;
import org.junit.Assert;
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
        Map<String, String> m = Maps.newHashMap();
        m.put("$age", "23");
        m.put("$num", "44");
        m.put("$zzz", "gggg");
        Gson gson = new Gson();
        System.out.println(gson.toJson(m));
        Assert.assertNotNull(gson.toJson(m));
    }

    @Data
    public class Dog {

        private int id;

        private String name;

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
        Assert.assertTrue(o.has("name"));
    }
}
