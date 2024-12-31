package com.xiaomi.youpin.docean.test.demo.mydemo;

import com.xiaomi.youpin.docean.anno.Component;

/**
 * @author goodjava@qq.com
 * @date 2023/11/20 10:33
 */
@Component
public class CallImpl implements ICall{
    @Override
    public String call() {
        return "call";
    }
}
