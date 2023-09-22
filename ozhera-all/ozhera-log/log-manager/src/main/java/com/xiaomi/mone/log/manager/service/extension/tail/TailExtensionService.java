/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.service.extension.tail;

import com.xiaomi.mone.app.api.response.AppBaseInfo;
import com.xiaomi.mone.log.manager.model.bo.LogTailParam;
import com.xiaomi.mone.log.manager.model.dto.MilogAppEnvDTO;
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

    boolean tailHandlePreprocessingSwitch(MilogLogStoreDO milogLogStore, LogTailParam param);

    boolean bindMqResourceSwitch(Integer appType);

    boolean bindPostProcessSwitch(Long storeId);

    void postProcessing();

    void defaultBindingAppTailConfigRel(Long id, Long milogAppId, Long middleWareId, String topicName, Integer batchSendSize);

    void defaultBindingAppTailConfigRelPostProcess(Long spaceId, Long storeId, Long tailId, Long milogAppId, Long storeMqResourceId);

    void sendMessageOnCreate(LogTailParam param, MilogLogTailDo mt, Long milogAppId, boolean supportedConsume);

    void updateSendMsg(MilogLogTailDo milogLogtailDo, List<String> oldIps, boolean supportedConsume);

    void logTailDoExtraFiled(MilogLogTailDo milogLogtailDo, MilogLogStoreDO logStoreDO, LogTailParam logTailParam);

    void logTailConfigExtraField(LogtailConfig logtailConfig, MilogMiddlewareConfig middlewareConfig);

    void logTailDelPostProcess(MilogLogStoreDO logStoreDO, MilogLogTailDo milogLogtailDo);

    List<MilogAppEnvDTO> getEnInfosByAppId(AppBaseInfo appBaseInfo, Long milogAppId, Integer deployWay);

    boolean decorateTailDTOValId(Integer appType);

    List<String> getStreamMachineUniqueList(Integer projectTypeCode, String motorRoomEn);

    String deleteCheckProcessPre(Long id);

    String validLogPath(LogTailParam param);
}
