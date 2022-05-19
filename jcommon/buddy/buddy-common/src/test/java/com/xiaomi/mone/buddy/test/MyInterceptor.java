package com.xiaomi.mone.buddy.test;

import net.bytebuddy.implementation.bind.annotation.Super;

/**
 * @Author goodjava@qq.com
 * @Date 2021/7/20 09:56
 */
public class MyInterceptor {

    public static int log(int a, int b, @Super Foo foo) {
        System.out.println("Calling sum");
        try {
            return foo.sum(a, b);
        } finally {
            System.out.println("Returned from sum");
        }
    }


}
