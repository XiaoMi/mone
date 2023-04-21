package com.xiaomi.mone.monitor.service.api;

import com.xiaomi.mone.monitor.result.Result;

public interface AppAlarmServiceExtension {
    Result queryFunctionList(Integer projectId);

    Result queryRulesByIamId(Integer iamId, String userName);

    Integer getAlarmIdByResult(Result result);
}
