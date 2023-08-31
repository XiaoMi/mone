package com.xiaomi.mone.monitor.service;

import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.api.TeslaService;
import com.xiaomi.mone.monitor.service.model.prometheus.AlarmRuleData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/20 5:07 PM
 */
@Service
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class TeslaServiceImpl implements TeslaService {
    @Override
    public String getTeslaTimeCost4P99(AlarmRuleData rule) {
        return null;
    }

    @Override
    public String getTeslaAvailability(AlarmRuleData rule) {
        return null;
    }

    @Override
    public void checkTeslaMetrics(StringBuilder title, String alert) {

    }

    @Override
    public Result getTeslaAlarmHealthByUser(String user) {
        return null;
    }

}
