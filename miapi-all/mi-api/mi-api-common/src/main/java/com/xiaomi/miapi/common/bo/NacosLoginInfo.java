package com.xiaomi.miapi.common.bo;

import lombok.Data;

@Data
public class NacosLoginInfo {
    private String accessToken;
    private long tokenTtl;
    private boolean globalAdmin;
}
