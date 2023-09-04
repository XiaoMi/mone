package com.xiaomi.mone.monitor.service.api;

import com.xiaomi.mone.monitor.result.Result;

/**
 * @author zhangxiaowei6
 */
public interface PrometheusServiceExtension {
    Result queryDubboServiceList(String serviceName, String type, String startTime, String endTime);
}
