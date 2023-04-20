package com.xiaomi.mone.monitor.service.api;

import com.xiaomi.mone.monitor.result.Result;

/**
 * @author zhangxiaowei6
 */
public interface AppMonitorServiceExtension {
    Result getResourceUsageUrlForK8s(Integer appId, String appName);

    Result grafanaInterfaceList();
}
