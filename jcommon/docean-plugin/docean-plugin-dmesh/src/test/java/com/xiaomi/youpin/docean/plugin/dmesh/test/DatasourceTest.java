package com.xiaomi.youpin.docean.plugin.dmesh.test;

import com.xiaomi.youpin.docean.plugin.dmesh.ds.Datasource;
import org.junit.Test;

public class DatasourceTest {


    @Test
    public void test1() {
        Datasource ds = new Datasource();
        ds.set("defaultMaxPoolSize","100");
        System.out.println(ds);
    }
}
