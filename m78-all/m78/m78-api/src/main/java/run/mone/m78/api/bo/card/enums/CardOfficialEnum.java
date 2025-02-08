package run.mone.m78.api.bo.card.enums;

import java.util.HashMap;
import java.util.Map;

public enum CardOfficialEnum {
    NOT_OFFICIAL(0, "非官方"),

    OFFICIAL(1, "官方");

    private Integer cardOfficialCode;

    private String cardOfficialDesc;

    CardOfficialEnum(Integer cardOfficialCode, String cardOfficialDesc) {
        this.cardOfficialCode = cardOfficialCode;
        this.cardOfficialDesc = cardOfficialDesc;
    }

    public Integer getCode() {
        return this.cardOfficialCode;
    }

    public static Map<Integer, String> getCardOfficialMap() {
        Map<Integer, String> cardOfficialMap = new HashMap<>();
        for (CardOfficialEnum cardOfficialEnum : CardOfficialEnum.values()) {
            cardOfficialMap.put(cardOfficialEnum.cardOfficialCode, cardOfficialEnum.cardOfficialDesc);
        }
        return cardOfficialMap;
    }

}
