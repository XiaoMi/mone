package com.xiaomi.mone.buddy.test;

import org.junit.Test;

import java.util.ServiceLoader;

/**
 * @Author goodjava@qq.com
 * @Date 2021/7/26 14:26
 */
public class ServiceTest {

    @Test
    public void test1() {
        ServiceLoader<IService> serviceLoader = ServiceLoader.load(IService.class);
        for (IService userService : serviceLoader) {
            System.out.println(userService.hi());
        }
    }

}
