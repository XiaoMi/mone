package run.mone.hive.common;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * @author goodjava@qq.com
 * @date 2024/12/29 14:31
 */
public class JsonUtils {

    public static final Gson gson = new Gson();

    /**
     * Extracts a value from a JsonElement using the given expression.
     *
     * @param jsonElement The JsonElement to extract the value from.
     * @param expression  The expression to navigate the JSON structure (e.g., "["friends"][0].name").
     * @return The extracted JsonElement, or null if not found.
     */
    public static JsonElement extractValue(JsonElement jsonElement, String expression) {
        String[] parts = expression.split("(?<=\\])|(?=\\[)|\\.");

        for (String part : parts) {
            if (jsonElement == null) {
                return null;
            }

            if (part.startsWith("[") && part.endsWith("]")) {
                // Array access
                String indexStr = part.substring(1, part.length() - 1);
                if (indexStr.startsWith("\"") && indexStr.endsWith("\"")) {
                    // Object key access
                    String key = indexStr.substring(1, indexStr.length() - 1);
                    jsonElement = jsonElement.getAsJsonObject().get(key);
                } else {
                    // Array index access
                    int index = Integer.parseInt(indexStr);
                    jsonElement = jsonElement.getAsJsonArray().get(index);
                }
            } else {
                // Object property access
                jsonElement = jsonElement.getAsJsonObject().get(part);
            }
        }

        return jsonElement;
    }


}