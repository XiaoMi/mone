package com.xiaomi.mione.graalvm.service;

/**
 * @Author goodjava@qq.com
 * @Date 2021/6/10 10:32
 */
public class AB implements IA, IB {
    @Override
    public String a() {
        return "a";
    }

    @Override
    public String b() {
        return "b";
    }
}
