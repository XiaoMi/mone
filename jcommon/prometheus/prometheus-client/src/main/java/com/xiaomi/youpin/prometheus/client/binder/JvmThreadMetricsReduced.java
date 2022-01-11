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
