package run.mone.ultraman.common;

import com.google.gson.Gson;
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
            return Boolean.valueOf(obj.get(key).getAsString());
        }
        return defaultValue;
    }

    public static String get(JsonObject obj, String key, String defaultValue) {
        if (obj.has(key)) {
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
