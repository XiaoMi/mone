package com.xiaomi.mone.monitor.service.model.prometheus;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangxiaowei
 */
@Data
public class TeslaMetric implements Serializable {
    private String group;
    private String url;
    private double value;
}
