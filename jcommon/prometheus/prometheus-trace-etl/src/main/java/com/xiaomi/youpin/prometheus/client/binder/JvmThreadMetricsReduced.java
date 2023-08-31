package com.xiaomi.youpin.prometheus.client.binder;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.lang.NonNullApi;
import io.micrometer.core.lang.NonNullFields;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Collections;

/**
 * @Description
 * @Author dingtao
 * @Date 2021/10/27 10:49 上午
 */
@NonNullApi
@NonNullFields
public class JvmThreadMetricsReduced implements MeterBinder {

    private final Iterable<Tag> tags;

    public JvmThreadMetricsReduced() {
        this.tags = Collections.emptyList();
    }
    @Override
    public void bindTo(MeterRegistry registry) {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        Gauge.builder("jvm.threads.peak", threadBean, ThreadMXBean::getPeakThreadCount).tags(this.tags).description("The peak live thread count since the Java virtual machine started or peak was reset").baseUnit("threads").register(registry);
        Gauge.builder("jvm.threads.daemon", threadBean, ThreadMXBean::getDaemonThreadCount).tags(this.tags).description("The current number of live daemon threads").baseUnit("threads").register(registry);
        Gauge.builder("jvm.threads.live", threadBean, ThreadMXBean::getThreadCount).tags(this.tags).description("The current number of live threads including both daemon and non-daemon threads").baseUnit("threads").register(registry);
    }
}
