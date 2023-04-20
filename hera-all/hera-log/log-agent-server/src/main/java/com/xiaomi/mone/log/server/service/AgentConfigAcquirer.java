package com.xiaomi.mone.log.server.service;

import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/12/6 14:29
 */
public interface AgentConfigAcquirer {

    /**
     * 获取日志配置信息
     * @param ip
     * @return
     */
    LogCollectMeta getLogCollectMetaFromManager(String ip);
}
