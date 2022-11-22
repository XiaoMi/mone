package com.xiaomi.youpin.gwdash.bo;

import lombok.Data;

/**
 * @author tsingfu
 */
@Data
public class NacosLoginInfo {
    private String accessToken;
    private long tokenTtl;
    private boolean globalAdmin;
}
