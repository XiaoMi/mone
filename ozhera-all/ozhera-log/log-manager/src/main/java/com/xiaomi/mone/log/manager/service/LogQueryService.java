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
import com.xiaomi.mone.log.manager.model.vo.LogContextQuery;
import com.xiaomi.mone.log.manager.model.vo.LogQuery;
import com.xiaomi.mone.log.manager.model.vo.RegionTraceLogQuery;

import java.io.IOException;

public interface LogQueryService {

    /**
     * 读取ES索引中数据
     *
     * @param logQuery
     * @return
     */
    Result<LogDTO> logQuery(LogQuery logQuery) throws Exception;

    Result<EsStatisticResult> EsStatistic(LogQuery param) throws Exception;

    /**
     * 获取机房内trace日志
     *
     * @param regionTraceLogQuery
     * @return
     */
    Result<TraceLogDTO> queryRegionTraceLog(RegionTraceLogQuery regionTraceLogQuery) throws IOException;

    /**
     * 获取日志上下文
     * @param logContextQuery
     * @return
     */
    Result<LogDTO> getDocContext(LogContextQuery logContextQuery);

    /**
     * 日志导出
     * @param logQuery
     * @throws Exception
     */
    void logExport(LogQuery logQuery) throws Exception;
}
