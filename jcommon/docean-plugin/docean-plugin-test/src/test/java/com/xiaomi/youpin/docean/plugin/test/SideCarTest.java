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

import com.xiaomi.data.push.common.SafeRun;
import com.xiaomi.data.push.uds.UdsClient;
import com.xiaomi.data.push.uds.UdsServer;
import com.xiaomi.data.push.uds.context.UdsServerContext;
import com.xiaomi.data.push.uds.po.RpcCommand;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.config.Config;
import io.netty.channel.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import run.mone.api.IServer;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2022/6/18
 */
@Slf4j
public class SideCarTest {

    private boolean useGrpc = false;

    @Test
    public void testServer() throws InterruptedException {
        Config config = new Config();
        config.put("sidecarServer", "true");
        config.put("sidecarGrpc", String.valueOf(useGrpc));
        config.put("app", "demoAppServer");
        Ioc ioc = Ioc.ins().putBean(config).init("run.mone.sidecar", "run.mone.docean.plugin", "com.xiaomi.youpin.docean.plugin.config");
        IServer server = ioc.getBean("sideCarServer");
        sleep(5);
        IntStream.range(0, 1000).forEach(i -> {
            SafeRun.run(() -> {
                log.info("execute call client");
                UdsCommand request = UdsCommand.createRequest();
                request.setApp("demoApp");
                request.setCmd("execute");
                RpcCommand res = server.call(request);
                log.info(new String(res.getData()));
            });
            sleep(2);
        });
        Thread.currentThread().join();
    }


    @SneakyThrows
    @Test
    public void testClient() {
        Config config = new Config();
        config.put("sidecarClient", "true");
        config.put("sidecarGrpc", String.valueOf(useGrpc));
        config.put("app", "demoApp");
        Ioc.ins().putBean(config).init("run.mone.sidecar", "run.mone.docean.plugin", "com.xiaomi.youpin.docean.plugin.config");
        Thread.currentThread().join();
    }

    private void sleep(int timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
