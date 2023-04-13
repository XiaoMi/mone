package com.xiaomi.mone.log.manager.service.extension.agent;

import com.xiaomi.mone.log.common.Config;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;

import static com.xiaomi.mone.log.manager.service.extension.agent.MilogAgentService.DEFAULT_AGENT_EXTENSION_SERVICE_KEY;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/4/10 21:01
 */
@Slf4j
public class MilogAgentServiceFactory {

    private static String factualServiceName;

    public static MilogAgentService getAgentExtensionService() {
        factualServiceName = Config.ins().get("agent.extension.service", DEFAULT_AGENT_EXTENSION_SERVICE_KEY);
        log.info("StoreExtensionServiceFactory factualServiceName:{}", factualServiceName);
        return Ioc.ins().getBean(factualServiceName);
    }
}
