package com.xiaomi.mone.demo;

import com.xiaomi.mone.demo.service.DemoService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author goodjava@qq.com
 * @Date 2021/7/28 16:43
 */
@Slf4j
public class TestService {

    private ExecutorService pool = Executors.newFixedThreadPool(20);

    private DemoService demoService = new DemoService();


    public TestService() {
//        demoService.init();
    }


    public double order(String id) {
        log.info("id:{}", id);
        return 1000;
    }

    public String tao() {
         log.info("tao gogogo");
//        pool.submit(() -> {
//            String res = method3();
//            System.out.println(res);
//        });
//        pool.submit(()->{
//            method2();
//        });

//        String res = demoService.redis();
//        System.out.println("redis:" + res);
//        demoService.nutz();

//        return "ok:" + res;
        return "tao";
    }


    public String method2() {
        return "method2";
    }

    public String method3() {
        return "method3";
    }

}
