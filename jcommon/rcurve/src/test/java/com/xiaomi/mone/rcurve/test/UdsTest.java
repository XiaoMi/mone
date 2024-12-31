package com.xiaomi.mone.rcurve.test;

import com.xiaomi.data.push.common.CommonUtils;
import com.xiaomi.data.push.common.RcurveConfig;
import com.xiaomi.data.push.common.SafeRun;
import com.xiaomi.data.push.uds.UdsClient;
import com.xiaomi.data.push.uds.UdsServer;
import com.xiaomi.data.push.uds.codes.GsonCodes;
import com.xiaomi.data.push.uds.context.UdsClientContext;
import com.xiaomi.data.push.uds.handler.MessageTypes;
import com.xiaomi.data.push.uds.handler.ClientStreamCallback;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.client.CallMethodProcessor;
import com.xiaomi.data.push.uds.processor.sever.MockStreamProcessor;
import com.xiaomi.data.push.uds.processor.sever.PingProcessor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.UUID;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 1/3/21
 */
@Slf4j
public class UdsTest {

    private String path = "/tmp/test.sock";

    private boolean remote = false;

    /**
     * 模拟启动server
     */
    @Test
    public void testServer() {
        //使用gson格式编码
        RcurveConfig.ins().init(it -> it.setCodeType(GsonCodes.type));
        UdsServer udsServer = new UdsServer();
        udsServer.setRemote(remote);
        udsServer.setHost("0.0.0.0");
        udsServer.setPort(7777);
        udsServer.putProcessor(new PingProcessor());
        udsServer.putProcessor(new MockStreamProcessor());
        new Thread(() -> {
            CommonUtils.sleep(10);
            UdsCommand command = UdsCommand.createRequest();
            command.setApp("app1");
            command.setCmd("call");
            command.setServiceName("com.xiaomi.youpin.rpc.test.uds.UdsTestServcie");
            command.setMethodName("test");
            command.setTimeout(1000000);
//            SafeRun.run(() -> {
//                UdsCommand res = udsServer.call(command);
//                System.out.println(res);
//            });
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
     *
     * @throws InterruptedException
     */
    @Test
    public void testClient() throws InterruptedException {
        RcurveConfig.ins().init(it -> it.setCodeType(GsonCodes.type));
        UdsClient client = new UdsClient("app1");
        client.setRemote(true);
        client.setHost("127.0.0.1");
        client.setPort(7777);
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


    /**
     * 测试StreamClient方法
     *
     * 该方法初始化RcurveConfig，创建并配置UdsClient，设置处理器并启动客户端。
     * 然后进行100次ping操作，每次ping都会创建一个UdsCommand请求并通过client.stream方法发送，
     * 并使用ClientStreamCallback处理响应内容、完成和错误信息。
     */
	@Test
    public void testStreamClient() {
        RcurveConfig.ins().init(it -> it.setCodeType(GsonCodes.type));
        UdsClient client = new UdsClient("app1");
        client.setRemote(remote);
        client.setHost("127.0.0.1");
        client.setPort(7777);
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
            request.setCmd("stream");
            request.setApp("app1");

            request.putAtt(MessageTypes.TYPE_KEY, MessageTypes.TYPE_OPENAI);
            request.putAtt(MessageTypes.STREAM_ID_KEY, UUID.randomUUID().toString());

            SafeRun.run(() -> client.stream(request, new ClientStreamCallback() {
                @Override
                public void onContent(String content) {
                    System.out.println(content);
                }

                @Override
                public void onComplete() {
                    System.out.println("complete");
                }

                @Override
                public void onError(Throwable error) {
                    System.out.println(error.getMessage());
                }
            }));
            CommonUtils.sleep(15);
        });
    }

}
