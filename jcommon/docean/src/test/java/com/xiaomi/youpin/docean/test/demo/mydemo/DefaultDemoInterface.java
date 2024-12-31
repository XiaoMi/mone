package com.xiaomi.youpin.docean.test.demo.mydemo;

import com.xiaomi.youpin.docean.anno.Component;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/11/20 10:19
 */
@Component
public class DefaultDemoInterface implements DemoInterface {
    @Override
    public String hi() {
        return "test";
    }
}
