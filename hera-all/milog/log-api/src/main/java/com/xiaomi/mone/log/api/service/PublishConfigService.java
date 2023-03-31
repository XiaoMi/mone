package com.xiaomi.mone.log.api.service;

import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/12/6 17:42
 */
public interface PublishConfigService {

    void sengConfigToAgent(final String agentIp, LogCollectMeta logCollectMeta);

    List<String> getAllAgentList();
}
