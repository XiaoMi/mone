package com.xiaomi.mone.log.manager.domain.analyse;

import com.xiaomi.mone.log.manager.domain.EsCluster;
import com.xiaomi.youpin.docean.Ioc;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

import java.io.IOException;

import static org.junit.Assert.*;

public class TopTest {
    @Resource
    private Top top;

    @Before
    public void pushBean() {
        Ioc.ins().init("com.xiaomi");
        top = Ioc.ins().getBean(Top.class);
    }

    @Test
    public void caclulate() throws IOException {
        String storeName = "milog_store";
        top.caclulate(storeName);
    }
}