package com.xiaomi.hera.trace.etl.consumer;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import com.xiaomi.youpin.prometheus.client.MetricsManager;
import com.xiaomi.youpin.prometheus.client.Prometheus;
import io.prometheus.client.*;
import io.prometheus.client.exporter.common.TextFormat;
import lombok.extern.slf4j.Slf4j;
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

    private CopyOnWriteArrayList<byte[]> cacheData = new CopyOnWriteArrayList<>();

    @Resource
    private MutiMetricsCall call;

    public int dataSize() {
        return cacheData.size();
    }

    @PostConstruct
    public void init() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                if (cacheData.size() > 4) {
                    log.info("clear cache data:{}", cacheData.size());
                    cacheData.clear();
                }
            } catch (Throwable ex) {
                log.error(ex.getMessage());
            }
        }, 0, 60, TimeUnit.SECONDS);
    }

    public byte[] getData() {
        log.info("get data");
        Stopwatch sw = Stopwatch.createStarted();
        try {
            if (cacheData.size() >= 1) {
                return cacheData.remove(0);
            }
        } finally {
            log.info("get data use time:{}ms", sw.elapsed(TimeUnit.MILLISECONDS));
        }
        return new byte[]{};
    }


    public void cacheData() {
        call.change();
        Executors.newSingleThreadExecutor().submit(() -> {
            log.info("cache data");
            Stopwatch sw = Stopwatch.createStarted();
            List<String> list = new ArrayList<>();
            CollectorRegistry registry = call.old().getRegistry();
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
                this.cacheData.add(bytes);
            } catch (Throwable ex) {
                log.error(ex.getMessage());
            } finally {
                clearMetrics();
            }
            log.info("cache data use time:{} ms", sw.elapsed(TimeUnit.MILLISECONDS));
        });
    }


    private void clearMetrics() {
        try {
            MetricsManager gMetricsMgr = call.old().gMetricsMgr;
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
        CollectorRegistry registry = call.old().getRegistry();
        for (String key : prometheusMetrics.keySet()) {
            Object o = prometheusMetrics.get(key);
            if (o instanceof Counter) {
                Counter counter = (Counter) o;
                registry.unregister(counter);
            } else if (o instanceof Gauge) {
                Gauge gauge = (Gauge) o;
                gauge.clear();
                registry.unregister(gauge);
            } else if (o instanceof Histogram) {
                Histogram histogram = (Histogram) o;
                histogram.clear();
                registry.unregister(histogram);
            } else {
                log.error("metrics : " + key + " Type conversion failed, original type : " + o.getClass().getName());
            }
        }
    }


}
