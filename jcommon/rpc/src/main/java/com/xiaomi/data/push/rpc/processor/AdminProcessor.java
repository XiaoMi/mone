package com.xiaomi.data.push.rpc.processor;

import com.xiaomi.data.push.bo.AdminReq;
import com.xiaomi.data.push.context.AgentContext;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author goodjava@qq.com
 */
public class AdminProcessor implements NettyRequestProcessor {

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        AdminReq req = request.getReq(AdminReq.class);
        switch (req.getCmd()) {
            case "df":{
                String address = req.getParams().get("address");
                String filePath = req.getParams().get("file");
                Channel ch = AgentContext.ins().map.get(address).getChannel();
                if (null != ch) {

                }
                break;
            }
        }


        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
