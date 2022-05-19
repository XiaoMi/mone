package com.xiaomi.data.push.monitor.model;

import lombok.Data;

/**
 * @author maojinrui
 */
@Data
public class GetAccessTokenResponse {

    private Integer errcode;
    private String errmsg;
    private String access_token;
    private Integer expires_in;
}
