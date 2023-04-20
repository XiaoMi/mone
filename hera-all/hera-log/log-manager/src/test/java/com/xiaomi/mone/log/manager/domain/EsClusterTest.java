package com.xiaomi.mone.log.manager.domain;

import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.youpin.docean.Ioc;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EsClusterTest {

    private EsCluster esCluster;

    @Before
    public void pushBean() {
        Ioc.ins().init("com.xiaomi");
        esCluster = Ioc.ins().getBean(EsCluster.class);
    }

    @Test
    public void getByRegion() {
        Assert.assertNull(esCluster.getByRegion(""));
        Assert.assertNull(esCluster.getByRegion(null));
        System.out.println(esCluster.getByRegion(Constant.ES_REGION_EROUP));
    }
}