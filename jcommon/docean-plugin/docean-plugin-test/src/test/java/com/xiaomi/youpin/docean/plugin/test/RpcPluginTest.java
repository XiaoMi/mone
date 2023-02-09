package com.xiaomi.youpin.docean.plugin.test;

import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.processor.PingProcessor;
import com.xiaomi.data.push.rpc.processor.RpcCallMethodProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.task.Task;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.config.Config;
import com.xiaomi.youpin.docean.plugin.test.rpc.server.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import run.mone.docean.plugin.rpc.context.RpcContext;
import run.mone.docean.plugin.rpc.context.RpcContextHolder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2023/1/8 15:50
 * <p>
 * 用来测试 rpc plugin
 */
@Slf4j
public class RpcPluginTest {


    @Test
    public void testRpcServer() throws IOException, InterruptedException {
        Config config = new Config();
        config.put("rpcOpenServer", "true");
        config.put("serviceName", "sautumn-manager");
        config.put("nacosAddr", System.getenv("nacos_addr"));
        Ioc ioc = Ioc.ins();
        PingProcessor pingProcessor = new PingProcessor() {
            @Override
            public int cmdId() {
                return 1;
            }
        };
        ioc.putBean(config).putBean(pingProcessor).init("run.mone.docean.plugin.rpc", "com.xiaomi.youpin.docean.plugin.config", "com.xiaomi.youpin.docean.plugin.test.rpc.server");
        TimeUnit.SECONDS.sleep(20);
        RpcServer rpcServer = Ioc.ins().getBean(RpcServer.class.getName());
        List<String> list = rpcServer.clientList();
        if (list.size() > 0) {
            IntStream.range(0, 100).forEach(i -> {
                String addr = list.get(0);
                RpcContext context = new RpcContext();
                context.setAddress(addr.substring(1));
                RpcContextHolder.getContext().set(context);
                RpcService service = Ioc.ins().getBean(RpcService.class);
                int res = service.sum(11,11);
                System.out.println("res:" + res);
                RpcContextHolder.getContext().close();
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        System.in.read();
    }


    @Test
    public void testRpcClient() throws IOException {
        Config config = new Config();
        config.put("rpcOpenClient", "true");
        //会从nacos找到这个server 并且连接过去
        config.put("serviceName", "sautumn-manager");
        config.put("nacosAddr", System.getenv("nacos_addr"));
        Ioc ioc = Ioc.ins();
        Task task = new Task(() -> {
            RpcClient client = ioc.getBean(RpcClient.class.getName());
            RemotingCommand req = RemotingCommand.createRequestCommand(1);
            req.setBody("ping".getBytes());
            client.sendMessage(client.getServerAddrs(), req, responseFuture -> {
                String pong = new String(responseFuture.getResponseCommand().getBody());
                log.info("--->" + pong);
            });

        }, 5);

        RpcCallMethodProcessor rpcCallMethodProcessor = new RpcCallMethodProcessor(mr -> {
            String name = mr.getServiceName();
            return ioc.getBean(name);
        }) {
            @Override
            public int cmdId() {
                return 2;
            }
        };

        ioc.putBean(config).putBean(task).putBean(rpcCallMethodProcessor).init("run.mone.docean.plugin.rpc", "com.xiaomi.youpin.docean.plugin.config", "com.xiaomi.youpin.docean.plugin.test.rpc.client");
        System.in.read();
    }
}
