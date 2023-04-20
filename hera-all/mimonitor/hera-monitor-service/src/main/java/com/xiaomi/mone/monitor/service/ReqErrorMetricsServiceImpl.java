package com.xiaomi.mone.monitor.service;

import com.xiaomi.mone.monitor.bo.AlarmPresetMetrics;
import com.xiaomi.mone.monitor.bo.ReqErrorMetrics;
import com.xiaomi.mone.monitor.pojo.ReqErrorMetricsPOJO;
import com.xiaomi.mone.monitor.service.api.ReqErrorMetricsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/20 2:44 PM
 */
@Service
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class ReqErrorMetricsServiceImpl implements ReqErrorMetricsService {

    @Override
    public ReqErrorMetricsPOJO getErrorMetricsByMetrics(String metrics) {
        if (StringUtils.isBlank(metrics)) {
            return null;
        }
        for (ReqErrorMetrics errMetrics : ReqErrorMetrics.values()) {
            if (errMetrics.getMetrics() == null || errMetrics.getMetrics().length == 0) {
                continue;
            }
            for (AlarmPresetMetrics ele : errMetrics.getMetrics()) {
                if (ele.getCode().equals(metrics)) {
                    return covert(errMetrics);
                }
            }
        }
        return null;
    }

    private ReqErrorMetricsPOJO covert(ReqErrorMetrics metrics){
        ReqErrorMetricsPOJO pojo = new ReqErrorMetricsPOJO();
        pojo.setCode(metrics.getCode());
        pojo.setMessage(metrics.getMessage());
        return pojo;
    }
}
