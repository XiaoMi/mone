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

import com.alibaba.nacos.api.config.ConfigService;
import com.google.gson.Gson;
import com.xiaomi.mone.log.api.enums.MachineRegionEnum;
import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.api.enums.ProjectTypeEnum;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;
import com.xiaomi.mone.log.manager.service.impl.LogSpaceServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.LogTailServiceImpl;
import com.xiaomi.mone.log.manager.service.impl.MilogStreamServiceImpl;
import com.xiaomi.mone.log.manager.service.nacos.MultipleNacosConfig;
import com.xiaomi.mone.log.manager.service.nacos.impl.StreamConfigNacosProvider;
import com.xiaomi.mone.log.manager.service.nacos.impl.StreamConfigNacosPublisher;
import com.xiaomi.mone.log.model.MiLogStreamConfig;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/15 15:30
 */
@Slf4j
public class ConfigNacosServiceTest {

    Gson gson;

    @Before
    public void init() {
        gson = new Gson();
    }

    @Test
    public void testNacosOk() {
        Ioc.ins().init("com.xiaomi");
        StreamConfigNacosPublisher nacosPublisher = Ioc.ins().getBean(StreamConfigNacosPublisher.class);
        ConfigService configService = MultipleNacosConfig.getConfigService("127.0.0.1:80");
        nacosPublisher.setConfigService(configService);
        MiLogStreamConfig miLogStreamConfig = new MiLogStreamConfig();
        Map<String, Map<Long, String>> config = new ConcurrentHashMap<>();
        config.put("1", new HashMap<>());
        miLogStreamConfig.setConfig(config);
        nacosPublisher.publish("logmanager", miLogStreamConfig);
    }

    @Test
    public void testQueryDataFromNacos() {
        Ioc.ins().init("com.xiaomi");
        StreamConfigNacosProvider nacosProvider = Ioc.ins().getBean(StreamConfigNacosProvider.class);
        MiLogStreamConfig config = nacosProvider.getConfig("");
        log.info(gson.toJson(config));
        Assert.assertNull(config);
    }

    @Test
    public void testNacosPushData() {
        Ioc.ins().init("com.xiaomi");
        LogSpaceServiceImpl milogSpaceService = Ioc.ins().getBean(LogSpaceServiceImpl.class);
//        milogSpaceService.test();
    }

    @Test
    public void testConfigIssue() {
        Ioc.ins().init("com.xiaomi");
        MilogStreamServiceImpl milogStreamService = Ioc.ins().getBean(MilogStreamServiceImpl.class);
        Result<String> result = milogStreamService.configIssueStream("127.0.0.1");
        Assert.assertNotNull(result);
    }

    @Test
    public void testSyncSpace() {
        Ioc.ins().init("com.xiaomi");
        LogTailServiceImpl milogLogtailService = Ioc.ins().getBean(LogTailServiceImpl.class);
        MilogLogTailDo mt = new MilogLogTailDo();
        mt.setSpaceId(3L);
        milogLogtailService.handleNaocsConfigByMotorRoom(mt, MachineRegionEnum.CN_MACHINE.getEn(), OperateEnum.ADD_OPERATE.getCode(), ProjectTypeEnum.MIONE_TYPE.getCode());
        Assert.assertNotNull(true);
    }
}
