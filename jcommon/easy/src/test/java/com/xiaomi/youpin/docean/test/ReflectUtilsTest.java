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

import com.google.common.base.Stopwatch;
import com.xiaomi.youpin.docean.common.ReflectUtils;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
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
            int r = m.sum(1, 2);
//            System.out.println(r);
        });
        System.out.println(sw.elapsed(TimeUnit.MILLISECONDS));
    }


    @Test
    public void testInvokeMethod() {
        M m = new M();
        Object res = ReflectUtils.invokeMethod("hi", m, new String[]{"java.lang.String"}, new byte[][]{"abc".getBytes()}, new BiFunction<Class[], byte[][], Object[]>() {
            @Override
            public Object[] apply(Class[] classes, byte[][] bytes) {
                return new Object[]{"abc"};
            }
        }, true);
        System.out.println(res);

        res = ReflectUtils.invokeMethod("hi", m, new String[]{"java.lang.String","java.lang.String"}, new byte[][]{"abc".getBytes()}, new BiFunction<Class[], byte[][], Object[]>() {
            @Override
            public Object[] apply(Class[] classes, byte[][] bytes) {
                return new Object[]{"abc","def"};
            }
        }, true);
        System.out.println(res);

        res = ReflectUtils.invokeMethod("hi", m, new String[]{}, new byte[][]{"abc".getBytes()}, new BiFunction<Class[], byte[][], Object[]>() {
            @Override
            public Object[] apply(Class[] classes, byte[][] bytes) {
                return new Object[]{};
            }
        }, true);
        System.out.println(res);
    }


}
