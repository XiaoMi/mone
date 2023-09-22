package com.xiaomi.hera.trace.etl.api.service;

import com.xiaomi.hera.trace.etl.domain.HeraTraceEtlConfig;

public interface TraceEtlService {

    void insertConfig(HeraTraceEtlConfig config);

    void updateConfig(HeraTraceEtlConfig config);

    void deleteConfig(HeraTraceEtlConfig config);

}
