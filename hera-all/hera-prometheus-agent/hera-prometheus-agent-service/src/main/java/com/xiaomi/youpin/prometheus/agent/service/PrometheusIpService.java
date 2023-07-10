package com.xiaomi.youpin.prometheus.agent.service;

import com.xiaomi.youpin.prometheus.agent.domain.Ips;
import com.xiaomi.youpin.prometheus.agent.service.api.PrometheusIpServiceExtension;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class PrometheusIpService {

    @Autowired
    private PrometheusIpServiceExtension prometheusIpServiceExtension;

    public List<Ips> getByType(String type) {
        return prometheusIpServiceExtension.getByType(type);
    }

    public Set<String> getIpsByAppName(String name) {
        return prometheusIpServiceExtension.getIpsByAppName(name);
    }

    public Set<String> getEtcdHosts() {
        return prometheusIpServiceExtension.getEtcdHosts();
    }


    public List<Ips> getK8sNodeIp(String type) {
        return prometheusIpServiceExtension.getK8sNodeIp(type);
    }

}
