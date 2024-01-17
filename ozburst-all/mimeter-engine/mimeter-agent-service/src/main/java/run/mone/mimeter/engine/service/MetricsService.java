package run.mone.mimeter.engine.service;

import com.google.common.collect.Sets;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.prometheus.client.Metrics;
import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static common.Const.*;
import static run.mone.mimeter.engine.agent.bo.stat.MetricLabelEnum.getLabelApiRpsNames;
import static run.mone.mimeter.engine.agent.bo.stat.MetricLabelEnum.getLabelNames;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/7/25
 */
@Service
public class MetricsService {

    private static final String logPrefix = "[MetricsService]";

    private static final Logger log = LoggerFactory.getLogger(MetricsService.class);

    private CollectorRegistry defaultRegistry;

    public void init() {
        this.defaultRegistry = CollectorRegistry.defaultRegistry;
    }

    public String metrics() {
        return this.metrics(this.defaultRegistry);
    }

    private String metrics(CollectorRegistry registry) {
        List<String> list = new ArrayList<>();
        Enumeration<Collector.MetricFamilySamples> samples = registry.filteredMetricFamilySamples(Sets.newHashSet(list));
        StringWriter writer = new StringWriter();
        String str = "";

        try {
            TextFormat.write004(writer, samples);
            StringBuffer sb = writer.getBuffer();
            str = sb.toString();
        } catch (IOException e) {
            log.error(logPrefix + "metrics writer buffer error", e);
        }
        return str;
    }

    private static void validate(String metricName, long value, String[] labelNames) {
        checkArgument(value >= 0, logPrefix + "record invalid value");
        checkArgument(StringUtils.isNotBlank(metricName), logPrefix + "record empty metricName");
        checkArgument(labelNames != null && labelNames.length > 0, logPrefix + "record empty labelNames");
    }

    public static void recordCounter(String metricName, long value, String... labelValues) {
        recordCounter(metricName, getLabelNames(), value, labelValues);
    }

    public static void recordCounter(String metricName, String[] labelNames, long value, String... labelValues) {
        try {
            validate(metricName, value, labelNames);
            Metrics.getInstance().newCounter(metricName, labelNames).with(labelValues).add(value, labelValues);
        } catch (Exception e) {
            log.error(logPrefix + "recordCounter exception; " + e.getMessage());
        }
    }

    public static void recordRt(String metricName, long value, String... labelValues) {
        recordRt(metricName, getLabelNames(), value, labelValues);
    }

    public static void recordRt(String metricName, String[] labelNames, long value, String... labelValues) {
        double[] buckets = new double[]{10.0, 100.0, 200.0, 300.0, 400.0, 600.0, 800.0, 1000.0, 1400.0, 1800.0, 2200.0, 2600.0, 3000.0, 6000.0};
        recordHistogram(metricName, labelNames, buckets, value, labelValues);
    }

    private static void checkLabelValues(String... labelValues) {
        boolean hasNull = false;

        for (int i = 0; i < labelValues.length; i++) {
            if (labelValues[i] == null) {
                hasNull = true;
                labelValues[i] = "";
            }
        }
        if (hasNull) {
            log.error(logPrefix + "checkLabelValues null value; " + Arrays.toString(labelValues));
        }
    }

    private static void recordHistogram(String metricName, String[] labelNames, double[] buckets, long value, String... labelValues) {
        String logSuffix = "";

        try {
            validate(metricName, value, labelNames);
            checkArgument(buckets != null && buckets.length > 0, logPrefix + "recordHistogram empty buckets");
            checkLabelValues(labelValues);

            logSuffix = ", label names " + Arrays.toString(labelNames) + ", label values " + Arrays.toString(labelValues) + "; ";
            checkArgument(labelValues != null && labelValues.length == labelNames.length,
                    logPrefix + "recordHistogram invalid label" + logSuffix);

            Metrics.getInstance().newHistogram(metricName, buckets, labelNames).with(labelValues).observe(value, labelValues);
        } catch (Exception e) {
            log.error(logPrefix + "recordHistogram exception" + logSuffix + e.getMessage());
        }
    }

    public static void recordTpsAndRtMetrics(long elapsed, String[] labelVals,boolean recordTps) {
        recordRt(METRICS_NAME_RT_API, elapsed, labelVals);
        if (recordTps){
            recordCounter(METRICS_NAME_TPS_API, 1, labelVals);
        }
    }

    public static void recordApiRpsMetrics(String[] labelVals) {
        recordCounter(METRICS_NAME_RPS_API, getLabelApiRpsNames(),1, labelVals);
    }
}
