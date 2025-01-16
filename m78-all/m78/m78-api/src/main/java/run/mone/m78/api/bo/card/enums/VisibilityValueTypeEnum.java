package run.mone.m78.api.bo.card.enums;

import java.util.HashMap;
import java.util.Map;

public enum VisibilityValueTypeEnum {
    Constant("AlwaysDisplay", "常量"),

    Variable("Variable", "变量");

    private String valueType;

    private String desc;

    VisibilityValueTypeEnum(String valueType, String desc) {
        this.valueType = valueType;
        this.desc = desc;
    }

    public static Map<String, String> getValueTypeMap() {
        Map<String, String> valueTypeMap = new HashMap<>();
        for (VisibilityValueTypeEnum valueTypeEnum : VisibilityValueTypeEnum.values()) {
            valueTypeMap.put(valueTypeEnum.valueType, valueTypeEnum.desc);
        }
        return valueTypeMap;
    }

}
