package com.xiaomi.mone.monitor.service;

import com.xiaomi.mone.monitor.bo.AlarmPresetMetrics;
import com.xiaomi.mone.monitor.bo.BasicUrlType;
import com.xiaomi.mone.monitor.bo.MetricLabelKind;
import com.xiaomi.mone.monitor.bo.MetricsRule;
import com.xiaomi.mone.monitor.pojo.AlarmPresetMetricsPOJO;
import com.xiaomi.mone.monitor.pojo.BasicUrlTypePOJO;
import com.xiaomi.mone.monitor.service.api.AlarmPresetMetricsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/20 12:16 PM
 */
@Service
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class AlarmPresetMetricsServiceImpl implements AlarmPresetMetricsService {
    @Override
    public List<MetricsRule> getEnumList() {
        Map<AlarmPresetMetrics, MetricLabelKind> map = MetricLabelKind.getMetricLabelKindMap();
        MetricLabelKind kind = null;
        List <MetricsRule> list = new ArrayList<>();
        AlarmPresetMetrics[] values = AlarmPresetMetrics.values();
        for(AlarmPresetMetrics value : values){
            if (value.getMetricType() == null || value.getUnit() == null || value.getStrategyType() == null) {
                continue;
            }
            MetricsRule rule = new MetricsRule(value.getCode(),value.getMessage(), value.getUnit().getCode(), value.getStrategyType().getCode(),value.getMetricType().getName(),value.getHideValueConfig());
            kind = map.get(value);
            if (kind != null) {
                rule.setKind(kind.getKind());
            }
            list.add(rule);
        }
        return list;
    }

    @Override
    public AlarmPresetMetricsPOJO getByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        for (AlarmPresetMetrics metrics : AlarmPresetMetrics.values()) {
            if (metrics.getCode().equals(code)) {
                return convert(metrics);
            }
        }
        return null;
    }

    private AlarmPresetMetricsPOJO convert(AlarmPresetMetrics metrics){
        if(metrics == null){
            return null;
        }
        AlarmPresetMetricsPOJO pojo = new AlarmPresetMetricsPOJO();
        pojo.setCode(metrics.getCode());
        pojo.setMessage(metrics.getMessage());
        pojo.setErrorMetric(metrics.getErrorMetric());
        pojo.setTotalMetric(metrics.getTotalMetric());
        pojo.setSlowQueryMetric(metrics.getSlowQueryMetric());
        pojo.setTimeCostMetric(metrics.getTimeCostMetric());
        pojo.setUnit(metrics.getUnit());
        pojo.setGroupKey(metrics.getGroupKey());
        pojo.setStrategyType(metrics.getStrategyType());
        pojo.setMetricType(metrics.getMetricType());
        pojo.setHideValueConfig(metrics.getHideValueConfig());
        pojo.setBasicUrlType(convert(metrics.getBasicUrlType()));
        pojo.setViewPanel(metrics.getViewPanel());
        pojo.setEnv(metrics.getEnv());
        pojo.setDomain(metrics.getDomain());
        return pojo;
    }

    private BasicUrlTypePOJO convert(BasicUrlType basicUrlType){
        if(basicUrlType == null){
            return null;
        }
        BasicUrlTypePOJO pojo = new BasicUrlTypePOJO();
        pojo.setName(basicUrlType.getName());
        pojo.setReqJsonObject(basicUrlType.getReqJsonObject());
        return pojo;
    }

    @Override
    public Map<String, String> getEnumMap() {
        Map<String,String> map = new LinkedHashMap<>();
        AlarmPresetMetrics[] values = AlarmPresetMetrics.values();
        for(AlarmPresetMetrics value : values){
            map.put(value.getCode(),value.getMessage());
        }
        return map;
    }
}
