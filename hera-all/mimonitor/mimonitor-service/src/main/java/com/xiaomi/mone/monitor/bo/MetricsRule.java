package com.xiaomi.mone.monitor.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 告警指标规则
 * @author zhanggaofeng1
 */
@Data
public class MetricsRule implements Serializable {

    String value;
    String label;
    String unit;
    Integer strategyType;
    private int kind;
    String metricType;
    Boolean hideValueConfig;

    public MetricsRule(String value,String label, String unit, Integer strategyType,String metricType,Boolean hideValueConfig){
        this.label = label;
        this.value = value;
        this.unit = unit;
        this.strategyType = strategyType;
        this.metricType = metricType;
        this.hideValueConfig = hideValueConfig;
    }
}
