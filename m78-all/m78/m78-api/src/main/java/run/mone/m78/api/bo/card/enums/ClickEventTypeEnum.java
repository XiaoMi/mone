package run.mone.m78.api.bo.card.enums;

import java.util.HashMap;
import java.util.Map;

public enum ClickEventTypeEnum {


    OPEN_URL("OPEN_URL", "打开URL"),
    SEND_MSG_TO_BOT("SEND_MSG_TO_BOT", "给Bot发送消息");


    private String clickEventType;

    private String clickEventDesc;

    ClickEventTypeEnum(String clickEventType, String clickEventDesc) {
        this.clickEventType = clickEventType;
        this.clickEventDesc = clickEventDesc;
    }

    public static Map<String, String> getClickEventTypeMap() {
        Map<String, String> clickEventMap = new HashMap<>();
        for (ClickEventTypeEnum clickEventType : ClickEventTypeEnum.values()) {
            clickEventMap.put(clickEventType.clickEventType, clickEventType.clickEventDesc);
        }
        return clickEventMap;
    }
}
