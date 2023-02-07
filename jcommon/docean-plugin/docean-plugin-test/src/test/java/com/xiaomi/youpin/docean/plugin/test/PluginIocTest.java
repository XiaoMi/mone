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

package com.xiaomi.youpin.docean.plugin.test;

import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.bo.Bean;
import com.xiaomi.youpin.docean.plugin.Plugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
public class PluginIocTest {

    @Test
    public void testConfig() {
        Config config = new Config();
        config.put("rpcOpenServer", "true");
        config.put("db_url", "");
        config.put("nnn", "123");
        Ioc ioc = Ioc.ins().putBean(config).init("run.mone.docean", "com.xiaomi.youpin.docean");
        ioc.getBeans(Bean.Type.config).forEach(it -> {
            System.out.println(it.getAlias() + ":" + it.getObj());
        });
    }


    @Test
    public void testRpc() {
        Config config = new Config();
        config.put("rpcOpenServer", "true");
        config.put("db_url", "");
        config.put("nnn", "123");
        Ioc ioc = Ioc.ins().putBean(config).init("run.mone.docean", "com.xiaomi.youpin.docean");
        RpcServer server = ioc.getBean(RpcServer.class);
        System.out.println(server);
    }

    @Test
    public void testPluginIoc() throws IOException {
        System.out.println(Ioc.ins().init("com.xiaomi.youpin.docean").getBeans());
        System.out.println(Plugin.ins().getPlugins());
        S2 s2 = Ioc.ins().getBean(S2.class);
        System.out.println(s2);
        System.in.read();
        Ioc.ins().destory();
        System.out.println("finish");
    }


    @Test
    public void testMybatis() {
        System.out.println(Ioc.ins().init("com.xiaomi.youpin.docean").getBeans());
        System.out.println(Plugin.ins().getPlugins());
        S2 s2 = Ioc.ins().getBean(S2.class);
        s2.mybatis2();
    }

    @Test
    public void testMqproducer() throws IOException {
        System.out.println(Ioc.ins().init("com.xiaomi.youpin.docean").getBeans());
        System.out.println(Plugin.ins().getPlugins());
        S2 s2 = Ioc.ins().getBean(S2.class);
        s2.rocketproduce();
        System.out.println(s2);
        System.in.read();
        Ioc.ins().destory();
        System.out.println("finish");
    }


    @Test
    public void testMqConsumer() throws IOException {
        System.out.println(Ioc.ins().init("com.xiaomi.youpin.docean").getBeans());
        System.out.println(Plugin.ins().getPlugins());
        S2 s2 = Ioc.ins().getBean(S2.class);
        s2.rocketconsume();
        System.out.println(s2);
        System.in.read();
        Ioc.ins().destory();
        System.out.println("finish");
    }


    private void run(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
