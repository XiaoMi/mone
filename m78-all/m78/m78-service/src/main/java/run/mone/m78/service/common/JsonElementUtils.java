package run.mone.m78.service.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.m78.api.bo.flow.NodeInfo;
import run.mone.m78.api.bo.flow.NodeOutputInfo;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @author wmin
 * @date 2024/3/14
 */
@Slf4j
public class JsonElementUtils {

    /**
     * 给定字段定义，获取某个字段(如果是多层级的子字段，用.分割)的类型，即获取对应的valueType
     * 下面是例子 给定的字段定义：[{"name":"a","valueType":"Object","children":[{"name":"a1","valueType":"String","children":[],"id":"0fe"}],"id":"f543"}]
     * 获取a.a1字段的类型
     * 获取a字段的类型
     * <p>
     * 如果是数组类型，获取其子字段时，后面会跟一个0间隔，比如下面的例子
     * [{"name":"a","valueType":"Array<Object>","children":[{"name":"a1","valueType":"String","children":[],"id":"c4c1977e-818a-40ac-bfb8-227ba0ee753e"}],"id":"0_outputList_0_a"}]
     * 获取a.0.a1字段的类型
     */
    public static String getFieldType(String jsonFields, String fieldPath) {
        JsonArray fieldsArray = JsonParser.parseString(jsonFields).getAsJsonArray();
        if (fieldPath.startsWith("0.")) {
            fieldPath = fieldPath.substring(2);
            ;
        }
        String[] pathParts = fieldPath.split("\\.");

        return getFieldTypeRecursive(fieldsArray, pathParts, 0);
    }

    private static String getFieldTypeRecursive(JsonArray fieldsArray, String[] pathParts, int index) {
        for (JsonElement fieldElement : fieldsArray) {
            JsonObject fieldObject = fieldElement.getAsJsonObject();
            String currentName = fieldObject.get("name").getAsString();

            if (pathParts[index].equals(currentName)) {
                if (index == pathParts.length - 1) {
                    // 如果路径只有一层，直接返回类型
                    return fieldObject.get("valueType").getAsString();
                } else {
                    // 如果路径有多层，递归查找子字段
                    JsonArray children = fieldObject.getAsJsonArray("children");
                    if (children != null) {
                        boolean isArray = pathParts[index + 1].matches("\\d+") && "Array".equals(fieldObject.get("valueType").getAsString().split("<")[0]);
                        String childFieldType = getFieldTypeRecursive(children, pathParts, isArray ? index + 2 : index + 1);
                        if (childFieldType != null) {
                            return childFieldType;
                        }
                    }
                }
            }
        }
        return null; // 如果没有找到字段，返回null
    }


    /**
     *
     * @param referenceNode
     * @param referenceName
     * @return
     */
    public static String findValueTypeFromNodeInfo(NodeInfo referenceNode, String referenceName) {
        if (referenceNode != null) {
            // 获取referenceName对应的类型
            if (StringUtils.isNotBlank(referenceName)) {
                String valueType = "";
                if (referenceName.contains(".")) {
                    String schema = referenceNode.getOutputs().stream().filter(i -> i.getName().equals(referenceName.split("\\.")[0])).collect(Collectors.toList()).get(0).getSchema();
                    valueType = JsonElementUtils.getFieldType(schema, referenceName.substring(referenceName.indexOf(".") + 1));
                } else {
                    valueType = referenceNode.getOutputs().stream().filter(i -> i.getName().equals(referenceName)).collect(Collectors.toList()).get(0).getValueType();
                }
                // 重置valueType
                return valueType;
            }
        }
        return null;
    }


}