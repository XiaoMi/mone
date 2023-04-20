package com.xiaomi.mone.log.api.service;

import com.xiaomi.mone.log.api.model.dto.TraceLogDTO;
import com.xiaomi.mone.log.api.model.vo.TraceLogQuery;

import java.io.IOException;

public interface LogDataService {
    /**
     * 获取trace日志
     * @param logQuery
     * @return
     */
    TraceLogDTO getTraceLog(TraceLogQuery logQuery) throws IOException;
}
