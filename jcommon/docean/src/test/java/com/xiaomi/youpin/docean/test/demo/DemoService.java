package com.xiaomi.youpin.docean.test.demo;

import com.xiaomi.youpin.docean.anno.Lookup;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
@Service
@Slf4j
public class DemoService {


    @Resource(shareable = false)
    private DemoDao testDao;


    @Resource(name = "strBean")
    private String str;


    @Resource(lookup = "zzy")
    private DemoA demoA;

    @Resource
    private ErrorReport errorReport;

    public String demoA() {
        return demoA.f();
    }


    public String call() {
        return testDao.get() + "!!!" + str + testDao.toString();
    }


    public String vo() {
        return demoVo().toString();
    }

    @Lookup
    public DemoVo demoVo() {
        return null;
    }

    public void init() {
        log.info("demoService init");
        errorReport.setError(false);
        errorReport.setMessage("error!");
    }

    public void destory() {
        log.info("destory service");
    }
}
