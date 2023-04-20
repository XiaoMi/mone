package com.xiaomi.mone.monitor.service.model;

import lombok.Data;

import java.io.Serializable;
/**
 * @author zhangxiaowei6
 */
@Data
public class QualityMarketQuery implements Serializable {
    private Integer id;
    private String marketName;
    private String serviceList;
    private String remark;
}
