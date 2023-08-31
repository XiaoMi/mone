package com.xiaomi.mone.monitor.pojo;

import com.xiaomi.mone.monitor.bo.AlarmStrategyType;
import com.xiaomi.mone.monitor.bo.InterfaceMetricTypes;
import com.xiaomi.mone.monitor.bo.MetricsUnit;
import com.xiaomi.mone.monitor.bo.SendAlertGroupKey;
import lombok.Data;

/**
 * @author gaoxihui
 */
@Data
public class AlarmPresetMetricsPOJO {

    private String code;
    private String message;
    private String errorMetric;
    private String totalMetric;
    private String slowQueryMetric;
    private String timeCostMetric;
    private MetricsUnit unit;
    private SendAlertGroupKey groupKey;
    private AlarmStrategyType strategyType;
    private InterfaceMetricTypes metricType;
    private Boolean hideValueConfig;//是否隐藏页面的value配置，值为true隐藏页面的value配置
    private BasicUrlTypePOJO basicUrlType;
    private String viewPanel;
    private String env;
    private String domain;
}
