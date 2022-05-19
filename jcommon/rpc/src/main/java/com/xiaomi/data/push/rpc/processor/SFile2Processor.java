package com.xiaomi.data.push.rpc.processor;

import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class SFile2Processor implements NettyRequestProcessor {

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) {
        log.info("receive file:{}",request.getExtField("targetPath"));
        RandomAccessFile raf = null;
        if (null == request.getBody()) {
            return null;
        }
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
