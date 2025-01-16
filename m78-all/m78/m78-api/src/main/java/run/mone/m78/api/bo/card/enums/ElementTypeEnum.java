package run.mone.m78.api.bo.card.enums;

import java.util.ArrayList;
import java.util.List;

public enum ElementTypeEnum {

    /**
     * 卡片
     */
    CARD_ROOT("CARD_ROOT"),

    /**
     * 布局组件，以LAYOUT开头
     */
    LAYOUT_SINGLE_ROW_1("LAYOUT_SINGLE_ROW_1"),
    LAYOUT_MULTI_ROW_1_1_1("LAYOUT_MULTI_ROW_1_1_1"),
    LAYOUT_ROW("LAYOUT_ROW"),
    LAYOUT_GRID("LAYOUT_GRID"),

    LAYOUT_SIDESLIP("LAYOUT_SIDESLIP"),
    LAYOUT_FLOAT("LAYOUT_FLOAT"),

    SLOT("SLOT"),


    /**
     * 基础组件
     */
    BASE_COMPONENT_TITLE("BASE_COMPONENT_TITLE"),
    BASE_COMPONENT_TEXT("BASE_COMPONENT_TEXT"),
    BASE_COMPONENT_BUTTON("BASE_COMPONENT_BUTTON"),
    BASE_COMPONENT_IMAGE("BASE_COMPONENT_IMAGE"),
    BASE_COMPONENT_ICON("BASE_COMPONENT_ICON"),
    BASE_COMPONENT_TAG("BASE_COMPONENT_TAG"),
    BASE_COMPONENT_SCORE("BASE_COMPONENT_SCORE"),
    BASE_COMPONENT_DIVIDER("BASE_COMPONENT_DIVIDER"),


    /**
     * 表单
     */
    FORM_INPUT_BOX("FORM_INPUT_BOX"),
    FORM_DROPDOWN_SELECTION("FORM_DROPDOWN_SELECTION"),
    FORM_BUTTON_SELECTION("FORM_BUTTON_SELECTION")
    ;

    private String elementType;

    ElementTypeEnum(String elementType) {
        this.elementType = elementType;
    }

    public static List<String> getAllElementTypes() {
        List<String> list = new ArrayList<>();
        for (ElementTypeEnum elementType : ElementTypeEnum.values()) {
            list.add(elementType.getElementType());
        }
        return list;
    }

    public static List<String> getLayoutElementTypes() {
        List<String> list = new ArrayList<>();
        for (ElementTypeEnum elementType : ElementTypeEnum.values()) {
            if (elementType.getElementType().startsWith("LAYOUT")) {
                list.add(elementType.getElementType());
            }
        }
        return list;
    }

    public static List<String> getBaseComponentElementTypes() {
        List<String> list = new ArrayList<>();
        for (ElementTypeEnum elementType : ElementTypeEnum.values()) {
            if (elementType.getElementType().startsWith("BASE_COMPONENT")) {
                list.add(elementType.getElementType());
            }
        }
        return list;
    }

    public static List<String> getFormElementTypes() {
        List<String> list = new ArrayList<>();
        for (ElementTypeEnum elementType : ElementTypeEnum.values()) {
            if (elementType.getElementType().startsWith("FORM")) {
                list.add(elementType.getElementType());
            }
        }
        return list;
    }

    public String getElementType() {
        return this.elementType;
    }
}
