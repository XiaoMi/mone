package com.xiaomi.youpin.prometheus.client.binder;

import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.lang.NonNullApi;
import io.micrometer.core.lang.NonNullFields;
import io.micrometer.core.lang.Nullable;

import javax.management.ListenerNotFoundException;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description
 * @Author dingtao
 * @Date 2021/10/27 4:04 下午
 */
@NonNullApi
@NonNullFields
public class JvmGcMetricsReduced implements MeterBinder, AutoCloseable{
    private final boolean managementExtensionsPresent;
    private final Iterable<Tag> tags;
    @Nullable
    private String youngGenPoolName;
    @Nullable
    private String oldGenPoolName;
    private final List<Runnable> notificationListenerCleanUpRunnables;

    public JvmGcMetricsReduced() {
        this(Collections.emptyList());
    }

    public JvmGcMetricsReduced(Iterable<Tag> tags) {
        this.managementExtensionsPresent = isManagementExtensionsPresent();
        this.notificationListenerCleanUpRunnables = new CopyOnWriteArrayList();
        Iterator var2 = ManagementFactory.getMemoryPoolMXBeans().iterator();

        while(var2.hasNext()) {
            MemoryPoolMXBean mbean = (MemoryPoolMXBean)var2.next();
            String name = mbean.getName();
            if (this.isYoungGenPool(name)) {
                this.youngGenPoolName = name;
            } else if (this.isOldGenPool(name)) {
                this.oldGenPoolName = name;
            }
        }

        this.tags = tags;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        Counter promotedBytes = Counter.builder("jvm.gc.memory.promoted").tags(this.tags).baseUnit("bytes").description("Count of positive increases in the size of the old generation memory pool before GC to after GC").register(registry);
        Counter allocatedBytes = Counter.builder("jvm.gc.memory.allocated").tags(this.tags).baseUnit("bytes").description("Incremented for an increase in the size of the young generation memory pool after one GC to before the next").register(registry);
        if (this.managementExtensionsPresent) {
            AtomicLong youngGenSizeAfter = new AtomicLong(0L);
            Iterator var7 = ManagementFactory.getGarbageCollectorMXBeans().iterator();

            while(var7.hasNext()) {
                GarbageCollectorMXBean mbean = (GarbageCollectorMXBean)var7.next();
                if (mbean instanceof NotificationEmitter) {
                    NotificationListener notificationListener = (notification, ref) -> {
                        if (notification.getType().equals("com.sun.management.gc.notification")) {
                            CompositeData cd = (CompositeData)notification.getUserData();
                            GarbageCollectionNotificationInfo notificationInfo = GarbageCollectionNotificationInfo.from(cd);
                            String gcCause = notificationInfo.getGcCause();
                            String gcAction = notificationInfo.getGcAction();
                            GcInfo gcInfo = notificationInfo.getGcInfo();
                            long duration = gcInfo.getDuration();
                            if (this.isConcurrentPhase(gcCause)) {
                                Timer.builder("jvm.gc.concurrent.phase.time").tags(this.tags).tags(new String[]{"action", gcAction, "cause", gcCause}).description("Time spent in concurrent phase").register(registry).record(duration, TimeUnit.MILLISECONDS);
                            } else {
                                Timer.builder("jvm.gc.pause").tags(this.tags).tags(new String[]{"action", gcAction, "cause", gcCause}).description("Time spent in GC pause").register(registry).record(duration, TimeUnit.MILLISECONDS);
                            }

                            Map<String, MemoryUsage> before = gcInfo.getMemoryUsageBeforeGc();
                            Map<String, MemoryUsage> after = gcInfo.getMemoryUsageAfterGc();
                            long oldBefore;
                            long oldAfter;
                            long delta;
                            if (this.oldGenPoolName != null) {
                                oldBefore = ((MemoryUsage)before.get(this.oldGenPoolName)).getUsed();
                                oldAfter = ((MemoryUsage)after.get(this.oldGenPoolName)).getUsed();
                                delta = oldAfter - oldBefore;
                                if (delta > 0L) {
                                    promotedBytes.increment((double)delta);
                                }

                            }

                            if (this.youngGenPoolName != null) {
                                oldBefore = ((MemoryUsage)before.get(this.youngGenPoolName)).getUsed();
                                oldAfter = ((MemoryUsage)after.get(this.youngGenPoolName)).getUsed();
                                delta = oldBefore - youngGenSizeAfter.get();
                                youngGenSizeAfter.set(oldAfter);
                                if (delta > 0L) {
                                    allocatedBytes.increment((double)delta);
                                }
                            }

                        }
                    };
                    NotificationEmitter notificationEmitter = (NotificationEmitter)mbean;
                    notificationEmitter.addNotificationListener(notificationListener, (NotificationFilter)null, (Object)null);
                    this.notificationListenerCleanUpRunnables.add(() -> {
                        try {
                            notificationEmitter.removeNotificationListener(notificationListener);
                        } catch (ListenerNotFoundException var3) {
                        }

                    });
                }
            }

        }
    }

    private static boolean isManagementExtensionsPresent() {
        try {
            Class.forName("com.sun.management.GarbageCollectionNotificationInfo", false, JvmGcMetricsReduced.class.getClassLoader());
            return true;
        } catch (Throwable var1) {
            return false;
        }
    }

    private boolean isConcurrentPhase(String cause) {
        return "No GC".equals(cause);
    }

    private boolean isOldGenPool(String name) {
        return name.endsWith("Old Gen") || name.endsWith("Tenured Gen");
    }

    private boolean isYoungGenPool(String name) {
        return name.endsWith("Eden Space");
    }

    @Override
    public void close() {
        this.notificationListenerCleanUpRunnables.forEach(Runnable::run);
    }

    @NonNullApi
    static enum GcGenerationAge {
        OLD,
        YOUNG,
        UNKNOWN;

        private static Map<String, GcGenerationAge> knownCollectors = new HashMap<String, GcGenerationAge>() {
            {
                this.put("ConcurrentMarkSweep", GcGenerationAge.OLD);
                this.put("Copy", GcGenerationAge.YOUNG);
                this.put("G1 Old Generation", GcGenerationAge.OLD);
                this.put("G1 Young Generation", GcGenerationAge.YOUNG);
                this.put("MarkSweepCompact", GcGenerationAge.OLD);
                this.put("PS MarkSweep", GcGenerationAge.OLD);
                this.put("PS Scavenge", GcGenerationAge.YOUNG);
                this.put("ParNew", GcGenerationAge.YOUNG);
            }
        };

        private GcGenerationAge() {
        }

        static GcGenerationAge fromName(String name) {
            return (GcGenerationAge)knownCollectors.getOrDefault(name, UNKNOWN);
        }
    }
}
