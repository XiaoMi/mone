package com.xiaomi.mone.monitor.service;

import com.xiaomi.mone.monitor.bo.MetricLabelKind;
import com.xiaomi.mone.monitor.service.api.MetricsLabelKindService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/20 2:34 PM
 */
@Service
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class MetricsLabelKindServiceImpl implements MetricsLabelKindService {
    @Override
    public boolean dubboType(String alert) {
        for (MetricLabelKind metricLabelKind : MetricLabelKind.values()) {
            if (metricLabelKind.getKind() != 3) {
                continue;
            }
            if (metricLabelKind.getMetric().getCode().equals(alert)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean httpType(String alert) {
        for (MetricLabelKind metricLabelKind : MetricLabelKind.values()) {
            if (metricLabelKind.getKind() != 1 && metricLabelKind.getKind() != 2) {
                continue;
            }
            if (metricLabelKind.getMetric().getCode().equals(alert)) {
                return true;
            }
        }
        return false;
    }
}
