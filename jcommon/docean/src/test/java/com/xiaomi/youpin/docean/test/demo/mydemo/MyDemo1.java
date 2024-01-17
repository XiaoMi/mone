package com.xiaomi.youpin.docean.test.demo.mydemo;

import com.xiaomi.youpin.docean.anno.Component;

/**
 * @author goodjava@qq.com
 * @date 2023/11/18 14:50
 */
@Component
public class MyDemo1 implements MyDemo{
    @Override
    public String hi() {
        return "demo1";
    }
}
