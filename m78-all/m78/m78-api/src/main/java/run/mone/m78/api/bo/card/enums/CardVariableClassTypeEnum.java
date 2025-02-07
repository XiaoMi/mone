package run.mone.m78.api.bo.card.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum CardVariableClassTypeEnum {


    String("String"),
    Boolean("Boolean"),
    Number("Number"),
    Array("Array"),
    Object("Object");

    private String classType;

    CardVariableClassTypeEnum(String classType) {
        this.classType = classType;
    }

    //获取所有枚举值的classType列表
    public static List<String> getAllClassTypes() {
        return Arrays.stream(CardVariableClassTypeEnum.values())
                .map(CardVariableClassTypeEnum::getClassType)
                .collect(Collectors.toList());
    }

    public String getClassType() {
        return this.classType;
    }

}
