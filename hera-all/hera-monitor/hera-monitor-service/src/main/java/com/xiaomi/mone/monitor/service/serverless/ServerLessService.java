package com.xiaomi.mone.monitor.service.serverless;

import com.xiaomi.mone.monitor.bo.MetricsRule;
import com.xiaomi.mone.monitor.pojo.AlarmPresetMetricsPOJO;

import java.util.List;
import java.util.Map;

public interface ServerLessService {

    List<String> getFaasFunctionList(Integer appId);

}
