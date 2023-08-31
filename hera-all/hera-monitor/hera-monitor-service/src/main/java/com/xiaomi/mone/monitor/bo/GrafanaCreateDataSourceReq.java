package com.xiaomi.mone.monitor.bo;

import lombok.Data;

/**
 * @author zhangxiaowei6
 * @date 2023-02-23
 */
@Data
public class GrafanaCreateDataSourceReq {
    private String name;
    private String type;
    private String url;
    private String access;
    private Boolean basicAuth;
}
