package com.xiaomi.mone.log.api.service;

import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/8/4 19:38
 */
public interface AgentConfigService {
    /**
     * 获取日志配置信息
     * @param ip
     * @return
     */
    LogCollectMeta getLogCollectMetaFromManager(String ip);
}
