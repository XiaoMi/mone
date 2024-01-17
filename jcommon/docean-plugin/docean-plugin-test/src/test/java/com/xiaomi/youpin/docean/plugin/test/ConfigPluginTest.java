package com.xiaomi.youpin.docean.plugin.test;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.test.config.ConfigService;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2023/12/8 13:46
 */
public class ConfigPluginTest {


    @Test
    public void testConfigPlugin() {
        Ioc ioc = Ioc.ins().init("com.xiaomi.youpin.docean.plugin.test.config","com.xiaomi.youpin.docean.plugin.config");
        ConfigService configService = ioc.getBean(ConfigService.class);
        System.out.println(configService.getVal());
        System.out.println(configService.hi());
    }

}
