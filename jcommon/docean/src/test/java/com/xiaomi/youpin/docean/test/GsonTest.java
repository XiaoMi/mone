package com.xiaomi.youpin.docean.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.youpin.docean.bo.Bean;
import com.xiaomi.youpin.docean.common.ClassDeserializer;
import com.xiaomi.youpin.docean.common.ClassSerializer;
import org.junit.Test;

public class GsonTest {


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
