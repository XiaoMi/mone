package run.mone.m78.api.enums;


/**
 * @author wmin
 * @date 2024/2/29
 */
public enum InputValueTypeEnum {

    STRING("String"),
    OBJECT("Object"),
    ARRAY_STRING("Array<String>"),
    ARRAY_OBJECT("Array<Object>"),
    IMAGE("Image"),
    INTEGER("Integer"),
    BOOLEAN("Boolean"),
    ARRAY_INTEGER("Array<Integer>"),
    ARRAY_BOOLEAN("Array<Boolean>");

    private final String name;

    InputValueTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static InputValueTypeEnum getEnumByName(String name) {
        for (InputValueTypeEnum value : InputValueTypeEnum.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return InputValueTypeEnum.STRING;
    }
}
