package run.mone.m78.api.bo.card.enums;

import java.util.HashMap;
import java.util.Map;

public enum CardStatusEnum {
    NOT_PUBLISHED(0, "未发布"),

    PUBLISHED(1, "已发布");

    private Integer cardStatusCode;

    private String cardStatusDesc;

    CardStatusEnum(Integer cardStatusCode, String cardStatusDesc) {
        this.cardStatusCode = cardStatusCode;
        this.cardStatusDesc = cardStatusDesc;
    }

    public Integer getCode() {
        return this.cardStatusCode;
    }

    public static Map<Integer, String> getCardStatusMap() {
        Map<Integer, String> cardStatusMap = new HashMap<>();
        for (CardStatusEnum cardStatusEnum : CardStatusEnum.values()) {
            cardStatusMap.put(cardStatusEnum.cardStatusCode, cardStatusEnum.cardStatusDesc);
        }
        return cardStatusMap;
    }

}
