package com.xiaomi.youpin.prometheus.agent.service.api;

import com.xiaomi.youpin.prometheus.agent.domain.Ips;

import java.util.List;

/**
 * @author zhangxiaowei6
 */

public interface MioneMachineServiceExtension {
    List<Ips> queryMachineList(String type);
}
