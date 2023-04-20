package com.xiaomi.mone.log.api.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/12/8 14:27
 */
@Data
public class MontorAppDTO implements Serializable {
    private Long appId;
    private String appName;
    private String source;
    private Boolean isAccess = false;
}
