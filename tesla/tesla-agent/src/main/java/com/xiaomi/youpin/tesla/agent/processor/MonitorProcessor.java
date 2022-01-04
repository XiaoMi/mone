package com.xiaomi.youpin.tesla.agent.processor;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docker.YpDockerClient;
import com.xiaomi.youpin.tesla.agent.cmd.AgentCmd;
import com.xiaomi.youpin.tesla.agent.po.ContainerBasicInfo;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@Component
public class MonitorProcessor implements NettyRequestProcessor {
    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand remotingCommand) throws Exception {
        ContainerBasicInfo req = remotingCommand.getReq(ContainerBasicInfo.class);
        RemotingCommand response = RemotingCommand.createResponseCommand(AgentCmd.monitorRes);
        log.info("MonitorProcessor.processRequest, req: {}", req);
        String containerName = req.getContainerName();
        /*if ("docker".equals(req.getDeployType())){
            containerName = containerName.split("-20")[0];
        } else if ("dockerfile".equals(req.getDeployType())) {
            containerName = containerName.substring(0, containerName.lastIndexOf("-"));
        }*/
        List<Container> list = YpDockerClient.ins().listContainers(Lists.newArrayList(), true, containerName);
        log.info("containerName:{} list.size():{}", containerName, list.size());
        if (list.size() == 0){
            req.setRunning(false);
            response.setBody(new Gson().toJson(req).getBytes());
            return response;
        }
        InspectContainerResponse res = YpDockerClient.ins().inspectContainer(list.get(0).getId());
        InspectContainerResponse.ContainerState state = res.getState();
        log.info("containerName:{} id:{} state:{}", req.getContainerName(), list.get(0).getId(), state);
        req.setRunning(state.getRunning());
        req.setExitCode(state.getExitCode());
        req.setOomKilled(state.getOOMKilled());
        req.setError(state.getError());

        response.setBody(new Gson().toJson(req).getBytes());
        return response;
    }


    @Override
    public boolean rejectRequest() {
        return false;
    }

    @Override
    public int cmdId() {
        return AgentCmd.monitorReq;
    }
}
