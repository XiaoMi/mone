package com.xiaomi.hera.trace.etl.config;

import com.xiaomi.hera.trace.etl.domain.HeraTraceConfigVo;
import com.xiaomi.hera.trace.etl.domain.HeraTraceEtlConfig;
import com.xiaomi.hera.trace.etl.service.ManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description trace config
 * @Author dingtao
 * @Date 2022/4/25 3:12 下午
 */
@Configuration
@Slf4j
public class TraceConfig {

    private ConcurrentHashMap<String, HeraTraceEtlConfig> heraTraceConfig = new ConcurrentHashMap<>();

    @Autowired
    private ManagerService managerService;

    @PostConstruct
    public void init() {
        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(() -> {
            try {
                List<HeraTraceEtlConfig> all = managerService.getAll(new HeraTraceConfigVo());
                for (HeraTraceEtlConfig config : all) {
                    heraTraceConfig.put(getServiceName(config), config);
                }
            }catch(Throwable t){
                log.error("schedule trace config error : ",t);
            }
        },  0,1, TimeUnit.HOURS);
    }

    public HeraTraceEtlConfig getConfig(String serviceName) {
        return heraTraceConfig.get(serviceName);
    }

    public void insert(HeraTraceEtlConfig config) {
        heraTraceConfig.putIfAbsent(getServiceName(config), config);
    }

    public void update(HeraTraceEtlConfig config) {
        heraTraceConfig.put(getServiceName(config), config);
    }

    public void delete(HeraTraceEtlConfig config) {
        heraTraceConfig.remove(getServiceName(config));
    }

    private String getServiceName(HeraTraceEtlConfig config) {
        StringBuffer sb = new StringBuffer();
        sb.append(config.getBindId()).append("-").append(config.getAppName());
        return sb.toString();
    }
}
