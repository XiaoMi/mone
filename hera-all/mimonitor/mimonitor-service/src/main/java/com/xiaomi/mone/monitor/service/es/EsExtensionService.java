package com.xiaomi.mone.monitor.service.es;

import com.xiaomi.mone.es.EsClient;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.model.middleware.DbInstanceQuery;

import java.io.IOException;

public interface EsExtensionService {

    String getIndex();

    EsClient getEsClient(Integer appSource);

    Result queryMiddlewareInstance(DbInstanceQuery param, Integer page, Integer pageSize, Long esQueryTimeout) throws IOException;
}
