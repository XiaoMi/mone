package run.mone.local.docean.enums;


/**
 * @author wmin
 * @date 2024/2/29
 */
public enum FlowValueTypeEnum {

    STRING("String"),
    OBJECT("Object"),
    ARRAY_STRING("Array<String>"),
    ARRAY_OBJECT("Array<Object>"),
    IMAGE("Image"),
    INTEGER("Integer"),
    BOOLEAN("Boolean"),
    ARRAY_INTEGER("Array<Integer>"),
    ARRAY_BOOLEAN("Array<Boolean>"),
    CODE("Code");

    private final String name;

    FlowValueTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static FlowValueTypeEnum getEnumByName(String name) {
        for (FlowValueTypeEnum value : FlowValueTypeEnum.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
