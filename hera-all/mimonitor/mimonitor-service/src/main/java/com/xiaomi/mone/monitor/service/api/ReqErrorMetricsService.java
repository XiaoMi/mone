package com.xiaomi.mone.monitor.service.api;

import com.xiaomi.mone.monitor.pojo.ReqErrorMetricsPOJO;

public interface ReqErrorMetricsService {

    ReqErrorMetricsPOJO getErrorMetricsByMetrics(String metrics);
}
