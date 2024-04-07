package run.mone.local.docean.rpc.processor;

import com.alibaba.nacos.api.docean.PingInfo;
import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;
import run.mone.local.docean.rpc.TianyeCmd;

/**
 * @author goodjava@qq.com
 * @date 2022/4/18 10:10
 */
public class PingProcessor implements NettyRequestProcessor {
    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        PingInfo pingInfo = request.getReq(PingInfo.class);
        RemotingCommand response = RemotingCommand.createResponseCommand(TianyeCmd.pingRes);
        PingInfo res = new PingInfo();
        res.setPing(pingInfo.getPing() + ":pong");
        response.setBody(new Gson().toJson(res).getBytes());
        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
