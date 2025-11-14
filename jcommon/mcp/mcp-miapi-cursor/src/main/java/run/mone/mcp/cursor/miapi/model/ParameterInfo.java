package run.mone.mcp.cursor.miapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import run.mone.mcp.cursor.miapi.util.TypeExtractorUtil;

import java.util.List;

/**
 * 参数信息
 */
public class ParameterInfo {
    
    /**
     * 参数名称
     */
    @JsonProperty("name")
    private String name;
    
    /**
     * 参数类型
     */
    @JsonProperty("type")
    private String type;

    @JsonProperty("classType")
    private Type classType;
    
    /**
     * 参数描述
     */
    @JsonProperty("description")
    private String description;
    
    /**
     * 是否必填
     */
    @JsonProperty("required")
    private boolean required;
    
    /**
     * 参数位置（query, path, body, header等）
     */
    @JsonProperty("position")
    private String position;
    
    /**
     * 默认值
     */
    @JsonProperty("defaultValue")
    private String defaultValue;
    
    /**
     * 泛型信息
     */
    @JsonProperty("genericType")
    private String genericType;
    /**
     * 泛型信息
     */
    @JsonProperty("childList")
    private List<ParameterInfo> childList;

    public ParameterInfo() {
    }

    public ParameterInfo(String name, Type type, String description, boolean required, String position, List<ParameterInfo> list) {
        this.name = name;
        this.classType = type;
        this.description = description;
        this.required = required;
        this.position = position;
        this.childList = list;
        this.type = type.asString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getGenericType() {
        return genericType;
    }

    public void setGenericType(String genericType) {
        this.genericType = genericType;
    }

    public List<ParameterInfo> getChildList() {
        return childList;
    }

    public void setChildList(List<ParameterInfo> childList) {
        this.childList = childList;
    }

    public Type getClassType() {
        return classType;
    }

    public void setClassType(Type classType) {
        if (TypeExtractorUtil.isInternalType(classType)) {
            this.type = TypeExtractorUtil.typeStr2TypeNo(TypeExtractorUtil.getPrimitiveSimpleName(classType));
        } else {
            this.type = TypeExtractorUtil.typeStr2TypeNo(classType.asString());
        }
        this.classType = classType;
    }

    @Override
    public String toString() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"name\": \"").append(escapeJson(name)).append("\",\n");
        json.append("  \"type\": \"").append(escapeJson(type)).append("\",\n");
        json.append("  \"description\": \"").append(escapeJson(description)).append("\",\n");
        json.append("  \"required\": ").append(required).append(",\n");
        json.append("  \"position\": \"").append(escapeJson(position)).append("\",\n");
        json.append("  \"defaultValue\": \"").append(escapeJson(defaultValue)).append("\",\n");
        json.append("  \"genericType\": \"").append(escapeJson(genericType)).append("\",\n");
        json.append("  \"childList\": ").append(childListToString()).append("\n");
        json.append("}");
        return json.toString();
    }
    private String childListToString() {
        if (childList == null) {
            return "null";
        }
        if (childList.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < childList.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(childList.get(i).toString());
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
