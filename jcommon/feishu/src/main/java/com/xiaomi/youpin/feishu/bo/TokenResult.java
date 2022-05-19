package com.xiaomi.youpin.feishu.bo;

import lombok.Data;

@Data
public class TokenResult extends Result{
    /**
     * 访问 token
     */
    private String tenant_access_token;

    /**
     * tenant_access_token 过期时间 (单位秒)
     */
    private long expire;


    private long utime;

}
