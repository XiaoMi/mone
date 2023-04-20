package com.xiaomi.mone.log.server.service;

import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;
import com.xiaomi.mone.log.api.service.AgentConfigService;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wtt
 * @version 1.0
 * @description 通过dubbo接口从dashboard中获取配置
 * @date 2022/12/6 14:30
 */
@Component
@Slf4j
public class DefaultAgentConfigAcquirer implements AgentConfigAcquirer {

    @Reference(interfaceClass = AgentConfigService.class, group = "$dubbo.group", check = false, timeout = 5000)
    private AgentConfigService agentConfigService;

    @Override
    public LogCollectMeta getLogCollectMetaFromManager(String ip) {
        LogCollectMeta logCollectMeta = new LogCollectMeta();
        try {
            logCollectMeta = agentConfigService.getLogCollectMetaFromManager(ip);
        } catch (Exception e) {
            log.error("getLogCollectMetaFromManager error,ip:{}", ip, e);
        }
        return logCollectMeta;
    }
}


