package com.xiaomi.youpin.docean.plugin.test.rpc;

import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.docean.anno.Component;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author goodjava@qq.com
 * @date 2022/5/11
 */
@Component
public class DemoRpcProcessor implements NettyRequestProcessor {
    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    @Override
    public int cmdId() {
        return 199;
    }
}
