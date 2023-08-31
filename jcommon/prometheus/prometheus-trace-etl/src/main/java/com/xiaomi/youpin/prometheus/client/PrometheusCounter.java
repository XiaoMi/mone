package com.xiaomi.youpin.prometheus.client;

import com.xiaomi.youpin.prometheus.client.multi.MutiPrometheus;
import io.prometheus.client.Counter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @author zhangxiaowei
 */
@Slf4j
public class PrometheusCounter implements XmCounter {

    public String[] labelValues;

    public Counter myCounter;

    public String[] labelNames;

    private MutiPrometheus mutiPrometheus;

    public PrometheusCounter(Counter cb, String[] lns, String[] lvs, MutiPrometheus mutiPrometheus) {
        this.myCounter = cb;
        this.labelNames = lns;
        this.labelValues = lvs;
        this.mutiPrometheus = mutiPrometheus;
    }

    public PrometheusCounter() {

    }

    @Override
    public XmCounter with(String... labelValues) {
        try {
            if (this.labelNames.length != labelValues.length) {
                log.warn("Incorrect numbers of labels : " + myCounter.describe().get(0).name + " labelName: " + this.labelNames.length + " labelValues: " + labelValues.length + "{} {}", Arrays.toString(this.labelNames), Arrays.toString(labelValues));
                return new PrometheusCounter();
            }
            return this;
        } catch (Exception e) {
            log.warn("prometheus counter with error",e);
            return null;
        }
    }

    @Override
    public void add(double delta, String... labelValues) {
        try {
            this.myCounter.labels(labelValues).inc(delta);
        } catch (Exception e) {
            log.warn("prometheus counter add error",e);
        }
    }

    @Override
    public void add(String service, double delta, String... labelValues) {

    }
}
