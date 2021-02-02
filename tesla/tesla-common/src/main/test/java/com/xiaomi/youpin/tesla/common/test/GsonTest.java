/*
 *  Copyright 2020 Xiaomi
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */


package com.xiaomi.youpin.tesla.common.test;

import com.google.gson.*;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GsonTest {


    @Test
    public void testGosn3() {
        Map<String, String> m = new HashMap<>();
        m.put("name", "<\"\">");
        String str = new Gson().toJson(m);
        System.out.println(str);
        System.out.println(new Gson().fromJson(str, Map.class));
    }

    @Test
    public void test1() {

        Map<String, Object> m = new HashMap<>();
        m.put("id", 1);
        m.put("class", "com.Bean");

        System.out.println("test1");


        System.out.println(new Gson().toJson(m));


        Gson gson = new GsonBuilder().addDeserializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {

                System.out.println(f.getName());

                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();

        System.out.println(gson.toJson(m));


        JsonSerializer<Map> mapJsonSerializer = new JsonSerializer<Map>() {
            @Override
            public JsonElement serialize(Map src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject obj = new JsonObject();

                Set<Map.Entry> s = src.entrySet();
                s.stream().forEach(it -> {
                });


                return obj;
            }
        };


    }
}
