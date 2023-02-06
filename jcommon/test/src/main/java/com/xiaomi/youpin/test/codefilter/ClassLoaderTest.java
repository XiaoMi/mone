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

package com.xiaomi.youpin.test.codefilter;

import com.google.common.collect.Maps;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 3/14/21
 */
public class ClassLoaderTest {

    private static Map<String, Object> m = Maps.newHashMap();

    public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, InterruptedException {
//        test1();
        test2();
        Thread.currentThread().join();
    }

    private static void test1() throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        MyClassLoader loader = new MyClassLoader(new URL[]{
                new URL("file://tmp/test-1.4-SNAPSHOT.jar")
        });
        Class a = loader.loadClass("com.mone.test.codefilter.A");
        System.out.println(a);
        Object obj = a.newInstance();
        Method method = a.getMethod("hi");
        Object res = method.invoke(obj);
        System.out.println(res);
    }

    /**
     * 用来测试classloader 什么时候销毁掉(注意里边map的存储)
     * jps -l
     * jmap -histo  xxx > /tmp/f
     * grep /tmp/f
     * 执行gc
     * 然后 再 grep /tmp/f
     *
     * @throws MalformedURLException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    private static void test2() throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        IntStream.range(0, 1000).forEach(i -> {
            MyClassLoader loader = null;
            try {
                loader = new MyClassLoader(new URL[]{
                        new URL("file://tmp/test-1.4-SNAPSHOT.jar")
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                Class a = loader.loadClass("com.mone.test.codefilter.A");
                System.out.println(a);
                Object obj = a.newInstance();
                Method method = a.getMethod("hi");
                Object res = method.invoke(obj);
                System.out.println(res);
                //如果加上这句那么classloader 永远不会被销毁掉了,因为有引用
                m.put(UUID.randomUUID().toString(), obj);
            } catch (Throwable ex) {

            }
        });

    }
}
