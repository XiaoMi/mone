package com.xiaomi.mone.log.manager.controller;

import com.xiaomi.mone.log.api.model.vo.AgentLogProcessDTO;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.bo.MilogAgentIpParam;
import com.xiaomi.mone.log.manager.service.extension.agent.MilogAgentService;
import com.xiaomi.mone.log.manager.service.extension.agent.MilogAgentServiceFactory;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;

import java.util.List;

@Controller
public class MIlogAgentManageController {

    private MilogAgentService milogAgentService;

    public void init() {
        milogAgentService = MilogAgentServiceFactory.getAgentExtensionService();
    }

    /**
     * 日志收集进度
     *
     * @param ip
     * @return
     */
    @RequestMapping(path = "/milog/meta/process", method = "get")
    public Result<List<AgentLogProcessDTO>> process(@RequestParam(value = "ip") String ip) {
        return milogAgentService.process(ip);
    }

    /**
     * agent下发配置--全量下发
     *
     * @param agentId
     * @param agentIp
     * @param agentMachine
     * @return
     */
    @RequestMapping(path = "/milog/agent/config/issue", method = "get")
    public Result<String> configIssueAgent(@RequestParam(value = "agentId") String agentId,
                                           @RequestParam(value = "agentIp") String agentIp,
                                           @RequestParam("agentMachine") String agentMachine) {
        return milogAgentService.configIssueAgent(agentId, agentIp, agentMachine);
    }

    /**
     * 部署
     *
     * @param agentIpParam
     * @return
     */
    //TODO 待完成
    @RequestMapping(path = "/milog/agent/deployee")
    public Result<String> agentDeploy(@RequestParam("ips") MilogAgentIpParam agentIpParam) {
        return null;
    }

    /**
     * 下线
     *
     * @param agentIpParam
     * @return
     */
    //TODO 待完成
    @RequestMapping(path = "/milog/agent/offline/batch")
    public Result<String> agentOfflineBatch(@RequestParam("param") MilogAgentIpParam agentIpParam) {
        return milogAgentService.agentOfflineBatch(agentIpParam);
    }

    /**
     * 升级
     *
     * @param ip
     * @return
     */
    //TODO 待完成
    @RequestMapping(path = "/milog/agent/upgrade")
    public Result<String> agentUpgrade(@RequestParam("ip") String ip) {
        return null;
    }
}
