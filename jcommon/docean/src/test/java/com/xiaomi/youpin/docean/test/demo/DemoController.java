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

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;
import com.xiaomi.youpin.docean.mvc.MvcContext;
import com.xiaomi.youpin.docean.mvc.MvcResult;
import com.xiaomi.youpin.docean.test.anno.TAnno;
import com.xiaomi.youpin.docean.test.bo.M;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2020/6/21
 */
@Controller
@Slf4j
public class DemoController {

    @Resource
    private Ioc ioc;

    public void init() {
        log.info("init controller");
    }

    @RequestMapping(path = "/test")
    public DemoVo test() {
        DemoVo vo = new DemoVo();
        vo.setId("1");
        vo.setName("test");
        return vo;
    }

    @RequestMapping(path = "/header")
    public DemoVo header(MvcContext context) {
        DemoVo vo = new DemoVo();
        vo.setId("1");
        vo.setName("test");
        context.getResHeaders().put("name", "zzy");
        return vo;
    }


    @SneakyThrows
    @RequestMapping(path = "/view")
    public String view() {
        return new String(Files.readAllBytes(Paths.get("/Users/dongzhenxing/Documents/Mi/Projects/mione/jcommon/docean/src/test/resources/html/upload.html")));
    }


    @SneakyThrows
    @RequestMapping(path = "/a/**")
    public String a() {
//        TimeUnit.SECONDS.sleep(3);
        return "a:"+Thread.currentThread().getName();
    }


    @RequestMapping(path = "/p")
    public M p(MvcContext c, M m) {
        log.info("{}", c.getHeaders());
        m.setName("zz");
        return m;
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

    @RequestMapping(path = "/test4", method = "get")
    public DemoVo test4(MvcContext context) {
        log.info("{}", context);
        DemoVo vo = new DemoVo();
        vo.setName("test4");
        return vo;
    }

    @RequestMapping(path = "/test5")
    public DemoVo test5(DemoVo req) {
        DemoVo vo = new DemoVo();
        vo.setId(req.getId());
        vo.setName("test5");
        return vo;
    }


    /**
     * 测试302 跳转
     *
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

    @RequestMapping(path = "/testpost")
    public String testPost(String b) {
        log.info("b={}", b);
        return b;
    }


    /**
     * 测试session
     *
     * @return
     */
    @RequestMapping(path = "/tests", method = "get")
    public String testSession(MvcContext context) {
        String name = String.valueOf(context.session().getAttribute("name"));
        return "session:" + name;
    }


    @RequestMapping(path = "/tests2", method = "get")
    public String testSession2(MvcContext context) {
        String name = String.valueOf(context.session().getAttribute("name"));
        return "session:" + name;
    }


    public void destory() {
        log.info("destory controller");
    }

}
