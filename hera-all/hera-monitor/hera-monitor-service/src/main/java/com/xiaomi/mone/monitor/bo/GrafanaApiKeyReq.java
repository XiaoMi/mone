package com.xiaomi.mone.monitor.bo;

import lombok.Data;

/**
 * @author zhangxiaowei6
 * @date 2023-02-22
 */
@Data
public class GrafanaApiKeyReq {
    private String name;
    private String role;
    public GrafanaApiKeyReq(String name,String role){
        this.name = name;
        this.role = role;
    }
}
