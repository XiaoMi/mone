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
package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;

/**
 * @author shanwb
 * @date 2021-07-08
 */
public interface MiLogMetaManageService {

    /**
     * 获取指定agent的日志采集元数据信息
     *
     * @param agentId
     * @param agentIp
     * @return
     */
    LogCollectMeta queryLogCollectMeta(String agentId, String agentIp);

}
