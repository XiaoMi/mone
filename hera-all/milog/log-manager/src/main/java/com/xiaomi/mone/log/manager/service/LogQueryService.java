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
