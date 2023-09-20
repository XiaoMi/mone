package com.xiaomi.youpin.docean.plugin.test;

import com.google.common.collect.Maps;
import com.xiaomi.youpin.docean.Aop;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.test.lookup.LookupService;
import org.junit.Test;

import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2023/9/20 13:55
 */
public class IocTest {


    @Test
    public void testLookup() {
        Aop.ins().init(Maps.newLinkedHashMap());
        Ioc.ins().init("com.xiaomi.youpin.docean.plugin.test.lookup", "com.xiaomi.youpin.docean.plugin.config");
        LookupService ds = Ioc.ins().getBean(LookupService.class);
        IntStream.range(0, 5).forEach(i -> {
            com.xiaomi.youpin.docean.plugin.test.bo.Test dv = ds.g();
            dv.setId(123);
            System.out.println(dv.getId());
        });
    }

}
