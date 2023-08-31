package com.xiaomi.mone.monitor.pojo;

import lombok.Data;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/20 3:13 PM
 */
@Data
public class ReqSlowMetricsPOJO {

    private String code;
    private String message;
    private AlarmPresetMetricsPOJO metrics;
}
