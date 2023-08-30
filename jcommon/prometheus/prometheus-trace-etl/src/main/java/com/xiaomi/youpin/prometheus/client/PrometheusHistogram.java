package com.xiaomi.youpin.prometheus.client;

import com.xiaomi.youpin.prometheus.client.multi.MutiPrometheus;
import io.prometheus.client.Histogram;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangxiaowei
 */
@Slf4j
public class PrometheusHistogram implements XmHistogram {

    public Histogram myHistogram;
    public String[] labelNames;
    public String[] labelValues;
    private MutiPrometheus mutiPrometheus;

    public PrometheusHistogram(Histogram cb, String[] lns, String[] lvs, MutiPrometheus mutiPrometheus) {
        this.myHistogram = cb;
        this.labelNames = lns;
        this.labelValues = lvs;
        this.mutiPrometheus = mutiPrometheus;
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
    public void observe(double delta,String... labelValues) {
        try {
            this.myHistogram.labels(labelValues).observe(delta);
        } catch (Exception e) {
            log.warn("prometheus histogram observe error",e);
        }
    }

    @Override
    public void observe(String service, double delta, String... labelValue) {

    }
}
