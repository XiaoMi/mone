package com.xiaomi.mone.monitor.service.api;

import com.xiaomi.mone.monitor.service.model.AppMonitorRequest;
import com.xiaomi.mone.monitor.service.model.prometheus.MetricKind;
import com.xiaomi.mone.monitor.service.model.redis.AppAlarmData;

public interface ComputeTimerServiceExtension {

    void computByMetricType(AppMonitorRequest param, String appName, MetricKind metricKind, AppAlarmData.AppAlarmDataBuilder dataBuilder, Long startTime, Long endTime, String timeDurarion, Long step);
}
