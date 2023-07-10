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
package com.xiaomi.mone.log.manager.service.impl;

import com.xiaomi.mone.app.api.model.HeraSimpleEnv;
import com.xiaomi.mone.app.api.service.HeraAppEnvOutwardService;
import com.xiaomi.mone.log.manager.service.HeraAppEnvService;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/12 11:48
 */
@Slf4j
@Service
public class HeraAppEnvServiceImpl implements HeraAppEnvService {

    @Reference(interfaceClass = HeraAppEnvOutwardService.class, group = "$dubbo.env.group", check = false)
    private HeraAppEnvOutwardService heraAppEnvOutwardService;

    @Override
    public List<HeraSimpleEnv> querySimpleEnvAppBaseInfoId(Integer appBaseId) {
        return heraAppEnvOutwardService.querySimpleEnvAppBaseInfoId(appBaseId);
    }
}
