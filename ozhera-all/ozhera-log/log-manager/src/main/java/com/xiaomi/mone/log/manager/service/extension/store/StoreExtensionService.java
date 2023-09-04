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
package com.xiaomi.mone.log.manager.service.extension.store;

import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.vo.LogStoreParam;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/4/10 16:13
 */
public interface StoreExtensionService {

    String DEFAULT_STORE_EXTENSION_SERVICE_KEY = "defaultStoreExtensionService";

    boolean storeInfoCheck(LogStoreParam param);

    /**
     * resource bind
     *
     * @param ml
     * @param cmd
     * @param operateEnum
     */
    void storeResourceBinding(MilogLogStoreDO ml, LogStoreParam cmd, OperateEnum operateEnum);

    /**
     * Additional post processing
     *
     * @param ml
     * @param cmd
     */
    void postProcessing(MilogLogStoreDO ml, LogStoreParam cmd);

    /**
     * Send Configuration Switch
     *
     * @return
     */
    boolean sendConfigSwitch(LogStoreParam param);

    void deleteStorePostProcessing(MilogLogStoreDO logStoreD);

    String getMangerEsLabel();

    boolean updateLogStore(MilogLogStoreDO ml);

    boolean isNeedSendMsgType(Integer logType);
}
