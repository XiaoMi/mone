package com.xiaomi.youpin.docean.plugin.es;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@DOceanPlugin
@Slf4j
public class EsPlugin implements IPlugin {

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        Config config = ioc.getBean(Config.class);
        String address = config.get(EsConst.ES_ADDRESS, "");
        if (StringUtils.isBlank(address)) {
            log.error("no es address found");
            return;
        }
        String username = config.get(EsConst.ES_USERNAME, "");
        String password = config.get(EsConst.ES_PASSWORD, "");
        EsService esService = new EsService(address, username, password);
        ioc.putBean(esService);
    }

}
