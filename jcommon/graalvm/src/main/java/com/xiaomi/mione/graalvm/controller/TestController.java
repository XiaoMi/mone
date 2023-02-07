/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
