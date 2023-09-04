package com.xiaomi.mone.monitor.bo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author zhangxiaowei6
 * @date 2023-03-21
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GrafanaGetDataSourceRes {
    private int id;
    private String uid;
    private int orgId;
    private String name;
    private String type;
    private String typeLogoUrl;
    private String access;
    private String url;
    private String password;
    private String user;
    private String database;
    private boolean basicAuth;
    private String basicAuthUser;
    private String basicAuthPassword;
    private boolean withCredentials;
    private boolean isDefault;
    private int version;
    private boolean readOnly;
    private String message;
}
