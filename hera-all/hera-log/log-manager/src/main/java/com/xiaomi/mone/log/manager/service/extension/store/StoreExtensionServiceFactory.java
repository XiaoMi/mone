package com.xiaomi.mone.log.manager.service.extension.store;

import com.xiaomi.mone.log.common.Config;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;

import static com.xiaomi.mone.log.manager.service.extension.store.StoreExtensionService.DEFAULT_STORE_EXTENSION_SERVICE_KEY;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/4/10 16:41
 */
@Slf4j
public class StoreExtensionServiceFactory {

    private static String factualServiceName;

    public static StoreExtensionService getStoreExtensionService() {
        factualServiceName = Config.ins().get("store.extension.service", DEFAULT_STORE_EXTENSION_SERVICE_KEY);
        log.info("StoreExtensionServiceFactory factualServiceName:{}", factualServiceName);
        return Ioc.ins().getBean(factualServiceName);
    }
}
