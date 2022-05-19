package com.xiaomi.data.push.demo.task;

import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.task.Task;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class GetInfoTask extends Task {

    private RpcServer rpcServer;

    public GetInfoTask(final RpcServer rpcServer) {
        super(() -> {
            run(rpcServer);
        }, 5);
    }


    private static void run(RpcServer rpcServer) {
        rpcServer.sendMessageToAll(RemotingCommand.createRequestCommand(RpcCmd.getInfoReq), (f) -> {
            log.info(new String(f.getResponseCommand().getBody()));
        });
    }
}
