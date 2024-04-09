package run.mone.local.docean.fsm;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wmin
 * @date 2024/3/14
 */
@Slf4j
public class JsonElementUtils {

    // 查询指定field的值
    public static JsonElement queryFieldValue(JsonElement data, String field) {
        log.info("queryFieldValue {}", field);
        String[] fieldPath = field.split("\\.");
        return queryFieldRecursive(data, fieldPath, 0);
    }

    // 递归查询指定field的值
    private static JsonElement queryFieldRecursive(JsonElement data, String[] fieldPath, int index) {
        if (index == fieldPath.length) {
            return data;
        }

        if (data.isJsonObject()) {
            JsonObject jsonObject = data.getAsJsonObject();
            String fieldName = fieldPath[index];
            JsonElement fieldValue = jsonObject.get(fieldName);
            if (fieldValue != null) {
                return queryFieldRecursive(fieldValue, fieldPath, index + 1);
            } else {
                throw new IllegalArgumentException("Field not found: " + fieldName);
            }
        } else if (data.isJsonArray()) {
            JsonArray jsonArray = data.getAsJsonArray();
            try {
                int arrayIndex = Integer.parseInt(fieldPath[index]);
                if (arrayIndex >= 0 && arrayIndex < jsonArray.size()) {
                    return queryFieldRecursive(jsonArray.get(arrayIndex), fieldPath, index + 1);
                } else {
                    throw new IndexOutOfBoundsException("Index out ofbounds: " + arrayIndex);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid arrayindex: " + fieldPath[index]);
            }
        } else if (data.isJsonPrimitive()) {
            return data;
        } else {
            throw new IllegalArgumentException("Invalid JSON format");
        }
    }

    public static String getValue(JsonElement data) {
        if (null == data){
            return null;
        }
        if (data.isJsonPrimitive()) {
            return data.getAsString();
        } else if (data.isJsonObject() || data.isJsonArray()) {
            return data.toString();
        } else {
            return null;
        }
    }

}