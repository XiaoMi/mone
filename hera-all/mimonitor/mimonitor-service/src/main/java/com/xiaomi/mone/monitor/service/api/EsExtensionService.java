package com.xiaomi.mone.monitor.service.api;

import com.xiaomi.mone.es.EsClient;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.model.middleware.DbInstanceQuery;
import com.xiaomi.mone.monitor.service.model.prometheus.MetricDetailQuery;

import java.io.IOException;

public interface EsExtensionService {

    String getIndex(MetricDetailQuery param);

    EsClient getEsClient(Integer appSource);

    Result queryMiddlewareInstance(DbInstanceQuery param, Integer page, Integer pageSize, Long esQueryTimeout) throws IOException;
}
