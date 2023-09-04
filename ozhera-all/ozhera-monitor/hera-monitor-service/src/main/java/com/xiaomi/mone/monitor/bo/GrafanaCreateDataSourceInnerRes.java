package com.xiaomi.mone.monitor.bo;

import lombok.Data;

/**
 * @author zhangxiaowei6
 * @date 2023-02-23
 */
@Data
public class GrafanaCreateDataSourceInnerRes {
    private int id;
    private String uid;
    private int orgId;
    private String name;
    private String type;
    private String typeLogoUrl;
    private String access;
    private String url;
    private String user;
    private String database;
    private boolean basicAuth;
    private String basicAuthUser;
    private boolean withCredentials;
    private boolean isDefault;
    private int version;
    private boolean readOnly;
}
