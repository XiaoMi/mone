package com.xiaomi.mone.monitor.service.api;

import com.xiaomi.mone.monitor.result.Result;

public interface AlarmServiceExtension {

    Result<String> getGroup(Integer iamId, String user);
}
