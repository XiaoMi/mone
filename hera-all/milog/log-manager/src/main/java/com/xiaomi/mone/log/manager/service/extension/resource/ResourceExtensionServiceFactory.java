package com.xiaomi.mone.log.manager.service.extension.resource;

import com.xiaomi.mone.log.common.Config;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;

import static com.xiaomi.mone.log.manager.service.extension.resource.ResourceExtensionService.DEFAULT_RESOURCE_EXTENSION_SERVICE_KEY;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/4/11 10:02
 */
@Slf4j
public class ResourceExtensionServiceFactory {

    private static String factualServiceName;

    public static ResourceExtensionService getResourceExtensionService() {
        factualServiceName = Config.ins().get("store.resource.service", DEFAULT_RESOURCE_EXTENSION_SERVICE_KEY);
        log.info("ResourceExtensionServiceFactory factualServiceName:{}", factualServiceName);
        return Ioc.ins().getBean(factualServiceName);
    }
}
