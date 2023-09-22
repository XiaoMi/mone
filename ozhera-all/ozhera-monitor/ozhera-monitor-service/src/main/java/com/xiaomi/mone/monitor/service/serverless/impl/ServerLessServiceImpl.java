package com.xiaomi.mone.monitor.service.serverless.impl;

import com.xiaomi.mone.monitor.service.serverless.ServerLessService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gaoxihui
 * @date 2023/4/20 8:27 下午
 */

@Service
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class ServerLessServiceImpl implements ServerLessService {
    @Override
    public List<String> getFaasFunctionList(Integer appId) {
        return null;
    }
}
