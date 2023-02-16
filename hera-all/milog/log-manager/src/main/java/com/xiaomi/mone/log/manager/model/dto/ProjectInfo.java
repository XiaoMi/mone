package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/15 11:50
 */
@Data
public class ProjectInfo implements Serializable {
    private Long id;

    private String name;

    private String mioneEnv;

    private String desc;

    private long ctime;

    private long utime;

    private String domain;
    /**
     * 项目重要程度 1.一级项目 2.2级别项目 3.3级项目
     */
    private Integer importLevel;

    private Long iamTreeId;
}
