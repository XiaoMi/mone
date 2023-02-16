package com.xiaomi.mone.log.manager.domain;

import com.xiaomi.youpin.docean.Ioc;
import org.junit.Test;

public class LogTemplateTest {
    @Test
    public void createAppTemplate() {
        Ioc.ins().init("com.xiaomi");
        LogTemplate logTemplate = Ioc.ins().getBean(LogTemplate.class);
        logTemplate.createAppTemplate();
    }
}