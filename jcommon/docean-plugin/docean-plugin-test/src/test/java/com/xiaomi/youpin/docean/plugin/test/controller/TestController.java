package com.xiaomi.youpin.docean.plugin.test.controller;

import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;

/**
 * @author goodjava@qq.com
 * @date 2022/9/22 10:37
 */
@Controller
public class TestController {


    @RequestMapping(path = "/hi", method = "get")
    public String hi() {
        return "hi";
    }

}
