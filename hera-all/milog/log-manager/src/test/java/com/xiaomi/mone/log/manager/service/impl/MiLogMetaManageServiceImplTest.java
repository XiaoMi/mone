package com.xiaomi.mone.log.manager.service.impl;

import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;
import com.xiaomi.youpin.docean.Ioc;
import org.junit.Test;

public class MiLogMetaManageServiceImplTest {

    @Test
    public void queryLogCollectMeta() {
        Ioc.ins().init("com.xiaomi");
        MiLogMetaManageServiceImpl miLogMetaManageService = Ioc.ins().getBean(MiLogMetaManageServiceImpl.class);
        LogCollectMeta logCollectMeta = miLogMetaManageService.queryLogCollectMeta("", "127.0.0.1");
        System.out.println(logCollectMeta);
    }
}