/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.docean.test;

import com.google.common.collect.Maps;
import com.xiaomi.youpin.docean.Aop;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;
import com.xiaomi.youpin.docean.test.anno.TAnno;
import com.xiaomi.youpin.docean.test.demo.DemoDao;
import com.xiaomi.youpin.docean.test.demo.IDemoDao;
import com.xiaomi.youpin.docean.test.demo2.DemoService2;
import com.xiaomi.youpin.docean.test.interceptor.TAInterceptor;
import org.junit.Test;

import java.util.LinkedHashMap;

/**
 * @author goodjava@qq.com
 * @date 2020/6/21
 */
public class AopTest {


    /**
     * The bottom layer uses cglib enhancement or dynamic proxy.
     */
    @Test
    public void testAop() {
        LinkedHashMap<Class, EnhanceInterceptor> m = Maps.newLinkedHashMap();
        m.put(TAnno.class, new TAInterceptor());
        Aop.ins().init(m);
        IDemoDao d = Aop.ins().enhance(DemoDao.class, m);
        String res = d.get();
        System.out.println(res);
    }


    @Test
    public void testAopAnno() {
        Aop.ins().useAspect(Ioc.ins(), "com.xiaomi.youpin.docean.test");
    }

    @Test
    public void testAopAnno2() {
        Ioc ioc = Ioc.ins();
        Aop.ins().useAspect(ioc, "com.xiaomi.youpin.docean.test");
        DemoService2 ds = ioc.init("com.xiaomi.youpin.docean.test").getBean(DemoService2.class);
        System.out.println(ds.hi("zzy", "lucy"));
    }

}
