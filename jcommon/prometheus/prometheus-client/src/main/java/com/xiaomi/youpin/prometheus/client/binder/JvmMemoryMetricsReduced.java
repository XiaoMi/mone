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
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.lang.NonNullApi;
import io.micrometer.core.lang.NonNullFields;
import io.micrometer.core.lang.Nullable;

import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.ToLongFunction;

/**
 * @Description
 * @Author dingtao
 * @Date 2021/10/27 4:15 下午
 */
@NonNullApi
@NonNullFields
public class JvmMemoryMetricsReduced implements MeterBinder {
    private final Iterable<Tag> tags;

    public JvmMemoryMetricsReduced() {
        this(Collections.emptyList());
    }

    public JvmMemoryMetricsReduced(Iterable<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        Iterator var2 = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class).iterator();

        while(var2.hasNext()) {
            BufferPoolMXBean bufferPoolBean = (BufferPoolMXBean)var2.next();
            Iterable<Tag> tagsWithId = Tags.concat(this.tags, new String[]{"id", bufferPoolBean.getName()});
            Gauge.builder("jvm.buffer.memory.used", bufferPoolBean, BufferPoolMXBean::getMemoryUsed).tags(tagsWithId).description("An estimate of the memory that the Java virtual machine is using for this buffer pool").baseUnit("bytes").register(registry);
            Gauge.builder("jvm.buffer.total.capacity", bufferPoolBean, BufferPoolMXBean::getTotalCapacity).tags(tagsWithId).description("An estimate of the total capacity of the buffers in this pool").baseUnit("bytes").register(registry);
        }

        var2 = ManagementFactory.getPlatformMXBeans(MemoryPoolMXBean.class).iterator();

        while(var2.hasNext()) {
            MemoryPoolMXBean memoryPoolBean = (MemoryPoolMXBean)var2.next();
            String area = MemoryType.HEAP.equals(memoryPoolBean.getType()) ? "heap" : "nonheap";
            Iterable<Tag> tagsWithId = Tags.concat(this.tags, new String[]{"id", memoryPoolBean.getName(), "area", area});
            Gauge.builder("jvm.memory.used", memoryPoolBean, (mem) -> {
                return this.getUsageValue(mem, MemoryUsage::getUsed);
            }).tags(tagsWithId).description("The amount of used memory").baseUnit("bytes").register(registry);
            Gauge.builder("jvm.memory.committed", memoryPoolBean, (mem) -> {
                return this.getUsageValue(mem, MemoryUsage::getCommitted);
            }).tags(tagsWithId).description("The amount of memory in bytes that is committed for the Java virtual machine to use").baseUnit("bytes").register(registry);
            Gauge.builder("jvm.memory.max", memoryPoolBean, (mem) -> {
                return this.getUsageValue(mem, MemoryUsage::getMax);
            }).tags(tagsWithId).description("The maximum amount of memory in bytes that can be used for memory management").baseUnit("bytes").register(registry);
        }

    }

    private double getUsageValue(MemoryPoolMXBean memoryPoolMXBean, ToLongFunction<MemoryUsage> getter) {
        MemoryUsage usage = this.getUsage(memoryPoolMXBean);
        return usage == null ? 0.0D / 0.0 : (double)getter.applyAsLong(usage);
    }

    @Nullable
    private MemoryUsage getUsage(MemoryPoolMXBean memoryPoolMXBean) {
        try {
            return memoryPoolMXBean.getUsage();
        } catch (InternalError var3) {
            return null;
        }
    }
}
