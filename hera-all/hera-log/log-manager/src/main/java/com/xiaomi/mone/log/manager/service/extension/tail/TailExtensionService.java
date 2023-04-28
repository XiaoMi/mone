package com.xiaomi.mone.log.manager.service.extension.tail;

import com.xiaomi.mone.log.manager.model.bo.MilogLogtailParam;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;
import com.xiaomi.mone.log.manager.model.pojo.MilogMiddlewareConfig;
import com.xiaomi.mone.log.model.LogtailConfig;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/4/10 17:06
 */
public interface TailExtensionService {
    String DEFAULT_TAIL_EXTENSION_SERVICE_KEY = "defaultTailExtensionService";

    boolean tailHandlePreprocessingSwitch(MilogLogStoreDO milogLogStore, MilogLogtailParam param);

    boolean bindMqResourceSwitch(Integer appType);

    boolean bindPostProcessSwitch(Long storeId);

    void postProcessing();

    void defaultBindingAppTailConfigRel(Long id, Long milogAppId,
                                        Long middleWareId, String topicName, Integer batchSendSize);

    void defaultBindingAppTailConfigRelPostProcess(Long spaceId, Long storeId, Long tailId, Long milogAppId, Long storeMqResourceId);

    void sendMessageOnCreate(MilogLogtailParam param, MilogLogTailDo mt, Long milogAppId, boolean supportedConsume);

    void updateSendMsg(MilogLogTailDo milogLogtailDo, List<String> oldIps, boolean supportedConsume);

    void logTailDoExtraFiled(MilogLogTailDo milogLogtailDo, MilogLogStoreDO logStoreDO, MilogLogtailParam logTailParam);

    void logTailConfigExtraField(LogtailConfig logtailConfig, MilogMiddlewareConfig middlewareConfig);
}
