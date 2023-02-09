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

import com.xiaomi.youpin.docean.common.ReflectUtils;
import com.xiaomi.youpin.docean.test.demo.DemoController;
import com.xiaomi.youpin.docean.test.demo.DemoService;
import org.junit.Test;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
public class ReflectUtilsTest {


    @Test
    public void testFields() {
        Field[] fields = ReflectUtils.fields(DemoService.class);
        Arrays.stream(fields).forEach(it -> {
            Resource r = it.getAnnotation(Resource.class);
            System.out.println(r);
            if (null != r) {
                Arrays.stream(r.getClass().getMethods()).forEach(it2 -> {
                    System.out.println(it2.getName());
                });
            }
        });
    }


    class Cl {
        public String m(String str) {
            return "m" + str;
        }
    }


    @Test
    public void testInvokeMethod() {
        Cl cl = new Cl();
        long b = System.currentTimeMillis();
        IntStream.range(0, 20000000).forEach(it -> {
            Object r = ReflectUtils.invokeMethod(cl, cl.getClass(), "m", new Object[]{"a"});
//            System.out.println(r);
        });
        System.out.println(System.currentTimeMillis()-b);

        b = System.currentTimeMillis();
        IntStream.range(0, 20000000).forEach(it -> {
            Object r = ReflectUtils.invokeFastMethod(cl, cl.getClass(), "m", new Object[]{"a"});
//            System.out.println(r);
        });
        System.out.println(System.currentTimeMillis()-b);
    }


    @Test
    public void testController() {
        Arrays.stream(DemoController.class.getMethods()).forEach(it -> {
            System.out.println("------" + it);
            Class<?>[] types = it.getParameterTypes();
            Arrays.stream(types).forEach(it2 -> {
                System.out.println(it2);
            });
        });
    }

    @Test
    public void testController2() {
        DemoController c = new DemoController();
//        Object[] res = ReflectUtils.getMethodParams(c, "test2", Lists.newArrayList("{\"id\":\"123\"}"));
//        System.out.println(Arrays.toString(res));
    }
}
