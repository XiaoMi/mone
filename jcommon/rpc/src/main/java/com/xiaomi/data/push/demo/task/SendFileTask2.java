package com.xiaomi.data.push.demo.task;

import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.task.Task;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author goodjava@qq.com
 */
public class SendFileTask2 extends Task {

    public SendFileTask2(RpcServer rpcServer) {
        super(() -> {
            File f = new File("/tmp/t");
            if (f.exists()) {

                RemotingCommand req = RemotingCommand.createRequestCommand(RpcCmd.sfileReq2, (int) f.length());
                req.addExtField("path", "/tmp/t");
                req.addExtField("targetPath", "/tmp/tt");

                rpcServer.send((ch) -> {
                    FileRegion fg = new DefaultFileRegion(f, 0, f.length());
                    ch.writeAndFlush(req);
                    ch.writeAndFlush(fg);
                });

            }
        }, 5);
    }
}