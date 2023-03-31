package com.xiaomi.youpin.prometheus.agent.service;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.youpin.prometheus.agent.domain.Ips;
//import com.xiaomi.youpin.quota.bo.ResourceBo;
//import com.xiaomi.youpin.quota.bo.Result;
//import com.xiaomi.youpin.quota.service.QuotaService;
//import com.xiaomi.youpin.quota.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class MioneMachineService {

//    @NacosValue(value = "${mione.machine.port}", autoRefreshed = true)
//    private String machinePort;
//
//    @NacosValue(value = "${mione.container.port}", autoRefreshed = true)
//    private String containerPort;

    /*@Reference(interfaceClass = QuotaService.class, group = "${ref.quota.service.group}", check = false)
    private QuotaService quotaService;

    @Reference(interfaceClass = ResourceService.class, group = "${ref.quota.service.group}", check = false)
    private ResourceService resourceService;*/

    public List<Ips> queryMachineList(String type) {
       /* List<String> result = new ArrayList<>();
        List<String> ips = new ArrayList<>();
        try {
            Result<List<ResourceBo>> resourceResult = resourceService.list();
            List<ResourceBo> data = resourceResult.getData();
            data.forEach((resource) -> {
                if (resource.getLables() != null && Objects.equals(resource.getLables().get("type"), "docker")) {
                    ips.add(resource.getIp());
                }
            });
            if (ips.size() > 0) {
                for (String ip : ips) {
                    if ("1".equals(type)) {
                        result.add(ip + ":" + machinePort);
                    } else {
                        result.add(ip + ":" + containerPort);
                    }
                }
            }
        } catch (Exception e) {
            log.error("quotaService.resourceService接口失败：", e);
        }
        List<Ips> defaultResult = new ArrayList<>();
        Ips ips2 = new Ips();
        ips2.setTargets(result);
        defaultResult.add(ips2);
        return defaultResult;
*/
        return null;
    }
}
