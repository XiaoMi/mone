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
import com.xiaomi.mone.log.api.enums.LogStructureEnum;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import static com.xiaomi.mone.log.manager.common.utils.ManagerUtil.getConfigFromNanos;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/6/13 15:06
 */
@Slf4j
public class LogTailServiceImplTest {

    private LogTailServiceImpl logTailService;
    private Gson gson;

    @Before
    public void before() {
        getConfigFromNanos();
        Ioc.ins().init("com.xiaomi");
        logTailService = Ioc.ins().getBean(LogTailServiceImpl.class);
        gson = new Gson();
    }

    @Test
    public void deleteConfigRemoteTest() {
        Long spaceId = 2L;
        Long id = 284L;
        String motorRoomEn = "cn";
        LogStructureEnum logStructureEnum = LogStructureEnum.TAIL;
        logTailService.deleteConfigRemote(spaceId, id, motorRoomEn, logStructureEnum);
        log.info("deleteConfigRemoteTest");
    }
}
