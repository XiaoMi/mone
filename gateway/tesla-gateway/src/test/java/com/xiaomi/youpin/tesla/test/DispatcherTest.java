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

package com.xiaomi.youpin.tesla.test;

import com.xiaomi.youpin.gateway.dispatch.Dispatcher;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import org.junit.Test;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class DispatcherTest {

    @Test
    public void testDispatcher0() throws InterruptedException {
//        int timeout = 2000;
//        ApiInfo apiInfo = new ApiInfo(null, null, null, null, null, 0, null, null, null, null, null, null, timeout, null, 0, 0, null, null, null, 0, 0, null, 0L, null);
//        Dispatcher dispatcher = new Dispatcher();
//        dispatcher.setInvokePoolSize(1);
//        dispatcher.init();
//        dispatcher.dispatcher((str) -> {
//            try {
//                TimeUnit.MILLISECONDS.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                throw new RuntimeException("---");
//            }
//            return "abc";
//        }, (r) -> {
//            System.out.println("------->" + r);
//        }, apiInfo);
//
//        Thread.currentThread().join();

    }


    @Test
    public void testDispatcher() throws InterruptedException {
//        int timeout = 20000;
//        ApiInfo apiInfo = new ApiInfo(null, null, null, null, null, 0, null, null, null, null, null, null, timeout, null, 0, 0, null, null, null, 0, 0, null, 0L, null);
//        Dispatcher dispatcher = new Dispatcher();
//        dispatcher.setInvokePoolSize(1);
//        dispatcher.init();
//        dispatcher.dispatcher((str) -> {
//            try {
//                TimeUnit.MILLISECONDS.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                throw new RuntimeException("---");
//            }
//            return "abc1";
//        }, (r) -> {
//            System.out.println("------->" + r);
//        }, apiInfo);
//
//        dispatcher.dispatcher((str) -> {
//            try {
//                TimeUnit.MILLISECONDS.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                throw new RuntimeException("---");
//            }
//            return "abc2";
//        }, (r) -> {
//            System.out.println("------->" + r);
//        }, apiInfo);
//
//
//        timeout = 100;
//        apiInfo = new ApiInfo(3L, null, null, null, null, 0, null, null, null, null, null, null, timeout, null, 0, 0, null, null, null, 0, 0, null, 0L, null);
//        Future f = dispatcher.dispatcher((str) -> {
//            try {
//                TimeUnit.MILLISECONDS.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                throw new RuntimeException("---");
//            } finally {
//                System.out.println("finish");
//            }
//            return "abc3";
//        }, (r) -> {
//            System.out.println("------->" + r + " 3");
//        }, apiInfo);
//
//        System.out.println(f);
//
//
//        TimeUnit.SECONDS.sleep(100);

    }
}
