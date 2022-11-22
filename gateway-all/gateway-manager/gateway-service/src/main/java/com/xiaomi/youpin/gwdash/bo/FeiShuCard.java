package com.xiaomi.youpin.gwdash.bo;

import lombok.Data;

import java.util.Map;

/**
 * @author tsingfu
 */
@Data
public class FeiShuCard {

    private String challenge;

    private String token;

    private String type;

    private String open_id;

    private String user_id;

    private String tenant_key;

    private String open_message_id;

    private Map<String, Object> action;
}
