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
