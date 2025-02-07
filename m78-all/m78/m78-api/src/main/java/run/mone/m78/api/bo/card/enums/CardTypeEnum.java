package run.mone.m78.api.bo.card.enums;

import java.util.HashMap;
import java.util.Map;

public enum CardTypeEnum {


    INPUT_BINDING("INPUT_BINDING", "INPUT类型"),
    OUTPUT_BINDING("OUTPUT_BINDING", "OUTPUT类型");


    private String cardType;

    private String cardTypeDesc;

    CardTypeEnum(String cardType, String cardTypeDesc) {
        this.cardType = cardType;
        this.cardTypeDesc = cardTypeDesc;
    }

    public static Map<String, String> getCardTypeMap() {
        Map<String, String> cardTypeMap = new HashMap<>();
        for (CardTypeEnum cardTypeEnum : CardTypeEnum.values()) {
            cardTypeMap.put(cardTypeEnum.cardType, cardTypeEnum.cardTypeDesc);
        }
        return cardTypeMap;
    }
}
