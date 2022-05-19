package com.xiaomi.mone.buddy.test;

/**
 * @Author goodjava@qq.com
 * @Date 2021/7/16 17:18
 */
public class Bar {

    public static String sayHelloFoo() {
        return "bar";
    }


    public static int sum(int a, int b) {
        System.out.println("sum call");
        return (int) ((a + b) * 0.1);
    }

}
