package com.xiaomi.data.push.rpc.netty;

import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;


/**
 * @author goodjava@qq.com
 */
public interface NettyRequestProcessor {

    RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request)
            throws Exception;

    boolean rejectRequest();

    default int cmdId() {
        return 0;
    }

    /**
     * 可以设定每个业务的线程数量(0使用默认线程池)
     * @return
     */
    default int poolSize() {
        return 0;
    }
}
