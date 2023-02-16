package com.xiaomi.mone.monitor.bo;

import org.apache.commons.lang3.StringUtils;

/**
 * @author gaoxihui
 */
public enum ReqSlowMetrics {
    dbSlowQuery("dbSlowQuery","db慢请求", AlarmPresetMetrics.db_slow_query),
    dubboConsumerSlowQuery("dubboConsumerSlowQuery","dubboConsumer慢请求", AlarmPresetMetrics.dubbo_slow_query),
    dubboProviderSlowQuery("dubboProviderSlowQuery","dubboProvider慢请求", AlarmPresetMetrics.dubbo_provider_slow_query),

    grpcClientSlowQuery("grpcClientSlowQuery","grpcClient慢请求", AlarmPresetMetrics.grpc_client_slow_times),
    grpcServerSlowQuery("grpcServerSlowQuery","grpcServer慢请求", AlarmPresetMetrics.grpc_server_slow_times),
    thriftClientSlowQuery("thriftClientSlowQuery","gthriftClient慢请求", AlarmPresetMetrics.thrift_client_slow_times),
    thriftServerSlowQuery("thriftServerSlowQuery","thriftServer慢请求", AlarmPresetMetrics.thrift_server_slow_times),
    apusClientSlowQuery("apusClientSlowQuery","apustClient慢请求", AlarmPresetMetrics.apus_client_slow_times),
    apusServerSlowQuery("apusServerSlowQuery","apustClient慢请求", AlarmPresetMetrics.thrift_server_slow_times),
    ;
    private String code;
    private String message;
    private AlarmPresetMetrics metrics;

    ReqSlowMetrics(String code, String message, AlarmPresetMetrics metrics){
        this.code = code;
        this.message = message;
        this.metrics = metrics;
    }

    public String getCode() {
        return code;
    }

    public AlarmPresetMetrics getMetrics() {
        return metrics;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ReqSlowMetrics getSlowMetricsByMetric(String metrics) {
        if (StringUtils.isBlank(metrics)) {
            return null;
        }
        for (ReqSlowMetrics errMetrics : ReqSlowMetrics.values()) {
            if (errMetrics.metrics != null && errMetrics.metrics.getCode().equals(metrics)) {
                return errMetrics;
            }
        }
        return null;
    }

}
