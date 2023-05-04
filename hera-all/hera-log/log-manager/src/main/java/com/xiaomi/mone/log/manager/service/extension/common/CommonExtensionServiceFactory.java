package com.xiaomi.mone.log.manager.service.extension.common;

import com.xiaomi.mone.log.common.Config;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;

import static com.xiaomi.mone.log.manager.service.extension.common.CommonExtensionService.DEFAULT_COMMON_EXTENSION_SERVICE_KEY;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/4/27 16:16
 */
@Slf4j
public class CommonExtensionServiceFactory {

    private static String factualServiceName;

    public static CommonExtensionService getCommonExtensionService() {
        factualServiceName = Config.ins().get("common.extension.service", DEFAULT_COMMON_EXTENSION_SERVICE_KEY);
        log.info("DictionaryExtensionServiceFactory factualServiceName:{}", factualServiceName);
        return Ioc.ins().getBean(factualServiceName);
    }
}
