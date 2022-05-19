package com.xiaomi.data.push.rpc.processor;

import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author goodjava@qq.com
 */
public class SFileProcessor implements NettyRequestProcessor {

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(request.getExtField("targetPath"), "rw");
            raf.write(request.getBody());
            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
