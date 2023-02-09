package com.xiaomi.youpin.docean.test.demo3;

import com.xiaomi.youpin.docean.anno.Controller;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2023/1/15 12:09
 */
@Controller
public class ControllerDemo {

    @Resource
    private ServiceDemo serviceDemo;


    public String call() {
        return serviceDemo.call();
    }

}
