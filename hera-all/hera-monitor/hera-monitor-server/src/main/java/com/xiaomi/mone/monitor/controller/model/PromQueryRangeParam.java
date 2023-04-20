package com.xiaomi.mone.monitor.controller.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author gaoxihui
 * @date 2021/8/9 5:48 下午
 */
@Data
public class PromQueryRangeParam implements Serializable {
    String timeUnit;
    Long timeDuration;
    Integer projectId;
    String projectName;
    String metric;
    String metricSuffix;
    Map<String,String> labels;
    Double timeCost;
    String op;
    double value;
    Long startTime;
    Long endTime;
    Long step;
}
