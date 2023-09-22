package com.xiaomi.mone.monitor.service;

import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.api.AppAlarmServiceExtension;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/21 2:53 PM
 */
@Service
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class AppAlarmServiceExtensionImpl implements AppAlarmServiceExtension {
    @Override
    public Result queryFunctionList(Integer projectId) {
        return null;
    }

    @Override
    public Result queryRulesByIamId(Integer iamId, String userName) {
        return null;
    }

    @Override
    public Integer getAlarmIdByResult(Result result) {
        Double alarmId = (Double) result.getData();
        return alarmId == null ? null : alarmId.intValue();
    }
}
