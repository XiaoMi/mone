package com.xiaomi.hera.trace.etl.consumer;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import com.xiaomi.hera.trace.etl.constant.LockUtil;
import com.xiaomi.youpin.prometheus.client.Metrics;
import com.xiaomi.youpin.prometheus.client.MetricsManager;
import com.xiaomi.youpin.prometheus.client.Prometheus;
import io.prometheus.client.*;
import io.prometheus.client.exporter.common.TextFormat;
import lombok.extern.slf4j.Slf4j;
import okio.Buffer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/8/29 10:02
 */
@Service
@Slf4j
public class DataCacheService {

    private CopyOnWriteArrayList<byte[]> data = new CopyOnWriteArrayList<>();

    @Resource
    private EnterManager enterManager;


    @PostConstruct
    public void init() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                enterManager.getMonitor().enter();
                cacheData();
            } catch (Throwable ex) {
                log.error(ex.getMessage(), ex);
            } finally {
                enterManager.getMonitor().leave();
            }
        }, 0, 15, TimeUnit.SECONDS);
    }


    public byte[] getData() {
        log.info("get data");
        Stopwatch sw = Stopwatch.createStarted();
        Buffer buffer = new Buffer();
        try {
            data.forEach(it -> buffer.write(it));
            data.clear();
            return buffer.readByteArray();
        } finally {
            log.info("get data use time:{}ms", sw.elapsed(TimeUnit.MILLISECONDS));
            buffer.clear();
        }
    }


    public void cacheData() {
        log.info("cache data");
        Stopwatch sw = Stopwatch.createStarted();
        synchronized (LockUtil.lock) {
            List<String> list = new ArrayList<>();
            CollectorRegistry registry = CollectorRegistry.defaultRegistry;
            try {
                Field field = registry.getClass().getDeclaredField("namesToCollectors");
                field.setAccessible(true);
                Map<String, Collector> namesToCollectors = (Map<String, Collector>) field.get(registry);
                list = namesToCollectors.keySet().stream()
                        .filter(it -> !it.endsWith("created"))
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.info("export metrics error : ", e);
            }
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); OutputStreamWriter writer = new OutputStreamWriter(baos)) {
                TextFormat.writeFormat(TextFormat.CONTENT_TYPE_004, writer, registry.filteredMetricFamilySamples(Sets.newHashSet(list)));
                writer.flush();
                byte[] bytes = baos.toByteArray();
                this.data.add(bytes);
            } catch (Throwable ex) {
                log.error(ex.getMessage());
            } finally {
                clearMetrics();
            }
        }
        log.info("cache data use time:{}ms", sw.elapsed(TimeUnit.MILLISECONDS));
    }


    private void clearMetrics() {
        try {
            MetricsManager gMetricsMgr = Metrics.getInstance().gMetricsMgr;
            if (gMetricsMgr instanceof Prometheus) {
                Prometheus prometheus = (Prometheus) gMetricsMgr;
                Map<String, Object> prometheusMetrics = prometheus.prometheusMetrics;
                clearTypeMetrics(prometheusMetrics);
                prometheus.prometheusMetrics.clear();
                prometheus.prometheusTypeMetrics.clear();
            }
        } catch (Exception e) {
            log.error("clear metrics error", e);
        }
    }

    private void clearTypeMetrics(Map<String, Object> prometheusMetrics) {
        for (String key : prometheusMetrics.keySet()) {
            Object o = prometheusMetrics.get(key);
            if (o instanceof Counter) {
                Counter counter = (Counter) o;
                CollectorRegistry.defaultRegistry.unregister(counter);
            } else if (o instanceof Gauge) {
                Gauge gauge = (Gauge) o;
                gauge.clear();
                CollectorRegistry.defaultRegistry.unregister(gauge);
            } else if (o instanceof Histogram) {
                Histogram histogram = (Histogram) o;
                histogram.clear();
                CollectorRegistry.defaultRegistry.unregister(histogram);
            } else {
                log.error("metrics : " + key + " Type conversion failed, original type : " + o.getClass().getName());
            }
        }
    }


}
