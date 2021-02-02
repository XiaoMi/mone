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

package com.xiaomi.youpin.tesla.agent.test;

import com.github.dockerjava.api.model.ExposedPort;
import com.google.gson.Gson;
import com.xiaomi.youpin.tesla.agent.common.LockUtils;
import com.xiaomi.youpin.tesla.agent.po.AbstractRes;
import com.xiaomi.youpin.tesla.agent.po.NukeRes;
import com.xiaomi.youpin.tesla.agent.po.SreLabel;
import org.junit.Test;

import java.util.concurrent.*;

public class AgentCommonTest {


    @Test
    public void testGson() {
        NukeRes nr = new NukeRes();
        nr.setCode(1);
        nr.setMessage("msg");
        String json = new Gson().toJson(nr);
        System.out.println(json);

        AbstractRes ar = new Gson().fromJson(json,NukeRes.class);
        System.out.println(ar.getCode());
        System.out.println("a:b".split(":")[0]);
    }


    @Test
    public void testSreLabel() {
        SreLabel label = new Gson().fromJson("{\"timestamp\":1593757059789,\"ip\":\"xxxx\",\"keycenter\":true,\"docker\":true,\"outbound\":true}",SreLabel.class);
        System.out.println(label);
    }

    @Test
    public void test() {
        System.out.println(LockUtils.tryLock("key"));
        System.out.println(LockUtils.tryLock("key1"));
    }


    @Test
    public void testPort() {
        ExposedPort nep = ExposedPort.parse("9999"+"/udp");
        System.out.println(nep.getPort()+","+nep.getProtocol());
    }


    @Test
    public void testGet() throws InterruptedException, ExecutionException, TimeoutException {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        Future<String> f = pool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                for (int i = 0; i < 10; i++) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (Throwable e) {
                        System.out.println("----:" + e);
                        throw new RuntimeException(e);
                    }
                    System.out.println("run");
                }
                return "abc";
            }
        });
        try {
            String res = f.get(5, TimeUnit.SECONDS);
            System.out.println(res);
        } catch (Throwable ex) {
            System.out.println("---->" + ex.getMessage());
            f.cancel(true);
        }
        TimeUnit.SECONDS.sleep(10);
    }


    @Test
    public void testGet2() throws InterruptedException, ExecutionException, TimeoutException {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        Future<String> f = pool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                if (1 == 1) {
                    throw new RuntimeException("!!!");
                }
                return "abc";
            }
        });
        try {
            String res = f.get(5, TimeUnit.SECONDS);
            System.out.println(res);
        } catch (Throwable ex) {
            System.out.println("---->" + ex.getMessage());
            f.cancel(true);
        }
        TimeUnit.SECONDS.sleep(10);
    }
}
