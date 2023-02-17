package com.xiaomi.mone.log.api.model.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: wtt
 * @date: 2022/5/24 18:24
 * @description:
 */
@Data
public class MiLogMoneTransfer implements Serializable {
    private Long milogAppId;
    private Long appId;
    private String appName;
    private Long envId;
    private String envName;
    private List<String> tailNames;
}
