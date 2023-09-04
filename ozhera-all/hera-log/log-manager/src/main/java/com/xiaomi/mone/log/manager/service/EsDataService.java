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
package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.log.api.model.dto.TraceLogDTO;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.dto.EsStatisticResult;
import com.xiaomi.mone.log.manager.model.dto.LogDTO;
import com.xiaomi.mone.log.manager.model.vo.LogQuery;
import com.xiaomi.mone.log.manager.model.vo.RegionTraceLogQuery;

import java.io.IOException;
import java.util.Map;

public interface EsDataService {

    /**
     * 读取ES索引中数据
     *
     * @param logQuery
     * @return
     */
    Result<LogDTO> logQuery(LogQuery logQuery);

    /**
     * 插入数据
     *
     * @param indexName
     * @param data
     * @return
     */
    void insertDoc(String indexName, Map<String, Object> data) throws IOException;

    Result<EsStatisticResult> EsStatistic(LogQuery param) throws Exception;

    /**
     * 获取机房内trace日志
     *
     * @param regionTraceLogQuery
     * @return
     */
    Result<TraceLogDTO> queryRegionTraceLog(RegionTraceLogQuery regionTraceLogQuery) throws IOException;

}
