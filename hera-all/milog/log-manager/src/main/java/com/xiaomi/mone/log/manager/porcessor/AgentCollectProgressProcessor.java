package com.xiaomi.mone.log.manager.porcessor;

import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.mone.log.api.model.vo.UpdateLogProcessCmd;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.manager.common.Version;
import com.xiaomi.mone.log.manager.service.impl.LogProcessServiceImpl;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author wtt
 * @version 1.0
 * @description 与agent通信的接收器----采集进度
 * @date 2021/8/19 15:32
 */
@Slf4j
@Component
public class AgentCollectProgressProcessor implements NettyRequestProcessor {

    @Resource
    LogProcessServiceImpl processService;

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        log.debug("接受到了agent发过来的信息");
        RemotingCommand response = RemotingCommand.createResponseCommand(Constant.RPCCMD_AGENT_CODE);
        String body = new String(request.getBody());
        UpdateLogProcessCmd cmd = new Gson().fromJson(body, UpdateLogProcessCmd.class);
        log.debug("agent发过来的客户端的请求:{}", cmd.getIp());
        if (null == processService && Ioc.ins().containsBean(LogProcessServiceImpl.class.getCanonicalName())) {
            processService = Ioc.ins().getBean(LogProcessServiceImpl.class);
        }
        if (null != processService) {
            processService.updateLogProcess(cmd);
        }
        response.setBody(new Version().toString().getBytes());
        response.setBody(Constant.SUCCESS_MESSAGE.getBytes());
        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
