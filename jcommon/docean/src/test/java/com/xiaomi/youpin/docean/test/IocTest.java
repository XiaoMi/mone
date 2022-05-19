package com.xiaomi.youpin.docean.test;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.youpin.docean.Aop;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;
import com.xiaomi.youpin.docean.bo.Bean;
import com.xiaomi.youpin.docean.listener.event.EventType;
import com.xiaomi.youpin.docean.test.anno.TAnno;
import com.xiaomi.youpin.docean.test.anno.TBAnno;
import com.xiaomi.youpin.docean.test.demo.DemoA;
import com.xiaomi.youpin.docean.test.demo.DemoService;
import com.xiaomi.youpin.docean.test.demo.DemoVo;
import com.xiaomi.youpin.docean.test.interceptor.TAInterceptor;
import com.xiaomi.youpin.docean.test.interceptor.TBInterceptor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
public class IocTest {


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
                System.out.println(event.getEventType()+":"+ bean.getName());
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
        Ioc.ins().getBeanInfos().entrySet().stream().forEach(it->{
            System.out.println(it.getValue());
            list.add(it.getValue().getClazz().toString());
        });
        System.out.println(list);
    }

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
