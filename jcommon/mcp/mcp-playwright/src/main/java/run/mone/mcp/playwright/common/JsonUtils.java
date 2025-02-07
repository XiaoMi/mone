package run.mone.mcp.playwright.common;

import com.google.gson.JsonObject;

/**
 * @author goodjava@qq.com
 * @date 2025/2/7 14:38
 */
public class JsonUtils {

    //给定一个key 从 JsonObject(Gson) 中获取value 返回 String,如果没有则返回提供的默认值(class)
    public static String getValueOrDefault(JsonObject jsonObject, String key, String defaultValue) {
        return jsonObject.has(key) ? jsonObject.get(key).getAsString() : defaultValue;
    }

}
