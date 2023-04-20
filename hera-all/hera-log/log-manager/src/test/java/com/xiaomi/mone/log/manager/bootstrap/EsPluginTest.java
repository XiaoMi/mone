package com.xiaomi.mone.log.manager.bootstrap;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.es.EsService;
import org.junit.Before;
import org.junit.Test;

public class EsPluginTest {

    @Before
    public void beforeFunc() {
        Ioc.ins().init("com.xiaomi");
    }

    @Test
    public void init() {
        EsService cn = (EsService)Ioc.ins().getBean("CN");
        EsService info = (EsService)Ioc.ins().getBean("INFO");
        System.out.println("");
    }
}