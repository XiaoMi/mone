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
import com.xiaomi.youpin.docean.test.anno.TBAnno;
import com.xiaomi.youpin.docean.test.demo.DemoService;
import com.xiaomi.youpin.docean.test.demo.DemoVo;
import com.xiaomi.youpin.docean.test.interceptor.TAInterceptor;
import com.xiaomi.youpin.docean.test.interceptor.TBInterceptor;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
public class IocTest {

    @Test
    public void testIoc() {
        LinkedHashMap<Class, EnhanceInterceptor> m = Maps.newLinkedHashMap();
        m.put(TAnno.class, new TAInterceptor());
        m.put(TBAnno.class, new TBInterceptor());
        Aop.ins().init(m);
        Ioc.ins().init("com.xiaomi.youpin.docean");
        DemoService service = Ioc.ins().getBean("com.xiaomi.youpin.docean.test.demo.DemoService");
        String res = service.call();
        System.out.println(res);
        String str = Ioc.ins().getBean("strBean");
        System.out.println(str);


        DemoService ds = Ioc.ins().getBean(DemoService.class);

        DemoVo v1 = ds.demoVo();
        v1.setName("zzy");

        System.out.println(v1.getName());
        System.out.println(ds.demoVo().getName());


        DemoVo dv = Ioc.ins().createBean(DemoVo.class);
        System.out.println(dv);

        Ioc.ins().destory();
        System.out.println("finish");
    }

    interface IA {
        String hi();
    }

    class A1 implements IA {

        @Override
        public String hi() {
            return "1";
        }
    }

    class A2 implements IA {

        @Override
        public String hi() {
            return "2";
        }
    }


    @Test
    public void testIoc2() {
        Ioc.ins().putBean(new A1()).putBean(new A2());
        Set<IA> set = Ioc.ins().getBeans(IA.class);
        System.out.println(set);
        set.stream().forEach(it->System.out.println(it.hi()));
    }
}
