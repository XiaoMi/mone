package com.xiaomi.youpin.docean.plugin.test;

import org.junit.Test;

import java.util.Arrays;

/**
 * @author goodjava@qq.com
 * @date 2022/7/12 22:16
 */
public class ReflectTest {

    class RT {
        int a;
        String b;
    }


    @Test
    public void testReflect() {
        Arrays.stream(RT.class.getDeclaredFields()).forEach(f->{
            System.out.println(f.getType().getName());
        });
    }

}
