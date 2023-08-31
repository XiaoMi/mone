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

package com.xiaomi.youpin.docean.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.youpin.docean.bo.Bean;
import com.xiaomi.youpin.docean.common.ClassDeserializer;
import com.xiaomi.youpin.docean.common.ClassSerializer;
import com.xiaomi.youpin.docean.mvc.MvcContext;
import org.junit.Test;

public class GsonTest {


    @Test
    public void testJsonArray() {
        JsonObject obj = new JsonObject();
        obj.addProperty("traceId","123");
        MvcContext mc = new Gson().fromJson(obj,MvcContext.class);
        System.out.println(mc);
    }


    @Test
    public void testGson() {
        String str = "{\"id\":123}";
        JsonElement obj = new Gson().fromJson(str, JsonElement.class);

        System.out.println(obj.isJsonObject());
        System.out.println(obj.isJsonArray());

        if (obj.isJsonArray()) {
            JsonArray array = obj.getAsJsonArray();
            array.forEach(it->System.out.println(it));
        }

        if (obj.isJsonObject()) {
            JsonObject jobj = obj.getAsJsonObject();
            System.out.println(jobj.toString());
        }

    }


    @Test
    public void testClazz() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Class.class,new ClassDeserializer())
                .registerTypeAdapter(Class.class, new ClassSerializer()).create();
        Bean bean = new Bean();
        bean.setClazz(String.class);
        String str = gson.toJson(bean);
        System.out.println(str);

        Bean bean2 = gson.fromJson(str,Bean.class);
        System.out.println(bean2);
    }
}
