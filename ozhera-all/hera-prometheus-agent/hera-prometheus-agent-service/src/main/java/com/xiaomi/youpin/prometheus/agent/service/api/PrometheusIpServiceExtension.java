package com.xiaomi.youpin.prometheus.agent.service.api;

import com.xiaomi.youpin.prometheus.agent.domain.Ips;

import java.util.List;
import java.util.Set;

/**
 * @author zhangxiaowei6
 */

public interface PrometheusIpServiceExtension {
    List<Ips> getByType(String type);

    Set<String> getIpsByAppName(String name);

    Set<String> getEtcdHosts();

    List<Ips> getK8sNodeIp(String type);
}
