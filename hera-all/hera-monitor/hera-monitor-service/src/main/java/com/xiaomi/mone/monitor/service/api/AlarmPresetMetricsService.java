package com.xiaomi.mone.monitor.service.api;

import com.xiaomi.mone.monitor.bo.MetricsRule;
import com.xiaomi.mone.monitor.pojo.AlarmPresetMetricsPOJO;

import java.util.List;
import java.util.Map;

public interface AlarmPresetMetricsService {

    List<MetricsRule> getEnumList();

    AlarmPresetMetricsPOJO getByCode(String code);

    Map<String, String> getEnumMap();
}
