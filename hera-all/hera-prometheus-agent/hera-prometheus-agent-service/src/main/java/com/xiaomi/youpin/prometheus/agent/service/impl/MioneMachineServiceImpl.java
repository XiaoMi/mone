package com.xiaomi.youpin.prometheus.agent.service.impl;

import com.xiaomi.youpin.prometheus.agent.domain.Ips;
import com.xiaomi.youpin.prometheus.agent.service.api.MioneMachineServiceExtension;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * @author zhangxiaowei6
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class MioneMachineServiceImpl implements MioneMachineServiceExtension {
    @Override
    public List<Ips> queryMachineList(String type) {
        return null;
    }
}
