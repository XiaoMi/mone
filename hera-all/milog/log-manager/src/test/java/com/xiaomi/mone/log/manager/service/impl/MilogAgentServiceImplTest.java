package com.xiaomi.mone.log.manager.service.impl;

import com.google.gson.Gson;
import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;
import com.xiaomi.mone.log.api.service.AgentConfigService;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;

@Slf4j
public class MilogAgentServiceImplTest {
    @Test
    public void getList() {
        Ioc.ins().init("com.xiaomi");
        MilogAgentServiceImpl milogAgentService = Ioc.ins().getBean(MilogAgentServiceImpl.class);
    }

    @Test
    public void testConfigIssueAgent() {
        Ioc.ins().init("com.xiaomi");
        MilogAgentServiceImpl milogAgentService = Ioc.ins().getBean(MilogAgentServiceImpl.class);
        milogAgentService.configIssueAgent("1", "127.0.0.1", "etret");
    }

    @Test
    public void testMan() {
        Ioc.ins().init("com.xiaomi");
        MilogAgentServiceImpl milogAgentService = Ioc.ins().getBean(MilogAgentServiceImpl.class);
        milogAgentService.publishIncrementConfig(14L, 4L, Arrays.asList("127.0.0.1"));
    }


    /**
     * 测试删除配置-通知log-agent停止收集
     */
    @Test
    public void testDelConfigStopColl() {
        Ioc.ins().init("com.xiaomi");
        MilogAgentServiceImpl milogAgentService = Ioc.ins().getBean(MilogAgentServiceImpl.class);
        milogAgentService.publishIncrementDel(79L, 667L, null);
    }

    @Test
    public void process1() {
        Ioc.ins().init("com.xiaomi");
        AgentConfigService agentConfigService = Ioc.ins().getBean(AgentConfigServiceImpl.class);
        LogCollectMeta logCollectMeta = agentConfigService.getLogCollectMetaFromManager("127.0.0.1");
        String responseInfo = new Gson().toJson(logCollectMeta);
        log.info("agent启动获取配置,获取到的配置信息:{}", responseInfo);
    }

}