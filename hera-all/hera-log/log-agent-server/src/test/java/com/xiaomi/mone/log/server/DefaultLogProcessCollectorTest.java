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
package com.xiaomi.mone.log.server;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xiaomi.mone.log.api.model.vo.UpdateLogProcessCmd;
import com.xiaomi.mone.log.server.service.DefaultLogProcessCollector;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/1/4 11:14
 */
@Slf4j
public class DefaultLogProcessCollectorTest {

    DefaultLogProcessCollector processCollector;
    Gson gson;

    @Before
    public void buildBean() {
        Ioc.ins().init("com.xiaomi");
        processCollector = Ioc.ins().getBean(DefaultLogProcessCollector.class);
        gson = new GsonBuilder().create();
    }

    @Test
    public void testCollectLogProcess() {
        UpdateLogProcessCmd updateLogProcessCmd = new UpdateLogProcessCmd();
        List<UpdateLogProcessCmd.CollectDetail> collectList = Lists.newArrayList();
        UpdateLogProcessCmd.CollectDetail collectDetail = new UpdateLogProcessCmd.CollectDetail();
        List<UpdateLogProcessCmd.FileProgressDetail> fileProgressDetails = Lists.newArrayList();
        UpdateLogProcessCmd.FileProgressDetail progressDetail = new UpdateLogProcessCmd.FileProgressDetail();
        progressDetail.setPattern("/home/work/log/test/server.log");
        progressDetail.setCollectPercentage("98%");
        fileProgressDetails.add(progressDetail);
        collectDetail.setFileProgressDetails(fileProgressDetails);
        collectList.add(collectDetail);
        updateLogProcessCmd.setCollectList(collectList);
        updateLogProcessCmd.setIp("127.0.0.1");
        processCollector.collectLogProcess(updateLogProcessCmd);
        List<UpdateLogProcessCmd.CollectDetail> colProcessImperfect = processCollector.getColProcessImperfect(0.97);
        log.info("result:{}", gson.toJson(colProcessImperfect));
    }

    @Test
    public void testGetColProcessImperfect() {
        List<UpdateLogProcessCmd.CollectDetail> colProcessImperfect = processCollector.getColProcessImperfect(0.98);
        log.info("result:{}", gson.toJson(colProcessImperfect));
    }
}
