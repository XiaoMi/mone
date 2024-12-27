package com.xiaomi.youpin.docean.test;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.IocConfiguration;
import com.xiaomi.youpin.docean.test.demo.DemoDao;

/**
 * @author goodjava@qq.com
 * @date 2024/3/5 14:47
 * <p>
 * Evaluating the efficacy of @IocConfiguration.
 */
@IocConfiguration(basePackage = {"com.xiaomi.youpin.docean.test.demo"})
public class TestRun {

    public static void main(String[] args) {
        DemoDao demoA = Ioc.run(TestRun.class, args).getBean(DemoDao.class);
        System.out.println(demoA.get());
    }

}
