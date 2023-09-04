package com.xiaomi.hera.trace.etl.consumer;

import com.xiaomi.youpin.prometheus.client.XmCounter;
import com.xiaomi.youpin.prometheus.client.XmHistogram;
import com.xiaomi.youpin.prometheus.client.multi.MutiMetrics;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author goodjava@qq.com
 * @date 2023/8/29 16:22
 */
@Service
public class MutiMetricsCall {


    private MutiMetrics[] mutiMetricsArray = new MutiMetrics[2];

    @Getter
    private AtomicInteger index = new AtomicInteger(0);

    public void init(String group, String service) {
        Arrays.stream(mutiMetricsArray).forEach(it -> {
            it.init(group, service);
        });
    }

    public void change() {
        index.updateAndGet((i) -> {
            if (i == 0) {
                return 1;
            }
            return 0;
        });
    }

    public MutiMetrics old() {
        return mutiMetricsArray[this.index.get() == 0 ? 1 : 0];
    }


    public MutiMetricsCall() {
        mutiMetricsArray[0] = new MutiMetrics();
        mutiMetricsArray[1] = new MutiMetrics();
    }


    public XmCounter newCounter(String metricName, String... labelNames) {
        return mutiMetricsArray[index.get()].newCounter(metricName, labelNames);
    }

    public XmHistogram newHistogram(String metricName, double[] buckets, String... labelNames) {
        return mutiMetricsArray[index.get()].newHistogram(metricName, buckets, labelNames);
    }


}
