package com.xiaomi.mone.log.manager.service.impl;

import com.google.gson.Gson;
import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;
import com.xiaomi.mone.log.api.service.AgentConfigService;
import com.xiaomi.mone.log.manager.service.extension.agent.MilogAgentService;
import com.xiaomi.mone.log.manager.service.extension.agent.MilogAgentServiceFactory;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/8/4 19:42
 */
@Slf4j
@Service
@com.xiaomi.youpin.docean.plugin.dubbo.anno.Service(interfaceClass = AgentConfigService.class, group = "$dubbo.env.group")
public class AgentConfigServiceImpl implements AgentConfigService {

    private MilogAgentService milogAgentService;

    public void init() {
        milogAgentService = MilogAgentServiceFactory.getAgentExtensionService();
    }

    /**
     * 1.查询出该物理机下的接入过日志的所有应用
     * 2.封装信息
     *
     * @param ip
     * @return
     */
    @Override
    public LogCollectMeta getLogCollectMetaFromManager(String ip) {
        log.info("getLogCollectMetaFromManager begin:{}", ip);
        try {
            long begin = System.currentTimeMillis();
            if (null == milogAgentService) {
                init();
            }
            LogCollectMeta logCollectMeta = milogAgentService.getLogCollectMetaFromManager(ip);
            log.info("getLogCollectMetaFromManager end:{} {} {}", ip, new Gson().toJson(logCollectMeta), (System.currentTimeMillis() - begin));
            return logCollectMeta;
        } catch (Exception e) {
            log.error("getLogCollectMetaFromManager error,ip:{}", ip, e);
        }
        return new LogCollectMeta();
    }

}
