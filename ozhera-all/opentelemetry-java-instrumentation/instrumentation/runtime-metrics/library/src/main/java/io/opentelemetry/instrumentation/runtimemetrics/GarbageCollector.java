/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.runtimemetrics;

import io.opentelemetry.api.metrics.GlobalMeterProvider;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.metrics.common.Labels;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Registers observers that generate metrics about JVM garbage collectors.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * GarbageCollector.registerObservers();
 * }</pre>
 *
 * <p>Example metrics being exported:
 *
 * <pre>
 *   runtime.jvm.gc.time{gc="PS1"} 6.7
 *   runtime.jvm.gc.count{gc="PS1"} 1
 * </pre>
 */
public final class GarbageCollector {
  private static final String GC_LABEL_KEY = "gc";

  /** Register all observers provided by this module. */
  public static void registerObservers() {
    List<GarbageCollectorMXBean> garbageCollectors = ManagementFactory.getGarbageCollectorMXBeans();
    Meter meter = GlobalMeterProvider.getMeter(GarbageCollector.class.getName());
    List<Labels> labelSets = new ArrayList<>(garbageCollectors.size());
    for (GarbageCollectorMXBean gc : garbageCollectors) {
      labelSets.add(Labels.of(GC_LABEL_KEY, gc.getName()));
    }
    meter
        .longSumObserverBuilder("runtime.jvm.gc.time")
        .setDescription("Time spent in a given JVM garbage collector in milliseconds.")
        .setUnit("ms")
        .setUpdater(
            resultLongObserver -> {
              for (int i = 0; i < garbageCollectors.size(); i++) {
                resultLongObserver.observe(
                    garbageCollectors.get(i).getCollectionTime(), labelSets.get(i));
              }
            })
        .build();
    meter
        .longSumObserverBuilder("runtime.jvm.gc.count")
        .setDescription(
            "The number of collections that have occurred for a given JVM garbage collector.")
        .setUnit("collections")
        .setUpdater(
            resultLongObserver -> {
              for (int i = 0; i < garbageCollectors.size(); i++) {
                resultLongObserver.observe(
                    garbageCollectors.get(i).getCollectionCount(), labelSets.get(i));
              }
            })
        .build();
  }

  private GarbageCollector() {}
}
