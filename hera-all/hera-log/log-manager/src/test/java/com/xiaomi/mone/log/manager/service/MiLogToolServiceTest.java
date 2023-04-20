package com.xiaomi.mone.log.manager.service;

import com.google.gson.Gson;
import com.xiaomi.mone.log.manager.service.impl.MiLogToolServiceImpl;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/2 21:34
 */
@Slf4j
public class MiLogToolServiceTest {

    private MiLogToolServiceImpl miLogToolService;
    private Gson gson = new Gson();

    @Before
    public void buildBean() {
        Ioc.ins().init("com.xiaomi");
        miLogToolService = Ioc.ins().getBean(MiLogToolServiceImpl.class);
    }

    @Test
    public void testDubbo(){
        miLogToolService.fixLogTailLogAppId("zzytest");
    }
}
