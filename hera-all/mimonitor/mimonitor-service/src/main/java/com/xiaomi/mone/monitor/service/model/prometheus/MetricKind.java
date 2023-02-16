package com.xiaomi.mone.monitor.service.model.prometheus;

import org.apache.commons.lang3.StringUtils;

/**
 * 指标类型枚举
 * @author gaoxihui
 * @date 2022/1/12 2:26 下午
 */
public enum MetricKind {

    http("methodName"),
    dubbo("methodName"),
    grpc("methodName"),
    apus("methodName"),
    thrift("methodName"),
    rpc("methodName"),
    redis("methodName"),
    db("sqlMethod");

    private String lebelName;

    MetricKind(String lebelName) {
        this.lebelName = lebelName;
    }

    public String getLebelName() {
        return lebelName;
    }

    public static MetricKind getByMetricType(String metricTypeCode){
        if(StringUtils.isBlank(metricTypeCode)){
            return null;
        }

        MetricType[] values = MetricType.values();
        for(MetricType value : values){
            if(value.getCode().equals(metricTypeCode)){
                return MetricKind.valueOf(value.getKind());
            }
        }

        return null;
    }

    public static MetricType getMetricTypeByCode(String metricTypeCode){
        if(StringUtils.isBlank(metricTypeCode)){
            return null;
        }
        MetricType[] values = MetricType.values();
        for(MetricType value : values){
            if(value.getCode().equals(metricTypeCode)){
                return value;
            }
        }
        return null;
    }

    public enum MetricType{

        http_exception("httpException","http异常","http"),
        http_client_exception("httpClientException","httpClient异常","http"),
        dubbo_consumer_exception("dubboConsumerExcption","dubboConsumer异常","dubbo"),
        dubbo_provider_exception("dubboProviderExcption","dubboProvider异常","dubbo"),
        dubbo_consumer_slow_query("dubboConsumerSlow","dubboConsumer慢查询","dubbo"),
        dubbo_provider_slow_query("dubboProviderSlow","dubboProvider慢查询","dubbo"),
        db_exception("dbException","db异常","db"),
        db_slow_query("dbSlow","db慢查询","db"),
        redis_exception("redisException","redis异常","redis"),


        grpc_server_exception("grpcServerError","grpcServer异常","grpc"),
        grpc_client_exception("grpcClientError","grpcClient异常","grpc"),
        apus_server_exception("apusServerError","apusServer异常","apus"),
        apus_client_exception("apusClientError","apusClient异常","apus"),
        thrift_server_exception("thriftServerError","thriftServer异常","thrift"),
        thrift_client_exception("thriftClientError","thriftClient异常","thrift"),

        grpc_server_slow_query("grpcServerSlowQuery","grpcServer慢查询","grpc"),
        grpc_client_slow_query("grpcClientSlowQuery","grpcClient慢查询","grpc"),
        apus_server_slow_query("apusServerSlowQuery","apusServer慢查询","apus"),
        apus_client_slow_query("apustClientSlowQuery","apusClient慢查询","apus"),
        thrift_server_slow_query("thriftServerSlowQuery","thriftServer慢查询","thrift"),
        thrift_client_slow_query("thriftClientSlowQuery","thriftClient慢查询","thrift"),

        ;

        private String code;
        private String desc;
        private String kind;

        MetricType(String code, String desc, String kind) {
            this.code = code;
            this.desc = desc;
            this.kind = kind;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public String getKind() {
            return kind;
        }

    }
}
