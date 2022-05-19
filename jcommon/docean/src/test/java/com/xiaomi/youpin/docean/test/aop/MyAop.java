package com.xiaomi.youpin.docean.test.aop;

import com.xiaomi.youpin.docean.aop.ProceedingJoinPoint;
import com.xiaomi.youpin.docean.aop.anno.After;
import com.xiaomi.youpin.docean.aop.anno.Aspect;
import com.xiaomi.youpin.docean.aop.anno.Before;
import com.xiaomi.youpin.docean.test.anno.CAfter;
import com.xiaomi.youpin.docean.test.anno.CBefore;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author goodjava@qq.com
 * @date 5/14/22
 */
@Aspect
@Slf4j
public class MyAop {


    @Resource
    private MyService myService;


    @Before(anno = CBefore.class)
    public void before(ProceedingJoinPoint point) {
        myService.log();
        log.info("before:" + Arrays.toString(point.getArgs()));
    }

    @After(anno = CAfter.class)
    public Object after(ProceedingJoinPoint point) {
        myService.log();
        log.info("after:" + point.getRes());
        return point.getRes() + " after";
    }


}
