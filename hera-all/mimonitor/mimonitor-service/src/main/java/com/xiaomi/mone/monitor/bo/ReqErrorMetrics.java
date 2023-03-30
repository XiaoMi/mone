package com.xiaomi.mone.monitor.bo;

import org.apache.commons.lang3.StringUtils;

/**
 * @author gaoxihui
 */
public enum ReqErrorMetrics {
    httpError("httpError","http请求错误", AlarmPresetMetrics.http_error_times, AlarmPresetMetrics.http_availability),
    httpClientError("httpClientError","httpClient请求错误", AlarmPresetMetrics.http_client_error_times,AlarmPresetMetrics.http_client_availability),
    dbError("dbError","db请求错误", AlarmPresetMetrics.db_error_times, AlarmPresetMetrics.db_availability),
    redisError("redisError","redis请求错误"),
    dubboConsumerError("dubboConsumerError","dubbo请求错误", AlarmPresetMetrics.dubbo_error_times,AlarmPresetMetrics.dubbo_availability),
    dubboProvider("dubboProviderError","dubboProvider请求错误", AlarmPresetMetrics.dubbo_provider_availability,AlarmPresetMetrics.dubbo_provider_error_times),

//    grpcServerError("grpcServerError","grpcServerError请求错误", AlarmPresetMetrics.grpc_server_error_times,AlarmPresetMetrics.grpc_server_availability),
//    apusServerError("apusServerError","apusServerError请求错误", AlarmPresetMetrics.apus_server_error_times,AlarmPresetMetrics.apus_server_availability),
//    thriftServerError("thriftServerError","thriftServerError请求错误", AlarmPresetMetrics.thrift_server_error_times,AlarmPresetMetrics.thrift_server_availability),
//
//    grpcClientError("grpcClientError","grpcClient请求错误", AlarmPresetMetrics.grpc_client_error_times,AlarmPresetMetrics.grpc_client_availability),
//    apusClientError("apusClientError","apusClient请求错误", AlarmPresetMetrics.apus_client_error_times,AlarmPresetMetrics.apus_client_availability),
//    thriftClientError("thriftClientError","thriftClient请求错误", AlarmPresetMetrics.thrift_client_error_times,AlarmPresetMetrics.thrift_client_availability),
    ;
    private String code;
    private String message;
    private AlarmPresetMetrics[] metrics;

    ReqErrorMetrics(String code, String message, AlarmPresetMetrics... metrics){
        this.code = code;
        this.message = message;
        this.metrics = metrics;
    }

    public String getCode() {
        return code;
    }

    public AlarmPresetMetrics[] getMetrics() {
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

    public static ReqErrorMetrics getErrorMetricsByMetrics(String metrics) {
        if (StringUtils.isBlank(metrics)) {
            return null;
        }
        for (ReqErrorMetrics errMetrics : ReqErrorMetrics.values()) {
            if (errMetrics.metrics == null || errMetrics.metrics.length == 0) {
                continue;
            }
            for (AlarmPresetMetrics ele : errMetrics.metrics) {
                if (ele.getCode().equals(metrics)) {
                    return errMetrics;
                }
            }
        }
        return null;
    }

}
