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
package com.xiaomi.mone.log.manager.service.extension.common;

import com.xiaomi.mone.log.api.enums.MachineRegionEnum;
import com.xiaomi.mone.log.api.enums.MiddlewareEnum;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;

import static com.xiaomi.mone.log.common.Constant.DEFAULT_STREAM_SERVER_NAME;
import static com.xiaomi.mone.log.common.Constant.LOG_MANAGE_PREFIX;
import static com.xiaomi.mone.log.manager.service.extension.common.CommonExtensionService.DEFAULT_COMMON_EXTENSION_SERVICE_KEY;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/4/27 16:13
 */
@Service(name = DEFAULT_COMMON_EXTENSION_SERVICE_KEY)
@Slf4j
public class DefaultCommonExtensionService implements CommonExtensionService {

    @Override
    public String getLogManagePrefix() {
        return LOG_MANAGE_PREFIX;
    }

    @Override
    public String getHeraLogStreamServerName() {
        return DEFAULT_STREAM_SERVER_NAME;
    }

    @Override
    public String getMachineRoomName(String machineRoomEn) {
        return MachineRegionEnum.queryCnByEn(machineRoomEn);
    }

    @Override
    public boolean middlewareEnumValid(Integer type) {
        return MiddlewareEnum.ROCKETMQ.getCode().equals(type);
    }
}
