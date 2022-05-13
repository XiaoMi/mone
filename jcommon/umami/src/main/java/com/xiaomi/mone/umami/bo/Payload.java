package com.xiaomi.mone.umami.bo;

import lombok.Builder;
import lombok.Data;

/**
 * @author tsingfu
 */
@Data
@Builder
public class Payload {
    private String event_type;

    private String event_value;

    private String language;

    private String screen;

    private String url;

    private String website;
}
