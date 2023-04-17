package com.xiaomi.mone.monitor.bo;

/**
 * @author gaoxihui
 * @date 2021/11/12 2:38 下午
 */
public enum AppendLabelType {
    http_include_uri,
    http_except_uri,
    http_include_errorCode,
    http_except_errorCode,
    http_client_inclue_domain,
    http_client_excpet_domain,
    dubbo_include_method,
    dubbo_except_method,
    dubbo_include_service,
    dubbo_except_service;
}
