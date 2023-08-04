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
package com.xiaomi.mone.log.api.service;

import com.xiaomi.mone.log.api.model.bo.MiLogMoneTransfer;
import com.xiaomi.mone.log.api.model.dto.MontorAppDTO;
import com.xiaomi.mone.log.api.model.vo.MiLogMoneEnv;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/12/8 14:53pom
 */
public interface MilogOpenService {
    /**
     * 查询是否接入了日志系统
     *
     * @param iamTreeId
     * @return
     */
    MontorAppDTO queryHaveAccessMilog(Long iamTreeId, String bingId, Integer platformType);

    /**
     * 查询接入的spaceId 最后一个
     *
     * @param iamTreeId
     * @return
     */
    Long querySpaceIdByIamTreeId(Long iamTreeId);

    /**
     * mione迁移 数据清洗
     */
    MiLogMoneTransfer ypMoneEnvTransfer(MiLogMoneEnv logMoneEnv);
}
