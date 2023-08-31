package com.xiaomi.youpin.prometheus.all.client;

import io.prometheus.client.Histogram;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhangxiaowei6
 */
@Slf4j
public class PrometheusHistogram implements XmHistogram {

    public Histogram myHistogram;
    public String[] labelNames;
    public String[] labelValues;

    public PrometheusHistogram(Histogram cb, String[] lns, String[] lvs) {
        this.myHistogram = cb;
        this.labelNames = lns;
        this.labelValues = lvs;
    }

    public PrometheusHistogram() {

    }

    @Override
    public XmHistogram with(String... labelValues) {
        try {
            if (this.labelNames.length != labelValues.length) {
                log.warn("Incorrect numbers of labels : " + myHistogram.describe().get(0).name + " labelName: " + this.labelNames.length + " labelValues: " + this.labelValues.length);
                return new PrometheusHistogram();
            }
            return this;
        } catch (Throwable throwable) {
            log.warn(throwable.getMessage());
            return null;
        }
    }

    @Override
    public void observe(double delta,String... labelValue) {
        try {
            List<String> mylist = new ArrayList<>(Arrays.asList(labelValue));
            mylist.add(Prometheus.constLabels.get(Metrics.SERVICE));
            String[] finalValue = mylist.toArray(new String[mylist.size()]);
            this.myHistogram.labels(finalValue).observe(delta);
        } catch (Throwable throwable) {
            log.warn(throwable.getMessage());
        }
    }
}
