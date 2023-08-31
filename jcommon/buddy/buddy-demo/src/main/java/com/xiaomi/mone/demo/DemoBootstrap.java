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

package com.xiaomi.mone.demo;

import com.xiaomi.mone.demo.service.AService;
import com.xiaomi.mone.demo.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @Author goodjava@qq.com
 * @Date 2021/7/26 10:17
 */
@Slf4j
public class DemoBootstrap {

    public static void main(String... args) throws Exception {

//        Class<?> clazz = Class.forName("com.xiaomi.mone.buddy.agent.bo.Dog");
//        System.out.println(clazz);


        DemoService service = new DemoService("lucy");
        service.init();
////        System.out.println(service.test("old"));
//        service.submit(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("run");
//            }
//        });
//
//        service.submit(new Callable() {
//            @Override
//            public Object call() throws Exception {
//                return "zzy";
//            }
//        });

//        System.out.println(service.hi());
//        String r = service.redis();
//        String r = service.nutz();
//        String r = service.tao();
//        System.out.println(r);
        TestService ts = new TestService();
        AService aService = new AService();
        int i = 12340;
        while (true) {
//            System.out.println(ts.order(String.valueOf(i++)));
            System.out.println(ts.tao());
//            System.out.println(aService.hi());
//            String res = service.redis();
//            String res = service.nutz();
//            System.out.println(res);
            TimeUnit.SECONDS.sleep(1);
//            if (i > 12346) {
//                break;
//            }
        }
//        System.out.println(new DemoService().okhttp());
//        service.run();


//        new Thread(()->{
//            System.out.println("thread");
//        }).start();
    }

}
