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

import com.xiaomi.mone.es.EsClient;
import com.xiaomi.mone.log.api.model.dto.TraceLogDTO;
import com.xiaomi.mone.log.api.model.vo.TraceLogQuery;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.dto.LogDTO;
import com.xiaomi.mone.log.manager.model.dto.LogDataDTO;
import com.xiaomi.mone.log.manager.model.vo.LogQuery;
import com.xiaomi.youpin.docean.Ioc;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TraceLogServiceTest {
    @Test
    public void testQueryByIndex() throws Exception {
        Ioc.ins().init("com.xiaomi");
        LogQueryServiceImpl esDataService = Ioc.ins().getBean(LogQueryServiceImpl.class);
        LogQuery logQuery = new LogQuery();
        logQuery.setLogstore("auto_create_index-2021.08.05");
        Map<String, Object> params = new HashMap<>();
        params.put("message", "bb");
        params.put("ip", "192");
        Result<LogDTO> result = esDataService.logQuery(logQuery);
        for (LogDataDTO logData : result.getData().getLogDataDTOList()) {
            System.out.println(logData.getLogOfString());
        }
    }

    @Test
    public void logQuery() throws Exception {
        LogQuery logQuery = new LogQuery();
        logQuery.setLogstore("milog_store_test");
        logQuery.setStartTime(1628158918793l);
        logQuery.setEndTime(1628763718793l);
        logQuery.setPageSize(2);
        logQuery.setFullTextSearch("INFO");
        Ioc.ins().init("com.xiaomi");
        LogQueryServiceImpl esDataService = Ioc.ins().getBean(LogQueryServiceImpl.class);
        Result<LogDTO> logDTOResult = esDataService.logQuery(logQuery);
        System.out.println(logDTOResult.getData());
    }

    @Test
    public void insertSingleDoc() throws IOException {
        EsClient client = new EsClient("127.0.0.1:80", "", "");
        String indexName = "milog_insert_test-" + new SimpleDateFormat("yyyy.MM.dd").format(new Date());
        long current = System.currentTimeMillis();
        client.insertDocJson(indexName,"");
    }

    @Test
    public void getTraceLog() throws IOException {
        Ioc.ins().init("com.xiaomi");
        LogQueryServiceImpl esDataService = Ioc.ins().getBean(LogQueryServiceImpl.class);
        TraceLogQuery query = new TraceLogQuery();
        query.setAppId(667l);
        query.setIp("127.0.0.1");
        query.setTraceId("");
        TraceLogDTO traceLog = esDataService.getTraceLog(query);
        for (String log : traceLog.getDataList()) {
            System.out.println(log);
        }
    }

    @Test
    public void collectLogCount() throws IOException {
        Ioc.ins().init("com.xiaomi");
        LogCountServiceImpl logCountService = Ioc.ins().getBean(LogCountServiceImpl.class);
        logCountService.collectLogCount("");
    }

}