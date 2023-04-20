package com.xiaomi.mone.log.manager.model.bo;

import lombok.Data;

import java.time.Instant;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/4/19 14:16
 */
@Data
public class AccessMilogParam {

    private String spaceName;
    private String storeName;
    private Long appId;
    private String appName;
    private String appCreator;
    private Long appCreatTime;
    private Long funcId;
    private String funcName;
    private String logPath;
    private Integer appType;
    private String appTypeText;
    private String envName;
    private Long envId;

    private String machineRoom = "cn";

    public static void main(String[] args) {
        System.out.println(Instant.now().toEpochMilli());
    }
}
