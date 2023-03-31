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

//    grpc_server_error_times 	(AlarmPresetMetrics.	grpc_server_error_times,3, " rpc service and method"),
//    grpc_server_availability 	(AlarmPresetMetrics.	    grpc_server_availability,3, " rpc service and method"),
//    grpc_server_qps 		    (AlarmPresetMetrics.grpc_server_qps,3, " rpc service and method"),
//    grpc_server_slow_times 		(AlarmPresetMetrics.    grpc_server_slow_times,3, " rpc service and method"),
//    grpc_server_time_cost 		(AlarmPresetMetrics.    grpc_server_time_cost,3, " rpc service and method"),
//    grpc_client_error_times 	(AlarmPresetMetrics.	    grpc_client_error_times,3, " rpc service and method"),
//    grpc_client_availability 	(AlarmPresetMetrics.	    grpc_client_availability,3, " rpc service and method"),
//    grpc_client_qps 		    (AlarmPresetMetrics.grpc_client_qps,3, " rpc service and method"),
//    grpc_client_slow_times 		(AlarmPresetMetrics.    grpc_client_slow_times,3, " rpc service and method"),
//    grpc_client_time_cost 		(AlarmPresetMetrics.    grpc_client_time_cost,3, " rpc service and method"),
//    apus_server_error_times 	(AlarmPresetMetrics.	    apus_server_error_times,3, " rpc service and method"),
//    apus_server_availability 	(AlarmPresetMetrics.	    apus_server_availability,3, " rpc service and method"),
//    apus_server_qps 		    (AlarmPresetMetrics.    apus_server_qps,3, " rpc service and method"),
//    apus_server_slow_times 		(AlarmPresetMetrics.    apus_server_slow_times,3, " rpc service and method"),
//    apus_server_time_cost 		(AlarmPresetMetrics.    apus_server_time_cost,3, " rpc service and method"),
//    apus_client_error_times 	(AlarmPresetMetrics.	    apus_client_error_times,3, " rpc service and method"),
//    apus_client_availability 	(AlarmPresetMetrics.	    apus_client_availability,3, " rpc service and method"),
//    apus_client_qps 		    (AlarmPresetMetrics.apus_client_qps,3, " rpc service and method"),
//    apus_client_slow_times 		(AlarmPresetMetrics.    apus_client_slow_times,3, " rpc service and method"),
//    apus_client_time_cost 		(AlarmPresetMetrics.    apus_client_time_cost,3, " rpc service and method"),
//    thrift_server_error_times 	(AlarmPresetMetrics.	    thrift_server_error_times,3, " rpc service and method"),
//    thrift_server_availability 	(AlarmPresetMetrics.	    thrift_server_availability,3, " rpc service and method"),
//    thrift_server_qps 		    (AlarmPresetMetrics.thrift_server_qps,3, " rpc service and method"),
//    thrift_server_slow_times 	(AlarmPresetMetrics.	    thrift_server_slow_times,3, " rpc service and method"),
//    thrift_server_time_cost 	(AlarmPresetMetrics.	    thrift_server_time_cost,3, " rpc service and method"),
//    thrift_client_error_times 	(AlarmPresetMetrics.	    thrift_client_error_times,3, " rpc service and method"),
//    thrift_client_availability 	(AlarmPresetMetrics.	    thrift_client_availability,3, " rpc service and method"),
//    thrift_client_qps 		    (AlarmPresetMetrics.thrift_client_qps,3, " rpc service and method"),
//    thrift_client_slow_times 	(AlarmPresetMetrics.	    thrift_client_slow_times,3, " rpc service and method"),
//    thrift_client_time_cost 	(AlarmPresetMetrics.	    thrift_client_time_cost,3, " rpc service and method"),

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

    /**
     * kind=1或2是http类型
     * @param alert
     * @return
     */
    public static boolean httpType(String alert) {
        for (MetricLabelKind metricLabelKind : MetricLabelKind.values()) {
            if (metricLabelKind.kind != 1 && metricLabelKind.kind != 2) {
                continue;
            }
            if (metricLabelKind.metric.getCode().equals(alert)) {
                return true;
            }
        }
        return false;
    }

    /**
     * kind=3是dubbo类型
     * @param alert
     * @return
     */
    public static boolean dubboType(String alert) {
        for (MetricLabelKind metricLabelKind : MetricLabelKind.values()) {
            if (metricLabelKind.kind != 3) {
                continue;
            }
            if (metricLabelKind.metric.getCode().equals(alert)) {
                return true;
            }
        }
        return false;
    }

    public final static Map<AlarmPresetMetrics,MetricLabelKind> getMetricLabelKindMap() {
        Map<AlarmPresetMetrics,MetricLabelKind> map = new HashMap<>();
        for (MetricLabelKind metricLabelKind : MetricLabelKind.values()) {
           map.put(metricLabelKind.metric, metricLabelKind);
        }
        return map;
    }

}
