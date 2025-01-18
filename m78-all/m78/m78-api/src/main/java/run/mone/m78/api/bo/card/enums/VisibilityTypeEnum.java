package run.mone.m78.api.bo.card.enums;

import java.util.HashMap;
import java.util.Map;

public enum VisibilityTypeEnum {
    AlwaysDisplay("AlwaysDisplay", "始终显示"),

    DisplayWithConditions("DisplayWithConditions", "满足条件时显示"),
    HideWithConditions("HideWithConditions", "满足条件时隐藏");

    private String visibilityType;

    private String visibilityDesc;

    VisibilityTypeEnum(String visibilityType, String visibilityDesc) {
        this.visibilityType = visibilityType;
        this.visibilityDesc = visibilityDesc;
    }

    public static Map<String, String> getVisibilityTypeMap() {
        Map<String, String> visibilityTypeMap = new HashMap<>();
        for (VisibilityTypeEnum visibilityTypeEnum : VisibilityTypeEnum.values()) {
            visibilityTypeMap.put(visibilityTypeEnum.visibilityType, visibilityTypeEnum.visibilityDesc);
        }
        return visibilityTypeMap;
    }

}
