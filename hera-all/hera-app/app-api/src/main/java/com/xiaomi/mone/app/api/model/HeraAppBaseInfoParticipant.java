package com.xiaomi.mone.app.api.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2022/3/20 10:32 上午
 */
@Data
public class HeraAppBaseInfoParticipant implements Serializable {

    private Integer id;

    private String appId;

    private String appName;

    private String appCname;

    private Integer iamTreeId;

    private Integer iamTreeType;

    private Integer bindType;

    private Integer platformType;

    private Integer appType;

    private String appLanguage;

    private String envsMapping;

    private Integer status;

    private Integer participant;


}
