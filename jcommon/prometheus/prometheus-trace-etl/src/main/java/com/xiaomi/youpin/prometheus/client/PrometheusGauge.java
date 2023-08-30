package com.xiaomi.youpin.prometheus.client;

import com.xiaomi.youpin.prometheus.client.multi.MutiMetrics;
import com.xiaomi.youpin.prometheus.client.multi.MutiPrometheus;
import io.prometheus.client.Gauge;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhangxiaowei
 */
@Slf4j
public class PrometheusGauge implements XmGauge {

    public Gauge myGauge;
    public String[] labelNames;
    public String[] labelValues;
    private MutiPrometheus mutiPrometheus;

    public PrometheusGauge() {

    }

    @Override
    public void set(double delta,String ...labelValues) {
        try {
            List<String> mylist = new ArrayList<>(Arrays.asList(labelValues));
            mylist.add(mutiPrometheus.getConstLabels().get(MutiMetrics.SERVICE));
            String[] finalValue = mylist.toArray(new String[mylist.size()]);
            this.myGauge.labels(finalValue).set(delta);
        } catch (Throwable throwable) {
            //log.warn(throwable.getMessage());
        }
    }

    public PrometheusGauge(Gauge cb, String[] lns, String[] lvs, MutiPrometheus mutiPrometheus) {
        this.myGauge = cb;
        this.labelNames = lns;
        this.labelValues = lvs;
        this.mutiPrometheus = mutiPrometheus;
    }

    @Override
    public XmGauge with(String... labelValue) {
        /*String traceId = MDC.get("tid");
        if (StringUtils.isEmpty(traceId)) {
            traceId = "no traceId";
        }*/
        try {
            if (this.labelNames.length != labelValue.length) {
                log.warn("Incorrect numbers of labels : " + myGauge.describe().get(0).name);
                return new PrometheusGauge();
            }
            return this;
        } catch (Exception e) {
            log.warn("prometheus gauge with error",e);
            return null;
        }
    }

    @Override
    public void add(double delta, String... labelValues) {
        try {
            this.myGauge.labels(labelValues).inc(delta);
        } catch (Exception e) {
            log.warn("prometheus gauge add error",e);
        }
    }

    @Override
    public void add(String service, double delta,String... labelValues) {

    }
}
