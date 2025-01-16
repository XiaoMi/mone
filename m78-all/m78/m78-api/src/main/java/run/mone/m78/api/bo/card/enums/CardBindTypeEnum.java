package run.mone.m78.api.bo.card.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum CardBindTypeEnum {
    PLUGIN("PLUGIN"),

    FLOW("FLOW");

    private String cardBindType;

    CardBindTypeEnum(String cardBindType) {
        this.cardBindType = cardBindType;
    }

    public String getCardBindType() {
        return cardBindType;
    }

    //返回List<String>，所有枚举
    public static List<String> getCardBindTypes() {
        return Arrays.stream(CardBindTypeEnum.values())
                .map(CardBindTypeEnum::getCardBindType)
                .collect(Collectors.toList());
    }

}
