package com.xiaomi.mone.monitor.service.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangxiaowei6
 */
@Data
public class ServiceMarketQuery implements Serializable {
    private Integer id;
    private String marketName;
    private String belongTeam;
    private String serviceList;
    private String remark;
    private Integer serviceType;
}
