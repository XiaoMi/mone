package run.mone.m78.client.util;

import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.function.Supplier;

/**
 * @author HawickMason@xiaomi.com
 * @date 8/22/24 14:35
 */
public class GsonUtils {

    public static final Gson GSON = new Gson();


    public static boolean get(JsonObject obj, String key, boolean defaultValue) {
        if (obj.has(key)) {
            if(!(obj.get(key) instanceof JsonNull)) {
                return Boolean.valueOf(obj.get(key).getAsString());
            }
        }
        return defaultValue;
    }

    public static String get(JsonObject obj, String key, String defaultValue) {
        if (obj.has(key)) {
            if(!(obj.get(key) instanceof JsonNull)) {
                return obj.get(key).getAsString();
            }
        }
        return defaultValue;
    }

    public static String get(JsonObject obj, String key, Supplier<String> supplier) {
        if (obj.has(key)) {
            if(!(obj.get(key) instanceof JsonNull)) {
                return obj.get(key).getAsString();
            }
        }
        return supplier.get();
    }

}
