package com.xiaomi.mone.log.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;
import com.xiaomi.mone.log.server.service.DefaultAgentConfigAcquirer;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/12/21 10:56
 */
@Slf4j
public class DefaultAgentConfigAcquirerTest {

    DefaultAgentConfigAcquirer configAcquirer;
    Gson gson;

    @Before
    public void buildBean() {
        Ioc.ins().init("com.xiaomi");
        configAcquirer = Ioc.ins().getBean(DefaultAgentConfigAcquirer.class);
        gson = new GsonBuilder().create();
    }

    @Test
    public void testGetConfig() {
        String ip = "127.0.0.1:1";
        LogCollectMeta logCollectMetaFromManager = configAcquirer.getLogCollectMetaFromManager(ip);
        log.info("config:{}", gson.toJson(logCollectMetaFromManager));
    }
}
