package run.mone.agentx.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.function.Supplier;

/**
 * @author goodjava@qq.com
 * @date 2023/12/10 16:00
 */
public class GsonUtils {

    public static Gson gson = new Gson();


    public static boolean get(JsonObject obj, String key, boolean defaultValue) {
        if (obj.has(key)) {
            if(!(obj.get(key) instanceof JsonNull)){
                return Boolean.valueOf(obj.get(key).getAsString());
            }
        }
        return defaultValue;
    }

    public static String get(JsonObject obj, String key, String defaultValue) {
        if (obj.has(key)) {
            if(!(obj.get(key) instanceof JsonNull)){
                return obj.get(key).getAsString();
            }
        }
        return defaultValue;
    }

    // 返回JsonArray
    public static JsonArray get(JsonObject obj, String key, JsonArray defaultValue) {
        if (obj.has(key)) {
            if(!(obj.get(key) instanceof JsonNull)) {
                return obj.get(key).getAsJsonArray();
            }
        }
        return defaultValue;
    }

    public static String getNoneNUll(JsonObject obj, String key, String defaultValue) {
        if (obj.has(key) && !obj.get(key).isJsonNull() && obj.get(key) != null && obj.get(key).getAsString() != null && !obj.get(key).getAsString().isBlank()) {
            return obj.get(key).getAsString();
        }
        return defaultValue;
    }

    public static String get(JsonObject obj, String key, Supplier<String> supplier) {
        if (obj.has(key)) {
            return obj.get(key).getAsString();
        }
        return supplier.get();
    }

}
