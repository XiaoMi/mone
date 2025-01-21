package run.mone.local.docean.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 给定字段定义，获取某个字段的类型
     * 例 给定的字段定义：[{"name":"a","valueType":"Object","children":[{"name":"a1","valueType":"String","children":[],"id":"075c"}],"id":"f543"}]
     */
    public static String getFieldType(String fieldDefinition, String fieldName) {
        if (fieldDefinition == null || fieldDefinition.isEmpty()) {
            throw new IllegalArgumentException("Invalid field definition");
        }

        JsonElement fieldDefinitionElement = JsonParser.parseString(fieldDefinition);
        if (!fieldDefinitionElement.isJsonArray()) {
            throw new IllegalArgumentException("Invalid field definition");
        }

        JsonArray fields = fieldDefinitionElement.getAsJsonArray();
        for (JsonElement field : fields) {
            if (field.isJsonObject()) {
                JsonObject fieldObject = field.getAsJsonObject();
                String name = fieldObject.get("name").getAsString();
                if (name.equals(fieldName)) {
                    return fieldObject.get("valueType").getAsString();
                } else if (fieldObject.has("children")) {
                    String childType = getFieldType(fieldObject.get("children").toString(), fieldName);
                    if (childType != null) {
                        return childType;
                    }
                }
            }
        }
        return null;
    }

    public static String getValue(JsonElement data) {
        if (null == data) {
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

    public static String fixJson(String json) {
        StringBuilder sb = new StringBuilder(json);
        int i = json.indexOf(":");
        i = json.indexOf("\"", i);
        int end = json.lastIndexOf("\"") - 1;
        List<Integer> list = new ArrayList<>();
        for (; i < end; ) {
            i = json.indexOf("\"", i + 1);
            if (i == -1 || i > end) {
                break;
            }
            if (!(json.charAt(i - 1) + "" + json.charAt(i)).equals("\\\"")) {
//                System.out.println(i);
//                System.out.println(sb.charAt(i - 2) + "" + sb.charAt(i - 1) + "" + sb.charAt(i));
                list.add(i);
            }
        }
        int k = 0;
        for (Integer v : list) {
//            System.out.println(sb.charAt(v - 1) + "" + sb.charAt(v));
            sb.insert(v + k, "\\");
            k += 1;
        }
        return sb.toString();
    }

}