package com.xiaomi.mone.app.api.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class HeraAppBaseInfoModel implements Serializable {

    private Integer id;

    private String bindId;

    private Integer bindType;

    private String appName;

    private String appCname;

    private Integer appType;

    private String appLanguage;

    private Integer platformType;

    private String appSignId;

    private Integer iamTreeId;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    private String envsMap;
}
