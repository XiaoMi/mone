package run.mone.m78.service.agent.rpc.processor;

import com.xiaomi.data.push.rpc.common.RemotingHelper;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.protobuf.PingMsg;
import run.mone.local.docean.rpc.TianyeCmd;
import run.mone.m78.service.agent.bo.Agent;
import run.mone.m78.service.agent.rpc.AgentManager;
import run.mone.m78.service.context.ApplicationContextProvider;

/**
 * @author goodjava@qq.com
 * @date 2022/4/18 10:10
 */
@Slf4j
public class PingProcessor implements NettyRequestProcessor {

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        PingMsg pingMsg = PingMsg.parseFrom(request.getBody());
        AgentManager agentManager = ApplicationContextProvider.getBean(AgentManager.class);
        agentManager.putAgent(Agent.builder().name(pingMsg.getUserName()).role(pingMsg.getRole()).address(RemotingHelper.parseChannelRemoteAddr(ctx.channel())).build());
        RemotingCommand response = RemotingCommand.createResponseCommand(TianyeCmd.pingRes);
        response.setBody(new byte[]{});
        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
