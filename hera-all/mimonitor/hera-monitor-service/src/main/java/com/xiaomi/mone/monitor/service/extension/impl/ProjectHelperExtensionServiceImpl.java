package com.xiaomi.mone.monitor.service.extension.impl;

import com.xiaomi.mone.monitor.service.extension.ProjectHelperExtensionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/4/20 18:44
 */
@Service
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class ProjectHelperExtensionServiceImpl implements ProjectHelperExtensionService {
    @Override
    public boolean accessLogSys(String projectName, Long projectId, Integer appSource) {
        return true;
    }
}
