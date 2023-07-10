package com.xiaomi.mone.monitor.service.api;

import com.xiaomi.mone.monitor.pojo.ReqSlowMetricsPOJO;

public interface ReqSlowMetricsService {

    ReqSlowMetricsPOJO getSlowMetricsByMetric(String metrics);
}
