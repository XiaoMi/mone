package com.xiaomi.mone.log.manager.service.extension.dictionary;

import com.xiaomi.mone.log.common.Config;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;

import static com.xiaomi.mone.log.manager.service.extension.dictionary.DictionaryExtensionService.DEFAULT_DICTIONARY_EXTENSION_SERVICE_KEY;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/4/12 10:40
 */
@Slf4j
public class DictionaryExtensionServiceFactory {

    private static String factualServiceName;

    public static DictionaryExtensionService getDictionaryExtensionService() {
        factualServiceName = Config.ins().get("directory.extension.service", DEFAULT_DICTIONARY_EXTENSION_SERVICE_KEY);
        log.info("DictionaryExtensionServiceFactory factualServiceName:{}", factualServiceName);
        return Ioc.ins().getBean(factualServiceName);
    }
}
