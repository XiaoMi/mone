package com.xiaomi.mone.log.api.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: wtt
 * @date: 2022/5/24 18:29
 * @description:
 */
@Data
public class MiLogMoneEnv implements Serializable {
    private Long oldAppId;
    private String oldAppName;
    private Long oldEnvId;
    private String oldEnvName;
    private Long newAppId;
    private String newAppName;
    private Long newEnvId;
    private String newEnvName;
    /**
     * 如果会滚则rollback = 1
     */
    private Integer rollback;
}
