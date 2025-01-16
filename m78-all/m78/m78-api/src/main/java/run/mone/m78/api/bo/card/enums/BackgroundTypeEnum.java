package run.mone.m78.api.bo.card.enums;

import java.util.HashMap;
import java.util.Map;

public enum BackgroundTypeEnum {
    Transparent("Transparent", "透明"),

    Color("Color", "颜色"),
    Picture("Picture", "图片");

    private String backgroundType;

    private String backgroundDesc;

    BackgroundTypeEnum(String backgroundType, String backgroundDesc) {
        this.backgroundType = backgroundType;
        this.backgroundDesc = backgroundDesc;
    }

    public static Map<String, String> getBackgroundTypeMap() {
        Map<String, String> backgroundTypeMap = new HashMap<>();
        for (BackgroundTypeEnum backgroundTypeEnum : BackgroundTypeEnum.values()) {
            backgroundTypeMap.put(backgroundTypeEnum.backgroundType, backgroundTypeEnum.backgroundDesc);
        }
        return backgroundTypeMap;
    }

}
