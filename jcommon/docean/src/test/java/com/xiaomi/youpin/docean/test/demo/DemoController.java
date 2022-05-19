package com.xiaomi.youpin.docean.test.demo;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;
import com.xiaomi.youpin.docean.mvc.MvcContext;
import com.xiaomi.youpin.docean.mvc.MvcResult;
import com.xiaomi.youpin.docean.test.anno.TAnno;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2020/6/21
 */
@Controller
@Slf4j
public class DemoController {

    @Resource
    private Ioc ioc;

    public void init(){
        log.info("init controller");
    }

    @RequestMapping(path = "/test")
    public DemoVo test() {
        DemoVo vo = new DemoVo();
        vo.setId("1");
        vo.setName("test");
        return vo;
    }

    @RequestMapping(path = "/a/**")
    public String a() {
        return "a";
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

    @RequestMapping(path = "/test4",method = "get")
    public DemoVo test4(MvcContext context) {
        log.info("{}", context);
        DemoVo vo = new DemoVo();
        vo.setName("test4");
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
