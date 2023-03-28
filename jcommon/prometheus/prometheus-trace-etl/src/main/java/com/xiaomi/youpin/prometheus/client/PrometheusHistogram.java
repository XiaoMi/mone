package com.xiaomi.youpin.prometheus.client;

import io.prometheus.client.Histogram;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhangxiaowei
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
        /*String traceId = MDC.get("tid");
        if (StringUtils.isEmpty(traceId)) {
            traceId = "no traceId";
        }*/
        try {
            if (this.labelNames.length != labelValues.length) {
                log.warn("Incorrect numbers of labels : " + myHistogram.describe().get(0).name + " labelName: " + this.labelNames.length + " labelValues: " + this.labelValues.length);
                return new PrometheusHistogram();
            }
            return this;
        } catch (Exception e) {
            log.warn("prometheus histogram with error",e);
            return null;
        }
    }

    @Override
    public void observe(double delta,String... labelValue) {
        this.observe(Prometheus.constLabels.get(Metrics.SERVICE), delta, labelValue);
    }

    @Override
    public void observe(String service, double delta, String... labelValue) {
        try {
            List<String> mylist = new ArrayList<>(Arrays.asList(labelValue));
//            mylist.add(service);
            String[] finalValue = mylist.toArray(new String[mylist.size()]);
            this.myHistogram.labels(finalValue).observe(delta);
        } catch (Exception e) {
            log.warn("prometheus histogram observe error",e);
        }
    }
}
