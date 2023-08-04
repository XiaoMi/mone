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
package com.xiaomi.mone.log.manager.test;

import com.google.gson.Gson;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.MilogSpaceParam;
import com.xiaomi.mone.log.manager.model.bo.LogTailParam;
import com.xiaomi.mone.log.manager.model.dto.MilogAppEnvDTO;
import com.xiaomi.mone.log.manager.service.impl.LogSpaceServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.LogTailServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.MilogConfigNacosServiceImpl;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/19 13:04
 */
@Slf4j
public class MilogConfigServiceTest {

    @Test
    public void testCreateNameSpace() {
        Ioc.ins().init("com.xiaomi");
        LogSpaceServiceImpl milogSpaceService = Ioc.ins().getBean(LogSpaceServiceImpl.class);
        MilogSpaceParam ms = new MilogSpaceParam();
        ms.setSpaceName("上山打老虎楼");
        ms.setDescription("我是测试人员啊");
        milogSpaceService.newMilogSpace(ms);
        Assert.assertNotNull(milogSpaceService);
    }

    @Test
    public void testDelNameSpace() {
        Ioc.ins().init("com.xiaomi");
        LogSpaceServiceImpl milogSpaceService = Ioc.ins().getBean(LogSpaceServiceImpl.class);
        milogSpaceService.deleteMilogSpace(2L);
        Assert.assertNotNull(milogSpaceService);
    }

    @Test
    public void testCreateLogTail() {
        Ioc.ins().init("com.xiaomi");
        LogTailServiceImpl milogSpaceService = Ioc.ins().getBean(LogTailServiceImpl.class);
        LogTailParam param = new LogTailParam();
        param.setSpaceId(37L);
        param.setStoreId(1L);
        param.setParseType(1);
        param.setParseScript("|");
        param.setLogPath("/home/work/log/xxx/server.log");
        param.setValueList("0,1,2,3,4,5,6");
        milogSpaceService.newMilogLogTail(param);
        Assert.assertNotNull(milogSpaceService);
    }

    @Test
    public void testDelLogTail() {
        Ioc.ins().init("com.xiaomi");
        LogTailServiceImpl milogSpaceService = Ioc.ins().getBean(LogTailServiceImpl.class);
        milogSpaceService.deleteLogTail(1L);
        Assert.assertNotNull(milogSpaceService);
    }

    @Test
    public void testDubbo() {
        Ioc.ins().init("com.xiaomi");
        MilogConfigNacosServiceImpl service = Ioc.ins().getBean(MilogConfigNacosServiceImpl.class);
        log.info("机器信息:{}");
    }

    @Test
    public void testIp() {
        Ioc.ins().init("com.xiaomi");
        LogTailServiceImpl service = Ioc.ins().getBean(LogTailServiceImpl.class);
        Result<List<MilogAppEnvDTO>> query = service.getEnInfosByAppId(305L, 1);
        log.info("机器信息:{}", new Gson().toJson(query.getData()));
        Assert.assertNotNull(query);
    }
}
