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
package com.xiaomi.mone.log.manager.common.validation;

import com.google.common.collect.Lists;
import com.xiaomi.mone.log.api.model.vo.MiLogMoneEnv;
import com.xiaomi.youpin.docean.anno.Component;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.SYMBOL_COMMA;

/**
 * @author: wtt
 * @date: 2022/5/24 18:32
 * @description: Third-party application access parameter verification
 */
@Slf4j
@Component
public class OpenSourceValid {


    public String validMiLogMoneEnv(MiLogMoneEnv logMoneEnv) {
        List<String> errorInfos = Lists.newArrayList();
        if (null == logMoneEnv.getNewAppId() || null == logMoneEnv.getOldAppId()) {
            errorInfos.add("appId Cannot be empty");
        }
        if (null == logMoneEnv.getNewEnvId() || null == logMoneEnv.getOldEnvId()) {
            errorInfos.add("envId Cannot be empty");
        }
        if (StringUtils.isBlank(logMoneEnv.getNewAppName()) ||
                StringUtils.isBlank(logMoneEnv.getOldAppName())) {
            errorInfos.add("appName Cannot be empty");
        }
        if (StringUtils.isBlank(logMoneEnv.getNewEnvName()) ||
                StringUtils.isBlank(logMoneEnv.getOldEnvName())) {
            errorInfos.add("envName Cannot be empty");
        }
        return errorInfos.stream().collect(Collectors.joining(SYMBOL_COMMA));
    }
}
