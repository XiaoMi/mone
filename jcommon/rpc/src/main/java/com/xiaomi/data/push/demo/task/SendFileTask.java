package com.xiaomi.data.push.demo.task;

import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author goodjava@qq.com
 */
public class SendFileTask extends Task {

    public SendFileTask(RpcServer rpcServer) {
        super(() -> {
            File f = new File("/tmp/t");
            if (f.exists()) {
                RemotingCommand req = RemotingCommand.createRequestCommand(RpcCmd.sfileReq);
                try {
                    req.setBody(Files.readAllBytes(Paths.get("/tmp/t")));
                    req.addExtField("path", "/tmp/t");
                    req.addExtField("targetPath", "/tmp/tt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                rpcServer.sendMessageToAll(req, null);
            }
        }, 5);
    }
}
