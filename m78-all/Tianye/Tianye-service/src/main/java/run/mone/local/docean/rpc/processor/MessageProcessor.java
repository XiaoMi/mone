package run.mone.local.docean.rpc.processor;

import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 2022/4/18 10:10
 */
@Slf4j
public class MessageProcessor implements NettyRequestProcessor {


    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

}
