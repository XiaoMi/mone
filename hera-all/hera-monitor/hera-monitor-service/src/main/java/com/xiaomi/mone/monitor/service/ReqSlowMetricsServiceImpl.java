package com.xiaomi.mone.monitor.service;

import com.xiaomi.mone.monitor.bo.ReqSlowMetrics;
import com.xiaomi.mone.monitor.pojo.ReqSlowMetricsPOJO;
import com.xiaomi.mone.monitor.service.api.ReqSlowMetricsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/20 3:11 PM
 */
@Service
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class ReqSlowMetricsServiceImpl implements ReqSlowMetricsService {
    @Override
    public ReqSlowMetricsPOJO getSlowMetricsByMetric(String metrics) {
        {
            if (StringUtils.isBlank(metrics)) {
                return null;
            }
            for (ReqSlowMetrics slowMetrics : ReqSlowMetrics.values()) {
                if (slowMetrics.getMetrics() != null && slowMetrics.getMetrics().getCode().equals(metrics)) {
                    return covert(slowMetrics);
                }
            }
            return null;
        }
    }

    private ReqSlowMetricsPOJO covert(ReqSlowMetrics slowMetrics){
        ReqSlowMetricsPOJO pojo = new ReqSlowMetricsPOJO();
        pojo.setCode(slowMetrics.getCode());
        pojo.setMessage(slowMetrics.getMessage());
        return pojo;
    }
}
