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

package com.xiaomi.youpin.tesla.plug.test;

import com.google.common.collect.Maps;
import com.xiaomi.youpin.tesla.plug.nutz.DubboIocLoader;
import com.xiaomi.youpin.tesla.plug.nutz.DubboManager;
import com.xiaomi.youpin.tesla.plug.nutz.ResourceLoader;
import com.youpin.xiaomi.tesla.service.DubboService;
import org.junit.Test;
import org.nutz.dao.impl.NutDao;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.annotation.AnnotationIocLoader;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.ioc.loader.map.MapLoader;
import org.nutz.lang.random.R;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class NutzTest {


    @Test
    public void testLoader() {
        Map<String, Map<String, Object>> m = new HashMap<>();

        m.put("bean2", Maps.newHashMap());

        Map<String, Object> m2 = new HashMap<>();
        m2.put("args", new Object[]{"zzy"});

        Map<String, String> m3 = new HashMap<>();
        m3.put("create", "init");
        m2.put("events", m3);
        m.put("bean", m2);

        Ioc ioc = new NutIoc(new MapLoader(m));

        System.out.println(ioc.get(Bean2.class));

        Bean bean = ioc.get(Bean.class);
        System.out.println("bean2:" + bean.getBean2());
        System.out.println(bean);
        System.out.println(bean.test());
        System.out.println(ioc.get(Bean.class));


    }


    @Test
    public void testMapLoader() {
        Ioc ioc = new NutIoc(new MyIocLoader());
        IntStream.range(0,2).forEach(it->{
            Bean bean = ioc.get(Bean.class, "bean6");
            System.out.println(bean.test());
            System.out.println(ioc.get(Bean2.class,"bean5"));
        });
    }


    @Test
    public void testUU32() {
        String name = R.UU32();
        System.out.println(name);
    }


    @Test
    public void testMyLoader() {
        Ioc ioc = new NutIoc(new MyIocLoader());
        Bean bean = ioc.get(Bean.class, "bean");
        System.out.println(bean);
        System.out.println(bean.test());
    }

    @Test
    public void testResourceLoader() {
        Ioc ioc = new NutIoc(new ResourceLoader());
        NutDao bean = ioc.get(NutDao.class, "dao");
        System.out.println(bean);
    }

    @Test
    public void testMyLoader2() {
        Ioc ioc = new NutIoc(new MyIocLoader());
        Bean bean = ioc.get(Bean.class, "bean2");
        System.out.println(bean.test());
        System.out.println(bean.test());
    }


    @Test
    public void testBaba() {
        Ioc ioc = new NutIoc(new MyIocLoader());
        Bean bean = ioc.get(Bean.class, "bean4");
        System.out.println(bean.test());
    }


    @Test
    public void testDubbo() throws InterruptedException {
        Ioc ioc = new NutIoc(new ComboIocLoader(new AnnotationIocLoader("com.xiaomi.youpin.tesla.plug"), new DubboIocLoader(new String[]{"com.xiaomi.youpin.tesla.plug"})));

        DubboManager manager = ioc.get(DubboManager.class);
        System.out.println(manager);


//        DubboService s = ioc.get(DubboService.class);

        TimeUnit.HOURS.sleep(1);
    }
}
