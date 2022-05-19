package com.xiaomi.data.push.rpc.processor;

import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.RpcVersion;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * 获取客户端信息
 */
@Slf4j
public class GetInfoProcessor implements NettyRequestProcessor {

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) {
        log.info("get info");
        return RemotingCommand.createResponseCommand(RpcCmd.getInfoRes, version());
    }

    public String version() {
        return new RpcVersion().toString();
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
