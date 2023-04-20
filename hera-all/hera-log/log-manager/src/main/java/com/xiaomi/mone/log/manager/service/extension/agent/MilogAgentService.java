package com.xiaomi.mone.log.manager.service.extension.agent;

import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;
import com.xiaomi.mone.log.api.model.vo.AgentLogProcessDTO;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.bo.MilogAgentIpParam;

import java.util.List;

public interface MilogAgentService {

    String DEFAULT_AGENT_EXTENSION_SERVICE_KEY = "defaultAgentExtensionService";

    String LOG_PATH_PREFIX = "/home/work/log";

    Result<List<AgentLogProcessDTO>> process(String ip);

    Result<String> configIssueAgent(String agentId, String agentIp, String agentMachine);

    void publishIncrementConfig(Long tailId, Long milogAppId, List<String> ips);

    void publishIncrementDel(Long tailId, Long milogAppId, List<String> ips);

    Result<String> agentOfflineBatch(MilogAgentIpParam agentIpParam);

    LogCollectMeta getLogCollectMetaFromManager(String ip);
}
