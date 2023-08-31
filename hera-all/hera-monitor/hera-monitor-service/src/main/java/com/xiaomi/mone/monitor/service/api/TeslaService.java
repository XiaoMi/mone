package com.xiaomi.mone.monitor.service.api;

import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.model.prometheus.AlarmRuleData;

public interface TeslaService {

    String getTeslaTimeCost4P99(AlarmRuleData rule);

    String getTeslaAvailability(AlarmRuleData rule);

    void checkTeslaMetrics(StringBuilder title, String alert);

    Result getTeslaAlarmHealthByUser(String user);
}
