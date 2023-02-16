package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.log.common.Result;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/1/5 10:55
 */
public interface MiLogToolService {
    Result<String> sendLokiMsg(Long tailId);

    void fixAlertAppId();

    void fixMilogAlertTailId();

    String fixResourceLabel();

    String fixLogStoreMqResourceId(Long storeId);

    String fixNacosEsInfo(Long spaceId);

    void fixLogTailLogAppId(String appName);
}
