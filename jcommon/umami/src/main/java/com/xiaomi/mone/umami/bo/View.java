package com.xiaomi.mone.umami.bo;

/**
 * @author tsingfu
 */
public class View {

    private String type = "pageview";

    private Payload payload;

    public View(String website, String viewName) {
        payload = Payload.builder()
                .url(viewName)
                .screen("1680x1050")
                .language("zh-CN")
                .website(website)
                .build();
    }

    public Payload getPayload() {
        return payload;
    }

    public String getType() {
        return type;
    }
}
