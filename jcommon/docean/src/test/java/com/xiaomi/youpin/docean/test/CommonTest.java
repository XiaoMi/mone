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

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.bo.Bean;
import com.xiaomi.youpin.docean.common.ReflectUtils;
import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.beans.BeanMap;
import net.sf.cglib.proxy.Mixin;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author goodjava@qq.com
 * @date 2020/6/22
 */
public class CommonTest {


    @Test
    public void testBeanGenerator() throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        BeanGenerator beanGenerator = new BeanGenerator();
        beanGenerator.setSuperclass(C.class);
        beanGenerator.addProperty("value", String.class);
        Object myBean = beanGenerator.create();

        Method setter = myBean.getClass().getMethod("setValue", String.class);
        setter.invoke(myBean, "Hello cglib!");
        Method getter = myBean.getClass().getMethod("getValue");
        System.out.println(getter.invoke(myBean));


        System.out.println(myBean.getClass().getMethod("c").invoke(myBean));
    }

    @Test
    public void testMin() {
        Object o = Mixin.create(new Class[]{Ic.class, Id.class}, new Object[]{new C(), new D()});
        Ic ic = (Ic) o;
        Id id = (Id) o;
        System.out.println(ic.c() + ":" + id.d());
    }


    @Test
    public void testMap() {
        Map<String, String> m = new HashMap<>();
        m.put("a", "a");
        m.put("b", "b");
        m.values().stream().collect(Collectors.toList()).forEach(it -> {
            m.remove(it);
        });

        System.out.println(m);
    }


    @Test
    public void testJsonArray() {
        JsonArray array = new JsonArray();
        array.add("1");
        array.add("2");
        System.out.println(array);

        JsonArray arrray2 = new JsonArray();
        arrray2.add("0");
        arrray2.add(array);
        System.out.println(arrray2);
    }


    static interface Ic {
        String c();
    }


    @Service(desc = "test")
    @Component
    public static class C implements Ic {
        public String c() {
            return "c";
        }
    }


    static interface Id {
        String d();
    }

    public static class D implements Id {
        public String d() {
            return "d";
        }
    }


    @Test
    public void testAnn() {
        Annotation[] ans = C.class.getAnnotations();
        System.out.println(ans.length);
        boolean find = Arrays.stream(ans).filter(it -> it.annotationType().equals(Service.class)).findAny().isPresent();
        System.out.println(find);

        if (ans.length > 0) {
            Object desc = ReflectUtils.invokeMethod(ans[0], "desc", new Object[]{});
            System.out.println(desc);
        }
    }


    @Test
    public void testStream() {
        List<Integer> l = Lists.newArrayList(1, 2, 3);
        System.out.println(l);
    }


    @Test
    public void testSort() {
        List<Bean> list = Lists.newArrayList();
        Bean b = new Bean();
        b.setType(2);
        b.setReferenceCnt(8);
        list.add(b);


        Bean b2 = new Bean();
        b2.setType(1);
        b2.setReferenceCnt(1);
        list.add(b2);

        System.out.println(list.stream().sorted(Bean::compareTo).collect(Collectors.toList()));
    }
}
