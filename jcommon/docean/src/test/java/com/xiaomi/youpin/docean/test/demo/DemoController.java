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

package com.xiaomi.youpin.docean.test.demo;

import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;
import com.xiaomi.youpin.docean.mvc.MvcContext;
import com.xiaomi.youpin.docean.mvc.MvcResult;
import com.xiaomi.youpin.docean.test.anno.TAnno;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 2020/6/21
 */
@Controller
@Slf4j
public class DemoController {

    @RequestMapping(path = "/test")
    public DemoVo test() {
        DemoVo vo = new DemoVo();
        vo.setId("1");
        vo.setName("test");
        return vo;
    }

    @RequestMapping(path = "/test2")
    public DemoVo test2(MvcContext context, DemoVo req) {
        log.info("{}", context);
        DemoVo vo = new DemoVo();
        vo.setId("1:" + req.getId());
        vo.setName("test2");
        return vo;
    }

    @RequestMapping(path = "/test3")
    public DemoVo test3(DemoVo req, DemoVo req2) {
        DemoVo vo = new DemoVo();
        vo.setId("1:" + req.getId() + ":" + req2.getId());
        vo.setName("test3");
        return vo;
    }


    /**
     * 测试302 跳转
     * @return
     */
    @RequestMapping(path = "/302")
    public MvcResult<String> go302() {
        MvcResult mr = new MvcResult<String>();
        mr.setCode(302);
        mr.setData("http://www.baidu.com");
        return mr;
    }

    @RequestMapping(path = "/ping")
    public String ping(MvcContext context) {
        log.info("header:{}", context.getHeaders());
        log.info("sessionId:{}", context.session().getId());
        return "pong";
    }

    @TAnno
    @RequestMapping(path = "/testg", method = "get")
    public String testGet(@RequestParam("a") int a, @RequestParam("b") int b) {
        return String.valueOf(a + b);
    }


    /**
     * 测试session
     *
     * @return
     */
    @RequestMapping(path = "/tests", method = "get")
    public String testSession(MvcContext context) {
        return "session";
    }


    public void destory() {
        log.info("destory controller");
    }

}
