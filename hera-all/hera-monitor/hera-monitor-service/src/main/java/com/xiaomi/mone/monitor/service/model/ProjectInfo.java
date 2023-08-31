package com.xiaomi.mone.monitor.service.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2021/7/9 9:35 上午
 */
@Data
public class ProjectInfo implements Serializable {
    private Long id;

    private String name;

    private String gitName;

    private String mioneEnv;

    private String desc;

    private long ctime;

    private long utime;

    private String domain;

    private Long iamTreeId;
}
