package com.xiaomi.youpin.docean.test;

import com.google.common.base.Stopwatch;
import com.xiaomi.youpin.docean.common.ReflectUtils;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/29 15:19
 */
public class ReflectUtilsTest {

    //3000
    @Test
    public void testInvokerFastMethod() {
        M m = new M();
        Stopwatch sw = Stopwatch.createStarted();
        IntStream.range(0, 10000000).forEach(it -> {
            int r = (int) ReflectUtils.invokeFastMethod(m, m.getClass(), "sum", new Object[]{1, 2});
//            System.out.println(r);
        });
        System.out.println(sw.elapsed(TimeUnit.MILLISECONDS));
    }


    //4184
    @Test
    public void testInvokerMethod() {
        M m = new M();
        Stopwatch sw = Stopwatch.createStarted();
        IntStream.range(0, 10000000).forEach(it -> {
            int r = (int) ReflectUtils.invokeMethod(m, m.getClass(), "sum", new Object[]{1, 2});
//            System.out.println(r);
        });
        System.out.println(sw.elapsed(TimeUnit.MILLISECONDS));
    }


    //61
    @Test
    public void testrMethod() {
        M m = new M();
        Stopwatch sw = Stopwatch.createStarted();
        IntStream.range(0, 10000000).forEach(it -> {
            int r = m.sum(1,2);
//            System.out.println(r);
        });
        System.out.println(sw.elapsed(TimeUnit.MILLISECONDS));
    }


}
