package com.xiaomi.mone.log.manager.domain.analyse;

import com.xiaomi.youpin.docean.Ioc;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.IOException;

public class TopTest {
    @Resource
    private FieldStrategy fieldStrategy;

    @Resource
    private DateGroupStrategy dateGroupStrategy;

    @Before
    public void pushBean() {
        Ioc.ins().init("com.xiaomi");
        dateGroupStrategy = Ioc.ins().getBean(DateGroupStrategy.class);
    }

    @Test
    public void caclulate() throws IOException {
        String storeName = "nr-pay";
//        dateGroupStrategy.caclulate(storeName);
    }
}