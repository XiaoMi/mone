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

import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;
import com.xiaomi.youpin.docean.Ioc;
import org.junit.Test;

public class MiLogMetaManageServiceImplTest {

    @Test
    public void queryLogCollectMeta() {
        Ioc.ins().init("com.xiaomi");
        MiLogMetaManageServiceImpl miLogMetaManageService = Ioc.ins().getBean(MiLogMetaManageServiceImpl.class);
        LogCollectMeta logCollectMeta = miLogMetaManageService.queryLogCollectMeta("", "127.0.0.1");
        System.out.println(logCollectMeta);
    }
}