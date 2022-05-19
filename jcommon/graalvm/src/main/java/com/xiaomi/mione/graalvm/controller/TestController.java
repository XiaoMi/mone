package com.xiaomi.mione.graalvm.controller;

import com.xiaomi.mione.graalvm.service.DestoryService;
import com.xiaomi.mione.graalvm.service.IService;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;

import javax.annotation.Resource;

/**
 * @Author goodjava@qq.com
 * @Date 2021/6/1 22:08
 */
@Controller
public class TestController {

    @Resource(name = "com.xiaomi.mione.graalvm.service.ServiceImpl")
    private IService service;

    @RequestMapping(path = "/hi")
    public String hi() {
        DestoryService d = (DestoryService) service;
        d.$destory();
        return service.hi();
    }

    @RequestMapping(path = "/version")
    public String version() {
        return service.version();
    }


}
