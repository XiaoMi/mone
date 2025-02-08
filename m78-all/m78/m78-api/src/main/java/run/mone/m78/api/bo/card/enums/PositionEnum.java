package run.mone.m78.api.bo.card.enums;

import java.util.HashMap;
import java.util.Map;

public enum PositionEnum {

    //水平
    HorizontalLeft("HorizontalLeft", "左对齐"),
    HorizontalMiddle("HorizontalMiddle", "水平居中"),
    HorizontalRight("HorizontalRight", "右对齐"),

    //垂直
    VerticalUpper("VerticalUpper", "上对齐"),
    VerticalMiddle("VerticalMiddle", "垂直居中"),
    VerticalLower("VerticalLower", "下对齐");

    private String position;

    private String positionDesc;

    PositionEnum(String position, String positionDesc) {
        this.position = position;
        this.positionDesc = positionDesc;
    }

    public static Map<String, String> getAllPositionMap() {
        Map<String, String> positionMap = new HashMap<>();
        for (PositionEnum positionEnum : PositionEnum.values()) {
            positionMap.put(positionEnum.position, positionEnum.positionDesc);
        }
        return positionMap;
    }

    public static Map<String, String> getHorizontalPositionMap() {
        Map<String, String> positionMap = new HashMap<>();
        for (PositionEnum positionEnum : PositionEnum.values()) {
            if (positionEnum.position.startsWith("Horizontal")) {
                positionMap.put(positionEnum.position, positionEnum.positionDesc);
            }
        }
        return positionMap;
    }

    public static Map<String, String> getVerticalPositionMap() {
        Map<String, String> positionMap = new HashMap<>();
        for (PositionEnum positionEnum : PositionEnum.values()) {
            if (positionEnum.position.startsWith("Vertical")) {
                positionMap.put(positionEnum.position, positionEnum.positionDesc);
            }
        }
        return positionMap;
    }

}
