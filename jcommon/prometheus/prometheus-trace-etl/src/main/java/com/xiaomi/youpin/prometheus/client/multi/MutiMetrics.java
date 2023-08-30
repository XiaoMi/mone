package com.xiaomi.youpin.prometheus.client.multi;

import com.xiaomi.youpin.prometheus.client.XmCounter;
import com.xiaomi.youpin.prometheus.client.XmGauge;
import com.xiaomi.youpin.prometheus.client.XmHistogram;
import io.prometheus.client.CollectorRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * @author zhangxiaowei
 */
@Slf4j
public class MutiMetrics {

    public MutiPrometheus gMetricsMgr;
    /**
     * group+service形成namespace标识
     */
    public static final String GROUP = "group";
    public static final String SERVICE = "service";
    public static final String APPLICATION = "application";

    private CollectorRegistry registry;

    public void init(String group,String service) {
        setGroup(group);
        setService(service);
    }


    public void setGroup(String group) {
        this.gMetricsMgr.getConstLabels().put(GROUP, group);
    }

    public void setService(String service) {
        this.gMetricsMgr.getConstLabels().put(SERVICE, service);
    }

    public static double[] DEFAULT_LATENCY_BUCKETS =
            new double[]{.01, .05, 1, 5, 7.5, 10, 25, 50, 100, 200, 500, 1000,1500,2000,3000,4000,5000};

    public MutiMetrics() {
        this.registry = new CollectorRegistry(true);
        gMetricsMgr = new MutiPrometheus(registry);
        gMetricsMgr.setConstLabels(new HashMap<>());
    }

    public CollectorRegistry getRegistry(){
        return this.registry;
    }

    public XmCounter newCounter(String metricName, String... labelNames) {
        try {
            return gMetricsMgr.newCounter(metricName, labelNames);
        } catch (Throwable throwable) {
            log.warn(throwable.getMessage());
            return null;
        }
    }

    public XmGauge newGauge(String metricName, String... labelNames) {
        return gMetricsMgr.newGauge(metricName, labelNames);
    }

    public XmHistogram newHistogram(String metricName, double[] buckets, String... labelNames) {
        if (buckets != null && buckets.length > 0) {
            return gMetricsMgr.newHistogram(metricName, buckets, labelNames);
        }
        return gMetricsMgr.newHistogram(metricName, DEFAULT_LATENCY_BUCKETS, labelNames);
    }

}
