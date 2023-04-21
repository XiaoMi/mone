package com.xiaomi.mone.monitor.service.api;

import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.model.ProjectInfo;

import java.util.List;

/**
 * @author zhangxiaowei6
 */
public interface AppMonitorServiceExtension {

    Result getResourceUsageUrlForK8s(Integer appId, String appName);

    Result getResourceUsageUrl(Integer appId, String appName) ;

    Result grafanaInterfaceList();

    Result initAppsByUsername(String userName);

    List<ProjectInfo> getAppsByUserName(String username);
}
