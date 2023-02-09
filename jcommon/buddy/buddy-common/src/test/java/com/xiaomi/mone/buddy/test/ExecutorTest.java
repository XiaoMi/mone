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

package com.xiaomi.mone.buddy.test;

import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @Author goodjava@qq.com
 * @Date 2021/8/1 10:59
 */
public class ExecutorTest {


    public static class MyCallable implements Callable {

        private Callable callable;

        private String name;

        public MyCallable(Callable callable) {
            this.name = Context.get();
            this.callable = callable;
        }

        @Override
        public Object call() throws Exception {
            System.out.println("begin:" + name);
            try {
                return callable.call();
            } finally {
                System.out.println("finish:" + name);
            }
        }
    }

    public static class MyRunnable implements Runnable {

        private Runnable runnable;

        private String name;

        public MyRunnable(Runnable runnable) {
            this.runnable = runnable;
            this.name = Context.get();
        }

        @Override
        public void run() {
            System.out.println("begin:" + name);
            try {
                runnable.run();
            } finally {
                System.out.println("finish:" + name);
            }
        }
    }

    public static class Context {
        static ThreadLocal<String> tl = new ThreadLocal<>();


        public static String get() {
            return tl.get();
        }

        public static void set(String str) {
            tl.set(str);
        }

        public static void remove() {
            tl.remove();
        }

    }


    @Test
    public void test1() throws ExecutionException, InterruptedException {
        Context.set("zzy");
        ExecutorService pool = Executors.newFixedThreadPool(10);
        Future f = pool.submit(new MyCallable(() -> {
            return "abc";
        }));
        System.out.println(f.get());
        new Thread(new MyRunnable(()->{System.out.println("run");})).start();
        Context.remove();
    }
}
