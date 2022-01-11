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

package com.xiaomi.mone.rcurve.test;

import com.xiaomi.data.push.common.CommonUtils;
import com.xiaomi.data.push.common.RcurveConfig;
import com.xiaomi.data.push.common.SafeRun;
import com.xiaomi.data.push.uds.UdsClient;
import com.xiaomi.data.push.uds.UdsServer;
import com.xiaomi.data.push.uds.context.UdsClientContext;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.client.CallMethodProcessor;
import com.xiaomi.data.push.uds.processor.sever.PingProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 1/3/21
 */
@Slf4j
public class UdsTest {

    private String path = "/tmp/test.sock";

    @Test
    public void testServer() {
        RcurveConfig.ins().init(it -> {
            it.setCodeType((byte) 0);
        });
        UdsServer udsServer = new UdsServer();
        udsServer.getProcessorMap().put("ping", new PingProcessor());
        new Thread(() -> {
            CommonUtils.sleep(10);
            UdsCommand command = UdsCommand.createRequest();
            command.setApp("app1");
            command.setCmd("call");
            command.setServiceName("com.xiaomi.youpin.rpc.test.uds.UdsTestServcie");
            command.setMethodName("test");
            command.setTimeout(1000000);
            SafeRun.run(() -> {
                UdsCommand res = udsServer.call(command);
                System.out.println(res);
            });
        }).start();
        udsServer.start(path);
    }


    @Test
    public void testClient() throws InterruptedException {
        RcurveConfig.ins().init(it -> {
            it.setCodeType((byte) 0);
        });
        UdsClient client = new UdsClient("app1");
        client.getProcessorMap().put("call", new CallMethodProcessor(new Function<UdsCommand, Object>() {
            @Override
            public Object apply(UdsCommand udsCommand) {
                return new UdsTestServcie();
            }
        }));
        client.start(path);
        UdsClientContext.ins().channel.set(client.getChannel());
        IntStream.range(0, 100).forEach(i -> {
            UdsCommand request = UdsCommand.createRequest();
            request.setCmd("ping");
            request.setApp("app1");
            SafeRun.run(() -> {
                UdsCommand res = client.call(request);
                String str = res.getData(String.class);
                System.out.println(str);
            });
            CommonUtils.sleep(5);
        });
        Thread.currentThread().join();
    }
}
