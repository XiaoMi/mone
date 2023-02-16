package com.xiaomi.hera.trace.etl.es.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.hera.trace.etl.common.HashUtil;
import com.xiaomi.hera.trace.etl.service.HeraContextService;
import com.xiaomi.hera.trace.etl.domain.HeraTraceEtlConfig;
import com.xiaomi.hera.trace.etl.es.config.TraceConfig;
import com.xiaomi.hera.trace.etl.es.domain.FilterResult;
import com.xiaomi.hera.trace.etl.es.util.bloomfilter.TraceIdRedisBloomUtil;
import com.xiaomi.hera.trace.etl.util.MessageUtil;
import com.xiaomi.hera.tspandata.TSpanData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 过滤逻辑
 */
@Service
@Slf4j
public class FilterService {

    @NacosValue(value = "${trace.es.filter.spanname}", autoRefreshed = true)
    private String filterSpanName;
    @NacosValue(value = "${trace.threshold}", autoRefreshed = true)
    private int defThreshold;
    @NacosValue(value = "${trace.duration.threshold}", autoRefreshed = true)
    private int defDurationThreshold;
    @NacosValue(value = "${trace.es.filter.isopen}", autoRefreshed = true)
    private boolean filterIsOpen;
    @NacosValue(value = "${trace.es.filter.random.base}", autoRefreshed = true)
    private int randomBase;

    @Autowired
    private TraceIdRedisBloomUtil traceIdRedisBloomUtil;
    @Autowired
    private TraceConfig traceConfig;
    @Autowired
    private HeraContextService heraContextService;

    public FilterResult filterBefore(String statusCode, String traceId, String spanName, String heraContext, String serviceName, long duration, TSpanData tSpanData) {
        FilterResult filterResult = new FilterResult();
        // spanName 黑名单
        if (filterSpanName.contains(spanName)) {
            filterResult.setDiscard(true);
            return filterResult;
        }
        if (filterIsOpen) {
            // 判断是否错误
            if (MessageUtil.ERROR_CODE.equals(statusCode)) {
                filterResult.setResult(true);
                filterResult.setAddBloom(true);
                return filterResult;
            }
            HeraTraceEtlConfig config = traceConfig.getConfig(serviceName);
            // 判断heraContext中是否有需要保留的key
            if (checkHeraContext(heraContext, config)) {
                filterResult.setResult(true);
                filterResult.setAddBloom(true);
                return filterResult;
            }
            // 判断耗时是否超过阈值
            int durationThreshold = config == null ? defDurationThreshold : config.getTraceDurationThreshold();
            if (duration / (1000 * 1000) > durationThreshold) {
                filterResult.setResult(true);
                filterResult.setAddBloom(true);
                return filterResult;
            }
            // 进行TraceID随机采样
            boolean filterRandom = filterRandom(serviceName, traceId);
            if(filterRandom){
                filterResult.setResult(true);
                filterResult.setAddBloom(false);
                return filterResult;
            }
            // 判断boolean过滤器
            if (traceIdRedisBloomUtil.isExistLocal(traceId)) {
                // bloom filter里面存在，则不需要再保存
                filterResult.setResult(true);
                filterResult.setAddBloom(false);
                return filterResult;
            } else {
                filterResult.setResult(false);
                return filterResult;
            }
        } else {
            filterResult.setResult(true);
            filterResult.setAddBloom(false);
            return filterResult;
        }
    }

    public boolean filterRandom(String serviceName, String traceId) {
        HeraTraceEtlConfig config = traceConfig.getConfig(serviceName);
        int i = HashUtil.consistentHash(traceId, randomBase);
        // 根据阈值，进行随机取样
        int threshold = config == null ? defThreshold : config.getTraceFilter();
        if (i < threshold) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkHeraContext(String heraContext, HeraTraceEtlConfig config) {
        if (config != null && StringUtils.isNotEmpty(heraContext) && StringUtils.isNotEmpty(config.getTraceDebugFlag())) {
            String[] flags = config.getTraceDebugFlag().split("\\|");
            Set<String> heraContextKeys = heraContextService.getHeraContextKeys(heraContext);
            for(String flag : flags){
                for(String heraContextKey : heraContextKeys){
                    if(flag.equals(heraContextKey)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
