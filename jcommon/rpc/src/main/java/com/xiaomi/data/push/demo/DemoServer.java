package com.xiaomi.data.push.demo;

import com.google.common.collect.Lists;
import com.xiaomi.data.push.demo.task.GetInfoTask;
import com.xiaomi.data.push.demo.task.SendFileTask2;
import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.common.Pair;
import com.xiaomi.data.push.rpc.processor.MPingProcessor;
import com.xiaomi.data.push.rpc.processor.PingProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class DemoServer {

    public static void main(String... args) {
        RpcServer rpcServer = new RpcServer("127.0.0.1:80", "demo_server1");
        //注册处理器
        rpcServer.setProcessorList(Lists.newArrayList(
                new Pair<>(RpcCmd.pingReq, new PingProcessor()),
                new Pair<>(RpcCmd.mpPingReq, new MPingProcessor())
        ));

        //注册周期任务
        rpcServer.setTasks(Lists.newArrayList(
                new GetInfoTask(rpcServer)
//                new SendFileTask(rpcServer),
//                new SendFileTask2(rpcServer)
        ));
        rpcServer.init();
        rpcServer.start();

    }
}
