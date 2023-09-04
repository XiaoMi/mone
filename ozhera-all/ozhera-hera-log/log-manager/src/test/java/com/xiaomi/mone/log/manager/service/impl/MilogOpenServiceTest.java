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

import com.google.gson.Gson;
import com.xiaomi.mone.log.api.model.dto.MontorAppDTO;
import com.xiaomi.youpin.docean.Ioc;
import org.junit.Test;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/12/8 14:58
 */
public class MilogOpenServiceTest {

    private Gson gson = new Gson();

    @Test
    public void testHaveAccess() {
        Ioc.ins().init("com.xiaomi");
        MilogOpenServiceImpl milogOpenService = Ioc.ins().getBean(MilogOpenServiceImpl.class);
        MontorAppDTO montorAppDTO = milogOpenService.queryHaveAccessMilog(17465L, "", null);
        System.out.println("返回值:" + gson.toJson(montorAppDTO));
    }

    @Test
    public void testSpaceId() {
        Ioc.ins().init("com.xiaomi");
        MilogOpenServiceImpl milogOpenService = Ioc.ins().getBean(MilogOpenServiceImpl.class);
        Long spaceIdLast = milogOpenService.querySpaceIdByIamTreeId(17700L);
        System.out.println(spaceIdLast);
    }
}
