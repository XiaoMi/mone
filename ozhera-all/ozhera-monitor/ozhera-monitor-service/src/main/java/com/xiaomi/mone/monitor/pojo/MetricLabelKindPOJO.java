package com.xiaomi.mone.monitor.pojo;

import lombok.Data;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/20 3:20 PM
 */
@Data
public class MetricLabelKindPOJO {

    private AlarmPresetMetricsPOJO metric;
    private int kind;
    private String message;
}
