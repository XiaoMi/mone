package com.xiaomi.mone.log.manager.service.extension.tail;

import com.xiaomi.mone.log.common.Config;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;

import static com.xiaomi.mone.log.manager.service.extension.tail.TailExtensionService.DEFAULT_TAIL_EXTENSION_SERVICE_KEY;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/4/10 17:15
 */
@Slf4j
public class TailExtensionServiceFactory {

    private static String factualServiceName;

    public static TailExtensionService getTailExtensionService() {
        factualServiceName = Config.ins().get("tail.extension.service", DEFAULT_TAIL_EXTENSION_SERVICE_KEY);
        log.info("TailExtensionServiceFactory factualServiceName:{}", factualServiceName);
        return Ioc.ins().getBean(factualServiceName);
    }
}
