package com.xiaomi.youpin.teambition.bo;

import com.xiaomi.youpin.teambition.Teambition;
import lombok.Data;

@Data
public class AppTokenInfo {
    /**
     * 访问 token
     */
    private String appToken;

    /**
     * tenant_access_token 过期时间 (单位毫秒)
     */
    private long expire = Teambition.EXPIRES_IN;


    private long utime;
}
