package com.xiaomi.mone.monitor.service.model.prometheus;

/**
 * @author gaoxihui
 * @date 2021/9/3 9:05 上午
 */
public enum EsIndexDataType {
    http,
    http_client,
    dubbo_consumer,
    dubbo_provider,

    grpc_client,
    grpc_server,
    thrift_client,
    thrift_server,
    apus_client,
    apus_server,
    mq_consumer,
    mq_producer,

    redis,
    mysql;
}
