package com.xiaomi.mone.rcurve.test;

import com.xiaomi.data.push.common.CommonUtils;
import com.xiaomi.data.push.common.Pair;
import com.xiaomi.data.push.common.RcurveConfig;
import com.xiaomi.data.push.common.SafeRun;
import com.xiaomi.data.push.uds.UdsClient;
import com.xiaomi.data.push.uds.UdsServer;
import com.xiaomi.data.push.uds.codes.GsonCodes;
import com.xiaomi.data.push.uds.context.UdsClientContext;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.client.CallMethodProcessor;
import com.xiaomi.data.push.uds.processor.sever.PingProcessor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 1/3/21
 */
@Slf4j
public class UdsTest {

    private String path = "/tmp/test.sock";

    /**
     * 模拟启动server
     */
    @Test
    public void testServer() {
        //使用gson格式编码
        RcurveConfig.ins().init(it -> it.setCodeType(GsonCodes.type));
        UdsServer udsServer = new UdsServer();
        udsServer.putProcessor(new PingProcessor());
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


    @SneakyThrows
    @Test
    public void testClient2() {
        testClient();
    }

    /**
     * 模拟启动client
     * @throws InterruptedException
     */
    @Test
    public void testClient() throws InterruptedException {
        RcurveConfig.ins().init(it -> it.setCodeType(GsonCodes.type));
        UdsClient client = new UdsClient("app1");
        client.putProcessor(new CallMethodProcessor(new Function<UdsCommand, Object>() {
            @Override
            public Object apply(UdsCommand udsCommand) {
                return new UdsTestServcie();
            }
        }));
        client.start(path);
        UdsClientContext.ins().channel.set(client.getChannel());
        //调用100次ping
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
