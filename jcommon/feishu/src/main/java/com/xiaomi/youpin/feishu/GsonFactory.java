package com.xiaomi.youpin.feishu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.internal.bind.ObjectTypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author tsingfu
 */
public class GsonFactory {

    private GsonFactory() {}

    private static volatile Gson gson;

    public static Gson getGson() {
        if (null == gson) {
            synchronized (GsonFactory.class) {
                if (null == gson) {
                    Gson tmpGson = new GsonBuilder().create();
                    try {
                        Field factories = Gson.class.getDeclaredField("factories");
                        factories.setAccessible(true);
                        Object o = factories.get(tmpGson);
                        Class<?>[] declaredClasses = Collections.class.getDeclaredClasses();
                        for (Class c : declaredClasses) {
                            if ("java.util.Collections$UnmodifiableList".equals(c.getName())) {
                                Field listField = c.getDeclaredField("list");
                                listField.setAccessible(true);
                                List<TypeAdapterFactory> list = (List<TypeAdapterFactory>) listField.get(o);
                                int i = list.indexOf(ObjectTypeAdapter.FACTORY);
                                list.set(i, MapTypeAdapter.FACTORY);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        gson = tmpGson;
                    }
                }
            }
        }
        return gson;
    }

    public static class MapTypeAdapter extends TypeAdapter<Object> {
        public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
            @SuppressWarnings("unchecked")
            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
                if (type.getRawType() == Object.class) {
                    return (TypeAdapter<T>) new MapTypeAdapter(gson);
                }
                return null;
            }
        };

        private final Gson gson;

        private MapTypeAdapter(Gson gson) {
            this.gson = gson;
        }

        @Override
        public Object read(JsonReader in) throws IOException {
            JsonToken token = in.peek();
            //判断字符串的实际类型
            switch (token) {
                case BEGIN_ARRAY:
                    List<Object> list = new ArrayList<>();
                    in.beginArray();
                    while (in.hasNext()) {
                        list.add(read(in));
                    }
                    in.endArray();
                    return list;

                case BEGIN_OBJECT:
                    Map<String, Object> map = new LinkedTreeMap<>();
                    in.beginObject();
                    while (in.hasNext()) {
                        map.put(in.nextName(), read(in));
                    }
                    in.endObject();
                    return map;
                case STRING:
                    return in.nextString();
                case NUMBER:
                    String s = in.nextString();
                    if (s.contains(".")) {
                        return Double.valueOf(s);
                    } else {
                        try {
                            return Integer.valueOf(s);
                        } catch (Exception e) {
                            return Long.valueOf(s);
                        }
                    }
                case BOOLEAN:
                    return in.nextBoolean();
                case NULL:
                    in.nextNull();
                    return null;
                default:
                    throw new IllegalStateException();
            }
        }

        @Override
        public void write(JsonWriter out, Object value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            //noinspection unchecked
            TypeAdapter<Object> typeAdapter = (TypeAdapter<Object>) gson.getAdapter(value.getClass());
            if (typeAdapter instanceof ObjectTypeAdapter) {
                out.beginObject();
                out.endObject();
                return;
            }
            typeAdapter.write(out, value);
        }
    }

}
