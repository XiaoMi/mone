/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package run.mone.mimeter.agent.manager.processor;

import com.google.common.collect.Lists;
import com.xiami.mione.tesla.k8s.service.K8sProxyService;
import com.xiaomi.data.push.context.AgentContext;
import com.xiaomi.data.push.rpc.common.RemotingHelper;
import com.xiaomi.data.push.rpc.netty.AgentChannel;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.mione.tesla.k8s.bo.PodNode;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import io.netty.channel.ChannelHandlerContext;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import run.mone.mimeter.agent.manager.bo.AgentInfo;
import run.mone.mimeter.engine.agent.bo.data.AgentInfoDTO;
import run.mone.mimeter.engine.agent.bo.data.AgentReq;
import run.mone.mimeter.engine.agent.bo.MibenchCmd;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * @author goodjava@qq.com
 * @date 2022/5/11
 * <p>
 * 处理agent 发过来的ping信息
 */
@Component
public class PingProcessor implements NettyRequestProcessor {

    private static final Logger log = LoggerFactory.getLogger(PingProcessor.class);


    @Reference(interfaceClass = K8sProxyService.class, group = "${k8s.proxy.dubbo.group}", timeout = 10000, check = false)
    private K8sProxyService k8sProxyService;

    @Resource(name = "$daoName:mibench_st_db", description = "mysql")
    private NutDao dao;

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand remotingCommand) throws Exception {
        AgentReq req = remotingCommand.getReq(AgentReq.class);
        Optional.ofNullable(req.getUser()).ifPresent(it -> {
            log.info("client:{} ping", req.getUser().getName());
            final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
            AgentChannel ch = AgentContext.ins().map.get(remoteAddress);
            if (null == ch) {
                ch = new AgentChannel();
                ch.setChannel(ctx.channel());
                AgentContext.ins().map.putIfAbsent(remoteAddress, ch);
            }
            // 机器信息入库
            insertAgentInfoToDB(req);
        });

        RemotingCommand res = RemotingCommand.createResponseCommand(100, "ok");
        res.setBody("ok".getBytes(StandardCharsets.UTF_8));
        return res;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    @Override
    public int cmdId() {
        return MibenchCmd.PING;
    }

    /**
     * agent入库
     *
     * @param req
     */
    private void insertAgentInfoToDB(AgentReq req) {
        try {
            AgentInfoDTO agentInfoDTO = req.getAgentInfoDTO();
            long now = System.currentTimeMillis();
            if (agentInfoDTO.getIp() == null || agentInfoDTO.getIp().equals("")) {
                return;
            }

            String nodeIp = "";
            List<PodNode> agents = k8sProxyService.getNodeIP(Lists.newArrayList(agentInfoDTO.getIp())).getData();
            if (agents != null && agents.size() != 0) {
                log.info("[PingProcessor.insertAgentInfoToDB], getNodeIP agents: {}, podIp: {}", agents, agentInfoDTO.getIp());
                nodeIp = agents.get(0).getNodeIP();
            } else {
                //如果最终获取不到nodeId
                nodeIp = agentInfoDTO.getIp() + "_" + agentInfoDTO.getDesc();
            }
            AgentInfo agentInfo = StringUtils.isEmpty(nodeIp)
                    ? null
                    : dao.fetch(AgentInfo.class, Cnd.where("node_ip", "=", nodeIp));

            if (agentInfo != null) {
                //更新
                agentInfo.setUseCpu(agentInfoDTO.getUseCpu());
                agentInfo.setUseMem(agentInfoDTO.getUseMem());
                agentInfo.setIp(agentInfoDTO.getIp());
                agentInfo.setEnable(true);
                agentInfo.setUtime(now);
                agentInfo.setClientDesc(agentInfoDTO.getDesc());
                log.info("old !!! update agent info podIp:{}, nodeIp:{}, agentInfo: {}", agentInfoDTO.getIp(), nodeIp, agentInfo);
                dao.update(agentInfo);
            } else {
                AgentInfo ipAgentInfo = dao.fetch(AgentInfo.class, Cnd.where("ip", "=", agentInfoDTO.getIp()));
                if (ipAgentInfo != null) {
                    ipAgentInfo.setNodeIp(nodeIp);
                    ipAgentInfo.setUtime(now);
                    ipAgentInfo.setEnable(true);
                    log.info("new !!! update agent info podIp:{},nodeIp:{}", agentInfoDTO.getIp(), nodeIp);
                    dao.update(ipAgentInfo);
                } else {
                    agentInfo = new AgentInfo();
                    BeanUtils.copyProperties(agentInfoDTO, agentInfo);
                    agentInfo.setCtime(now);
                    agentInfo.setUtime(now);
                    agentInfo.setEnable(true);
                    agentInfo.setClientDesc(agentInfoDTO.getDesc());
                    agentInfo.setNodeIp(nodeIp);
                    log.info("new!!! insert agent info podIp:{},nodeIp:{}", agentInfoDTO.getIp(), nodeIp);
                    dao.insert(agentInfo);
                }
            }
        } catch (Exception e) {
            log.error("update agent info error:{}", e.getMessage());
        }
    }


}
