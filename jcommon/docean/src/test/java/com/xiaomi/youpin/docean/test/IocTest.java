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
import com.google.gson.Gson;
import com.xiaomi.youpin.docean.Aop;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;
import com.xiaomi.youpin.docean.bo.Bean;
import com.xiaomi.youpin.docean.common.Safe;
import com.xiaomi.youpin.docean.listener.event.EventType;
import com.xiaomi.youpin.docean.test.anno.TAnno;
import com.xiaomi.youpin.docean.test.anno.TBAnno;
import com.xiaomi.youpin.docean.test.demo.Demo2Service;
import com.xiaomi.youpin.docean.test.demo.DemoA;
import com.xiaomi.youpin.docean.test.demo.DemoService;
import com.xiaomi.youpin.docean.test.demo.DemoVo;
import com.xiaomi.youpin.docean.test.demo3.ControllerDemo;
import com.xiaomi.youpin.docean.test.demo3.DaoDemo;
import com.xiaomi.youpin.docean.test.demo3.ServiceDemo;
import com.xiaomi.youpin.docean.test.factory.Abc;
import com.xiaomi.youpin.docean.test.interceptor.TAInterceptor;
import com.xiaomi.youpin.docean.test.interceptor.TBInterceptor;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
public class IocTest {


    /**
     * Remove a bean for testing.
     */
    @Test
    public void testRemoveBean() {
        ServiceDemo sd = new ServiceDemo();
        ControllerDemo cd = new ControllerDemo();

        Ioc.ins().putBean(sd).putBean(cd).putBean(new DaoDemo()).init();

        ControllerDemo controller = Ioc.ins().getBean(ControllerDemo.class);

        System.out.println(controller.call());

        Bean bean = Ioc.ins().getBeanInfo(ServiceDemo.class.getName());
        //rate limited or exceeded quota
        Map<String, Field> dependenceMap = bean.getDependenceFieldMap();

        //rate limited or exceeded quota
        Ioc.ins().removeBean(ServiceDemo.class.getName());
        //NPE
        Safe.runAndLog(() -> System.out.println(controller.call()));
        //add bean
        ServiceDemo sd2 = new ServiceDemo() {
            @Override
            public String call() {
                return "sd2";
            }
        };
        Ioc.ins().addBean(ServiceDemo.class.getName(), sd, dependenceMap);
        System.out.println(controller.call());
    }


    @Test
    public void testBeanFactory() {
        Ioc.ins().init("com.xiaomi.youpin.docean.test");
        Abc abc = Ioc.ins().getBean(Abc.class);
        System.out.println(abc);
    }


    @Test
    public void testGson() {
        Gson gson = new Gson();
        System.out.println(gson.toJson(new Bean()));
    }

    @Test
    public void testIocListener() {
        DemoA demoA = new DemoA();
        Ioc ioc = Ioc.ins().regListener(event -> {
            if (event.getEventType().equals(EventType.putBean)) {
                Bean bean = event.getData();
                System.out.println(event.getEventType() + ":" + bean.getName());
            }
        }).putBean(demoA).init();
        System.out.println(ioc);
    }


    @Test
    public void testSaveSnapshot() {
        DemoA demoA = new DemoA();
        Ioc.ins().putBean(demoA);
        DemoVo vo = new DemoVo();
        Ioc.ins().putBean(vo);
        Ioc.ins().saveSnapshot();
    }


    @Test
    public void testLoadSnapshot() {
        Ioc.ins().loadSnapshot();
        DemoA demoA = Ioc.ins().getBean(DemoA.class);
        System.out.println(demoA);
    }


    @Test
    public void testIoc6() {
        DemoA demoA = new DemoA();
        Ioc.ins().putBean(demoA);
        DemoVo vo = new DemoVo();
        Ioc.ins().putBean(vo);
        List<String> list = new ArrayList<>();
        Ioc.ins().getBeanInfos().entrySet().stream().forEach(it -> {
            System.out.println(it.getValue());
            list.add(it.getValue().getClazz().toString());
        });
        System.out.println(list);
    }


    @Test
    public void testIoc44() {
        Ioc.ins().init("com.xiaomi.youpin.docean", "run.mone");
        DemoService service = Ioc.ins().getBean("com.xiaomi.youpin.docean.test.demo.DemoService");
        System.out.println(service);
        Ioc.ins().getBeans(Bean.Type.component).forEach(it -> {
            System.out.println("component:" + it.getName());
        });
    }


    @Test
    public void testIoc() {
        LinkedHashMap<Class, EnhanceInterceptor> m = Maps.newLinkedHashMap();
        m.put(TAnno.class, new TAInterceptor());
        m.put(TBAnno.class, new TBInterceptor());
        Aop.ins().init(m);
        Ioc.ins().init("com.xiaomi.youpin.docean", "run.mone");
        DemoService service = Ioc.ins().getBean("com.xiaomi.youpin.docean.test.demo.DemoService");
        String res = service.call();
        System.out.println(res);
        String str = Ioc.ins().getBean("strBean");
        System.out.println(str);


        DemoService ds = Ioc.ins().getBean(DemoService.class);


        Demo2Service ds2 = Ioc.ins().getBean(Demo2Service.class);
        System.out.println(ds2);

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
        set.stream().forEach(it -> System.out.println(it.hi()));
    }


    @Test
    public void testIoc3() {
        Ioc.ins().putBean("com.xiaomi.youpin.docean.test.demo.DemoA:zzy", new DemoA()).init("com.xiaomi.youpin.docean");
        DemoService service = Ioc.ins().getBean(DemoService.class);
        String res = service.demoA();
        System.out.println(res);
    }

    @Test
    public void testIoc4() {
        DemoA demoA = new DemoA();
        Ioc.ins().putBean("com.xiaomi.youpin.docean.test.demo.DemoA:zzy", demoA).init("com.xiaomi.youpin.docean");
        DemoService service = Ioc.ins().getBean(DemoService.class);
        String res = service.demoA();
        System.out.println(res);
//        Bean bean = Ioc.ins().getBean(demoA);
//        String name = bean.getName();
//        System.out.println(name);
//        System.out.println(bean.getAlias());
    }
}
