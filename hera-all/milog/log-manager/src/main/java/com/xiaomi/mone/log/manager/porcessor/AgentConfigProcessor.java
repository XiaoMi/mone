package com.xiaomi.mone.log.manager.porcessor;

import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.manager.service.impl.AgentConfigServiceImpl;
import com.xiaomi.youpin.docean.Ioc;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wtt
 * @version 1.0
 * @description 与agent通信的接收器----agent启动获取配置
 * @date 2021/8/19 15:32
 */
@Slf4j
public class AgentConfigProcessor implements NettyRequestProcessor {

    private Gson gson = new Gson();

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        RemotingCommand response = RemotingCommand.createResponseCommand(Constant.RPCCMD_AGENT_CONFIG_CODE);
        String ip = new String(request.getBody());
        log.info("agent start get metadata config，agent ip:{}", ip);

        AgentConfigServiceImpl agentConfigService = Ioc.ins().getBean(AgentConfigServiceImpl.class);

        LogCollectMeta logCollectMeta = agentConfigService.getLogCollectMetaFromManager(ip);
        String responseInfo = gson.toJson(logCollectMeta);
        log.info("agent start get metadata config info:{}", response);
        response.setBody(responseInfo.getBytes());
        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
