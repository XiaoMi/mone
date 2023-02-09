package com.xiaomi.youpin.docean.plugin.test;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.config.Config;
import com.xiaomi.youpin.docean.plugin.test.service.SpringHealth2Service;
import com.xiaomi.youpin.docean.plugin.test.service.HealthService;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2022/7/12 22:40
 */
public class SpringPluginTest {


    @Test
    public void testSpringPlugin() {
        Config config = new Config();
        config.put("nnn", "123");
        Ioc ioc = Ioc.ins().putBean(config).init("run.mone.docean", "com.xiaomi.youpin.docean");
        System.out.println(ioc);
        HealthService hs = ioc.getBean(HealthService.class);
        //System.out.println(hs.test());
        System.out.println(hs);
        SpringHealth2Service hs2 = ioc.getBean(SpringHealth2Service.class.getName(), null);
        System.out.println(hs2);
        System.out.println(hs2.hi());
        ioc.destory();
        System.out.println("finish");
    }
}
