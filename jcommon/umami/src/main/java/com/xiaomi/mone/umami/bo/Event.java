package com.xiaomi.mone.umami.bo;

/**
 * @author tsingfu
 */
public class Event {

    private String type = "event";

    private Payload payload;

    private String url = "";

    public Event(String eventType, String eventValue) {
        payload = Payload.builder()
                .event_type(eventType)
                .event_value(eventValue)
                .url(url)
                .screen("1680x1050")
                .language("zh-CN")
                .website(Constant.website)
                .build();
    }

    public Payload getPayload() {
        return payload;
    }

    public String getType() {
        return type;
    }
}
