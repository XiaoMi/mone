package com.xiaomi.youpin.prometheus.all.client.binder;

import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.lang.NonNullApi;
import io.micrometer.core.lang.NonNullFields;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.util.Collections;

/**
 * @Description
 * @Author dingtao
 * @Date 2021/10/27 3:59
 */
@NonNullApi
@NonNullFields
public class ClassLoaderMetricsReduced implements MeterBinder {
    private final Iterable<Tag> tags;

    public ClassLoaderMetricsReduced() {
        this(Collections.emptyList());
    }

    public ClassLoaderMetricsReduced(Iterable<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        ClassLoadingMXBean classLoadingBean = ManagementFactory.getClassLoadingMXBean();
        Gauge.builder("jvm.classes.loaded", classLoadingBean, ClassLoadingMXBean::getLoadedClassCount).tags(this.tags).description("The number of classes that are currently loaded in the Java virtual machine").baseUnit("classes").register(registry);
    }
}
