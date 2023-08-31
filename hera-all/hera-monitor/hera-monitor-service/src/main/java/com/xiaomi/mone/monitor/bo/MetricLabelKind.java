package com.xiaomi.mone.monitor.bo;

import java.util.*;

/**
 *
 */
public enum MetricLabelKind {

    http_error_times(AlarmPresetMetrics.http_error_times,1, "http url and code"),
    http_availability(AlarmPresetMetrics.http_availability,2, "http url"),
    http_qps(AlarmPresetMetrics.http_qps,2, "http url"),
    http_cost(AlarmPresetMetrics.http_cost,2, "http url"),

    http_client_error_times(AlarmPresetMetrics.http_client_error_times,1, "http url and code"),
    http_client_availability(AlarmPresetMetrics.http_client_availability,2, "http url"),
    http_client_qps(AlarmPresetMetrics.http_client_qps,2, "http url"),
    http_client_cost(AlarmPresetMetrics.http_client_cost,2, "http url"),

    dubbo_error_times(AlarmPresetMetrics.dubbo_error_times,3, "dubbo service and method"),
    dubbo_provider_error_times(AlarmPresetMetrics.dubbo_provider_error_times,3, "dubbo service and method"),
    dubbo_qps(AlarmPresetMetrics.dubbo_qps,3, "dubbo service and method"),
    dubbo_provider_qps(AlarmPresetMetrics.dubbo_provider_qps,3, "dubbo service and method"),
    dubbo_cost(AlarmPresetMetrics.dubbo_cost,3, "dubbo service and method"),
    dubbo_provider_cost(AlarmPresetMetrics.dubbo_provider_cost,3, "dubbo service and method"),
    dubbo_availability(AlarmPresetMetrics.dubbo_availability,3, "dubbo service and method"),
    dubbo_provider_availability(AlarmPresetMetrics.dubbo_provider_availability,3, "dubbo service and method"),
    dubbo_slow_query(AlarmPresetMetrics.dubbo_slow_query,3, "dubbo service and method"),
    dubbo_provider_slow_query(AlarmPresetMetrics.dubbo_provider_slow_query,3, "dubbo service and method"),

    ;

    private AlarmPresetMetrics metric;
    private int kind;
    private String message;

    MetricLabelKind(AlarmPresetMetrics metric, int kind, String message){
        this.metric = metric;
        this.kind = kind;
        this.message = message;
    }

    public AlarmPresetMetrics getMetric() {
        return metric;
    }

    public int getKind() {
        return kind;
    }

    public String getMessage() {
        return message;
    }


    public final static Map<AlarmPresetMetrics,MetricLabelKind> getMetricLabelKindMap() {
        Map<AlarmPresetMetrics,MetricLabelKind> map = new HashMap<>();
        for (MetricLabelKind metricLabelKind : MetricLabelKind.values()) {
           map.put(metricLabelKind.metric, metricLabelKind);
        }
        return map;
    }

}
