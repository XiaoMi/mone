package com.xiaomi.mone.log.manager.domain;

import com.xiaomi.youpin.docean.Ioc;
import org.junit.Before;
import org.junit.Test;


public class IDMDeptTest {
    private IDMDept idmDept;

    @Before
    public void pushBean() {
        Ioc.ins().init("com.xiaomi");
        idmDept = Ioc.ins().getBean(IDMDept.class);
    }

    @Test
    public void refreshDeptCache() {
        long start = System.currentTimeMillis();
        System.out.println("======================================");
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(idmDept.getDeptCache());
    }
}