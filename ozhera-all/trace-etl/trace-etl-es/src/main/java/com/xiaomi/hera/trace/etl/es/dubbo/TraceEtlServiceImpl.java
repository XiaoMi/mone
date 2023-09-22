package com.xiaomi.hera.trace.etl.es.dubbo;

import com.xiaomi.hera.trace.etl.api.service.TraceEtlService;
import com.xiaomi.hera.trace.etl.domain.HeraTraceEtlConfig;
import com.xiaomi.hera.trace.etl.es.config.TraceConfig;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/4/25 3:09 pm
 */
@Service(cluster = "broadcast",group = "${dubbo.group}")
public class TraceEtlServiceImpl implements TraceEtlService {

    @Autowired
    private TraceConfig traceConfig;

    @Override
    public void insertConfig(HeraTraceEtlConfig config) {
        traceConfig.insert(config);
    }

    @Override
    public void updateConfig(HeraTraceEtlConfig config) {
        traceConfig.update(config);
    }

    @Override
    public void deleteConfig(HeraTraceEtlConfig config) {
        traceConfig.delete(config);
    }
}
