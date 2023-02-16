package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.bo.MilogAgentIpParam;
import com.xiaomi.mone.log.manager.model.dto.AgentLogProcessDTO;

import java.util.List;

public interface MilogAgentService {

    public Result<List<AgentLogProcessDTO>> process(String ip);

    Result<String> configIssueAgent(String agentId, String agentIp, String agentMachine);

    void publishIncrementConfig(Long tailId, Long milogAppId, List<String> ips);

    void publishIncrementDel(Long tailId, Long milogAppId, List<String> ips);

    Result<String> agentOfflineBatch(MilogAgentIpParam agentIpParam);
}
