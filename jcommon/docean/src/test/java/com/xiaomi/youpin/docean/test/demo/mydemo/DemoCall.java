package com.xiaomi.youpin.docean.test.demo.mydemo;

import com.xiaomi.youpin.docean.anno.Service;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2023/11/18 14:52
 */


@Service
public class DemoCall {

    @Resource(name = "$demoName")
    private MyDemo demo;


    public String hi() {
        return demo.hi();
    }


}
