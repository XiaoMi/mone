package com.xiaomi.youpin.rpc.test.tcp;

import com.google.common.collect.Lists;
import com.xiaomi.data.push.bo.ClientInfo;
import com.xiaomi.data.push.bo.MPPing;
import com.xiaomi.data.push.bo.User;
import com.xiaomi.data.push.demo.task.GetInfoTask;
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.common.NetUtils;
import com.xiaomi.data.push.rpc.common.Pair;
import com.xiaomi.data.push.rpc.processor.*;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.task.Task;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2022/5/19
 */
@Slf4j
public class TcpTest {


    @SneakyThrows
    @Test
    public void testServer() {
        RpcServer rpcServer = new RpcServer("", "demo_server1", false);
        //注册处理器
        rpcServer.setProcessorList(Lists.newArrayList(
                new Pair<>(RpcCmd.mpPingReq, new MPingProcessor())
        ));
        //注册周期任务
        rpcServer.setTasks(Lists.newArrayList(
//                new GetInfoTask(rpcServer),
                new Task(()->{
                    RemotingCommand req = RemotingCommand.createRequestCommand(RpcCmd.getInfoReq);
                    rpcServer.tell(u-> null!=u && u.getName().equals("zzy"),req);
                },2)
        ));
        rpcServer.init();
        rpcServer.start();

        Thread.currentThread().join();
    }


    @SneakyThrows
    @Test
    public void testClient() {
        RpcClient client = new RpcClient("169.254.216.11:59214");
        client.setReconnection(false);
        client.setProcessorList(Lists.newArrayList(
                new Pair<>(RpcCmd.getInfoReq, new GetInfoProcessor())
        ));
        client.setTasks(Lists.newArrayList(
                new Task(() -> {
                    try {
                        log.info("send ping");
                        MPPing p = new MPPing();
                        User user = new User();
                        user.setName("zzy");
                        p.setData("ping");
                        p.setUser(user);
                        client.sendMessage(client.getServerAddrs(), RemotingCommand.createMsgPackRequest(RpcCmd.mpPingReq, p), responseFuture -> {
                            MPPing pong = responseFuture.getResponseCommand().getReq(MPPing.class);
                            log.info("--->" + pong.getData());
                        });
                    } catch (Exception ex) {
                        log.error(ex.getMessage());
                    }
                }, 5)
        ));
        client.init();
        client.start();
        client.getClient().createChannel();
        Thread.currentThread().join();
    }

}

