package com.xiaomi.hera.trace.etl.api.service;

import com.xiaomi.hera.trace.etl.domain.HeraTraceEtlConfig;
import com.xiaomi.youpin.infra.rpc.Result;

import java.util.List;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/4/24 7:35 下午
 */
public interface TraceManagerService {
    Result<List<HeraTraceEtlConfig>> getAll();
}
