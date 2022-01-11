/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.prometheus.client;

import com.xiaomi.youpin.prometheus.client.binder.ClassLoaderMetricsReduced;
import com.xiaomi.youpin.prometheus.client.binder.JvmGcMetricsReduced;
import com.xiaomi.youpin.prometheus.client.binder.JvmMemoryMetricsReduced;
import com.xiaomi.youpin.prometheus.client.binder.JvmThreadMetricsReduced;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * @author zhangxiaowei
 */
@Slf4j
public class Metrics {

    public MetricsManager gMetricsMgr;
    /**
     * group+service形成namespace标识
     */
    public static final String GROUP = "group";
    public static final String SERVICE = "service";
    public static final String LABEL_TRACE_ID = "traceId";
    public static final String APPLICATION = "application";

    private static class LazyHolder {
        private static final Metrics ins = new Metrics();
    }


    public void init(String group,String service) {
        this.init(group,service,false);
    }

    public void init(String group,String service, boolean jvmReduced){
        setGroup(group);
        setService(service);
        Prometheus.REGISTRY.config().commonTags(APPLICATION, Prometheus.constLabels.get(Metrics.SERVICE));
        if(jvmReduced){
            new ClassLoaderMetricsReduced().bindTo(Prometheus.REGISTRY);
            new JvmMemoryMetricsReduced().bindTo(Prometheus.REGISTRY);
            new JvmGcMetricsReduced().bindTo(Prometheus.REGISTRY);
            new ProcessorMetrics().bindTo(Prometheus.REGISTRY);
            new JvmThreadMetricsReduced().bindTo(Prometheus.REGISTRY);
            new UptimeMetrics().bindTo(Prometheus.REGISTRY);
            new FileDescriptorMetrics().bindTo(Prometheus.REGISTRY);
        }else{
            new ClassLoaderMetrics().bindTo(Prometheus.REGISTRY);
            new JvmMemoryMetrics().bindTo(Prometheus.REGISTRY);
            new JvmGcMetrics().bindTo(Prometheus.REGISTRY);
            new ProcessorMetrics().bindTo(Prometheus.REGISTRY);
            new JvmThreadMetrics().bindTo(Prometheus.REGISTRY);
            new UptimeMetrics().bindTo(Prometheus.REGISTRY);
            new FileDescriptorMetrics().bindTo(Prometheus.REGISTRY);
        }
    }


    //获取唯一可用的对象
    public static Metrics getInstance() {
        return LazyHolder.ins;
    }

    public void setGroup(String group) {
        Prometheus.constLabels.put(GROUP, group);
    }

    public void setService(String service) {
        Prometheus.constLabels.put(SERVICE, service);
    }

    public static double[] DEFAULT_LATENCY_BUCKETS =
            new double[]{.01, .05, 1, 5, 7.5, 10, 25, 50, 100, 200, 500, 1000,1500,2000,3000,4000,5000};

    private Metrics() {
        Prometheus.constLabels = new HashMap<>();
        gMetricsMgr = new Prometheus();
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
