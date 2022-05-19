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
