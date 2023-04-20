package com.xiaomi.mone.monitor.service.model.prometheus;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2021/9/16 9:47 下午
 */
@Data
public class AlertTeamQuery implements Serializable {
    private String name;
    private String note;
    private String manager;
    private String oncallUser;
    private String service;
    private Integer iamId;
    private Integer page;
    private Integer pageSize;
}
